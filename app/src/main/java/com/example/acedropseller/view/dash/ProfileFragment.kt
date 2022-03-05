package com.example.acedropseller.view.dash

import android.app.Activity
import android.app.AlertDialog
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentProfileBinding
import com.example.acedropseller.model.Message
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.Datastore.Companion.ADDRESS_KEY
import com.example.acedropseller.repository.Datastore.Companion.DESC_KEY
import com.example.acedropseller.repository.Datastore.Companion.EMAIL_KEY
import com.example.acedropseller.repository.Datastore.Companion.IMAGE_URL
import com.example.acedropseller.repository.Datastore.Companion.NAME_KEY
import com.example.acedropseller.repository.Datastore.Companion.NO_OF_MEMBERS
import com.example.acedropseller.repository.Datastore.Companion.PHN_KEY
import com.example.acedropseller.repository.Datastore.Companion.SHOP_NAME_KEY
import com.example.acedropseller.repository.auth.SignOutRepository
import com.example.acedropseller.utill.ProgressDialog
import com.example.acedropseller.utill.generateToken
import com.example.acedropseller.viewmodel.ProfileViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentProfileBinding? = null
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private val binding get() = _binding!!
    lateinit var signOutRepository: SignOutRepository
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    lateinit var datastore: Datastore
    private val IMAGE_REQUEST = 101
    private var filePath: Uri? = null
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        datastore = Datastore(requireContext())

        dialog = ProgressDialog.progressDialog(requireContext())

        lifecycleScope.launch {
            getShopDetails()
        }

        binding.uploadImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                IMAGE_REQUEST
            )
        }

        binding.signOutBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            signOutRepository = SignOutRepository()
            signOutRepository.let { it ->
                lifecycleScope.launch {
                    it.signOut(datastore.getUserDetails(Datastore.REF_TOKEN_KEY)!!)
                }
                it.message.observe(viewLifecycleOwner) {
                    lifecycleScope.launch {
                        datastore.changeLoginState(false)
                        signout()
                        activity?.finish()
                        findNavController().navigate(R.id.action_profileFragment_to_authActivity)
                    }
                }

                it.errorMessage.observe(viewLifecycleOwner) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.changePassButton.setOnClickListener(this)
        binding.editProfileBtn.setOnClickListener(this)

        profileViewModel.shopDetails?.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> lifecycleScope.launch {
                    binding.shopName.text = datastore.getUserDetails(SHOP_NAME_KEY)
                    binding.shopDesc.text = datastore.getUserDetails(DESC_KEY)
                    binding.ownerName.text = datastore.getUserDetails(NAME_KEY)
                    binding.phoneNumber.text = datastore.getUserDetails(PHN_KEY)
                    binding.email.text = datastore.getUserDetails(EMAIL_KEY)
                    binding.coverImageButton.load(datastore.getUserDetails(IMAGE_URL)) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                    }
                }
            }
        }

    }


    private suspend fun getShopDetails() {
        val datastore = Datastore(requireContext())
        profileViewModel.getShopDetails(
            datastore.getUserDetails(Datastore.ID)!!.toInt(),
            requireContext()
        )
            ?.observe(requireActivity()) {
                when (it) {
                    is ApiResponse.Success -> {
                        lifecycleScope.launch {
                            datastore.saveUserDetails(SHOP_NAME_KEY, it.data?.shopName)
                            datastore.saveUserDetails(DESC_KEY, it.data?.description)
                            datastore.saveUserDetails(PHN_KEY, it.data?.phno)
                            datastore.saveUserDetails(ADDRESS_KEY, it.data?.address)
                            datastore.saveUserDetails(NO_OF_MEMBERS, it.data?.noOfMembers.toString())
                            if (it.data?.imgUrls.isNullOrEmpty()) {
                                datastore.saveUserDetails(
                                    IMAGE_URL,
                                    getString(R.string.shop_default_image)
                                )
                            } else datastore.saveUserDetails(
                                IMAGE_URL,
                                it.data?.imgUrls?.get(0)?.imageUrl
                            )
                        }
                    }
                }
            }
    }

    private fun uploadCoverPic() {
        dialog.show()
        binding.uploadImageButton.isEnabled = false

        val photoRef =
            FirebaseStorage.getInstance().reference.child("Shop/${filePath?.lastPathSegment}")
        photoRef.putFile(filePath!!).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                lifecycleScope.launch {
                    uploadCoverImage(image = uri.toString())
                }
                dialog.cancel()
                binding.uploadImageButton.isEnabled = true
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }

        dialog.cancel()
        binding.uploadImageButton.isEnabled = true
    }

    private suspend fun uploadCoverImage(image: String) {
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
                                binding.uploadImageButton.isEnabled = true
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
                                uploadCoverImage(image)
                            }
                        }
                        else -> {
                            dialog.cancel()
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
                binding.coverImageButton.setImageBitmap(bitmap)
                uploadDialog()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Cover Picture")
            .setMessage("Are you sure you want to upload this cover picture")
            .setPositiveButton("Upload") { dialog, id ->
                uploadCoverPic()
            }
            .setNeutralButton("Cancel") { dialog, id -> }
        val exit = builder.create()
        exit.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signout(): Boolean {
        var result = false
        googleSignInClient.signOut().addOnCompleteListener {
            result = true
        }.addOnCanceledListener {
            result = false
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.change_pass_Button -> findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
            R.id.edit_profile_btn -> findNavController().navigate(R.id.action_profileFragment_to_updateDetails)
        }
    }

}