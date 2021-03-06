package com.acedrops.acedropseller.view.auth

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.FragmentShopPhotoBinding
import com.acedrops.acedropseller.model.Message
import com.acedrops.acedropseller.network.ServiceBuilder
import com.acedrops.acedropseller.repository.Datastore
import com.acedrops.acedropseller.utill.ProgressDialog
import com.acedrops.acedropseller.utill.generateToken
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ShopPhotoFragment : Fragment() {
    private var _binding: FragmentShopPhotoBinding? = null
    private val binding get() = _binding!!
    private val IMAGE_REQUEST = 101
    private var filePath: Uri? = null
    lateinit var datastore: Datastore
    lateinit var dialog:Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentShopPhotoBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog = ProgressDialog.progressDialog(requireContext())

        binding.uploadImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                IMAGE_REQUEST
            )
        }

        binding.uploadBtn.setOnClickListener {
            dialog.show()
            it.isEnabled = false
            binding.uploadImageButton.isEnabled = false
            val photoRef =
                FirebaseStorage.getInstance().reference.child("Shop/${filePath?.lastPathSegment}")
            photoRef.putFile(filePath!!).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    lifecycleScope.launch {
                        uploadSellerImage(image = uri.toString())
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                dialog.cancel()
                binding.uploadBtn.isEnabled = true
                binding.uploadImageButton.isEnabled = true
            }
        }
        return view
    }

    private suspend fun uploadSellerImage(image: String) {
        val token = Datastore(requireContext()).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        ServiceBuilder.buildService(token = token).uploadShopPhoto(image = image)
            .enqueue(object : Callback<Message?> {
                override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                    when {
                        response.isSuccessful -> {
                            Toast.makeText(
                                requireContext(),
                                "Shop photo uploaded successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            datastore = Datastore(requireContext())
                            lifecycleScope.launch {
                                datastore.changeLoginState(true)
                                dialog.cancel()
                                binding.uploadBtn.isEnabled = true
                                binding.uploadImageButton.isEnabled = true
                                activity?.finish()
                                findNavController().navigate(R.id.action_shopPhotoFragment_to_dashboardActivity)
                            }
                        }
                        response.code() == 403 -> {
                            runBlocking {
                                generateToken(
                                    token!!,
                                    Datastore(requireContext()).getUserDetails(
                                        Datastore.REF_TOKEN_KEY
                                    )!!, requireContext()
                                )
                                uploadSellerImage(image)
                            }
                        }
                        else -> {
                            dialog.cancel()
                            binding.uploadBtn.isEnabled = true
                            binding.uploadImageButton.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                response.body()?.toString() ?: response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Message?>, t: Throwable) {
                    dialog.cancel()
                    binding.uploadBtn.isEnabled = true
                    binding.uploadImageButton.isEnabled = true
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            try {
                filePath = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    filePath
                )
                binding.uploadImageButton.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            Datastore(requireContext()).changeLoginState(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}