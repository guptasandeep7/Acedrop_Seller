package com.example.acedropseller.view.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentAadharBinding
import com.example.acedropseller.model.Message
import com.example.acedropseller.model.ShopDetails
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.utill.generateToken
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AadharFragment : androidx.fragment.app.Fragment() {

    private var _binding: FragmentAadharBinding? = null
    private val binding get() = _binding!!
    private val FRONT_IMAGE_REQUEST = 101
    private val BACK_IMAGE_REQUEST = 202
    private var frontFilePath: Uri? = null
    private var backFilePath: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAadharBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.frontButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                FRONT_IMAGE_REQUEST
            )
        }

        binding.backButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                BACK_IMAGE_REQUEST
            )
        }

        binding.uploadBtn.setOnClickListener {
            binding.frontButton.isEnabled = false
            binding.backButton.isEnabled = false
            it.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            val images = arrayOf<String>()
            val photoRef =
                FirebaseStorage.getInstance().reference.child("Aadhar/${frontFilePath?.lastPathSegment}")
            photoRef.putFile(frontFilePath!!).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    images[0] = uri.toString()
                    val photoRe =
                        FirebaseStorage.getInstance().reference.child("Aadhar/${backFilePath?.lastPathSegment}")
                    photoRe.putFile(backFilePath!!).addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener { uri ->
                            images[1] = uri.toString()
                            lifecycleScope.launch {
                                uploadAadhar(images = images)
                            }
                            binding.progressBar.visibility = View.GONE
                            binding.uploadBtn.isEnabled = true
                            binding.frontButton.isEnabled = true
                            binding.backButton.isEnabled = true
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { it ->
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private suspend fun uploadAadhar(images: Array<String>) {
        val token = Datastore(requireContext()).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        ServiceBuilder.buildService(token = token).uploadAadhar(images = images)
            .enqueue(object : Callback<Message?> {
                override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                    when {
                        response.isSuccessful -> {
                            Toast.makeText(
                                requireContext(),
                                "Aadhar uploaded successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.progressBar.visibility = View.GONE
                            binding.uploadBtn.isEnabled = true
                            binding.frontButton.isEnabled = true
                            binding.backButton.isEnabled = true
                            findNavController().navigate(R.id.action_aadharFragment_to_sellerPhotoFragment)
                        }
                        response.code() == 403 -> {
                            runBlocking {
                                generateToken(
                                    token!!,
                                    Datastore(requireContext()).getUserDetails(
                                        Datastore.REF_TOKEN_KEY
                                    )!!, requireContext()
                                )
                                uploadAadhar(images)
                            }
                        }
                        else -> {
                            Toast.makeText(
                                requireContext(),
                                response.body()?.toString() ?: "Try Again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Message?>, t: Throwable) {
                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                }
            })
        binding.progressBar.visibility = View.GONE
        binding.uploadBtn.isEnabled = true
        binding.frontButton.isEnabled = true
        binding.backButton.isEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == FRONT_IMAGE_REQUEST || requestCode == BACK_IMAGE_REQUEST) && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            try {
                if (requestCode == FRONT_IMAGE_REQUEST) {
                    frontFilePath = data.data
                    val bitmap = getBitmap(requireContext().contentResolver, frontFilePath)
                    binding.frontButton.setImageBitmap(bitmap)
                } else {
                    backFilePath = data.data
                    val bitmap = getBitmap(requireContext().contentResolver, backFilePath)
                    binding.backButton.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}