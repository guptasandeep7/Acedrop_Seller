package com.example.acedropseller.view.dash

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentUploadProductBinding
import com.example.acedropseller.model.ProductData
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.utill.ProgressDialog
import com.example.acedropseller.view.auth.AuthActivity
import com.example.acedropseller.viewmodel.UploadProductViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException

class UploadProductFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentUploadProductBinding? = null
    private val binding get() = _binding!!
    private val uploadProductViewModel: UploadProductViewModel by activityViewModels()
    private lateinit var categories: MutableList<String>
    private val IMAGE_REQUEST1 = 101
    private val IMAGE_REQUEST2 = 102
    private val IMAGE_REQUEST3 = 103
    private val IMAGE_REQUEST4 = 104
    private var imageUriList = arrayOfNulls<Uri>(4)
    var k = 0
    lateinit var dialog: Dialog
    private var lastFragment: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ProgressDialog.progressDialog(requireContext())

        binding.viewmodel = uploadProductViewModel

        categories = resources.getStringArray(R.array.category_array).toMutableList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        binding.category.adapter = adapter

        binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                uploadProductViewModel.category = categories[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.backBtn.setOnClickListener(this)
        binding.addProductBtn.setOnClickListener(this)
        binding.uploadImageButton.setOnClickListener(this)
        binding.uploadImage2Button.setOnClickListener(this)
        binding.uploadImage3Button.setOnClickListener(this)
        binding.uploadImage4Button.setOnClickListener(this)
    }

    private fun uploadProduct() {
        uploadProductViewModel.uploadProduct(requireContext()).observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Product Successfully added",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ApiResponse.Loading -> dialog.show()

                is ApiResponse.TokenExpire -> {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Refresh Token Expire login again !!!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    startActivity(Intent(activity, AuthActivity::class.java))
                }
                is ApiResponse.Error -> {
                    dialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        it.errorMessage ?: "Something went wrong!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> findNavController().popBackStack()
            R.id.upload_image_Button -> selectImage(IMAGE_REQUEST1)
            R.id.upload_image2_Button -> selectImage(IMAGE_REQUEST2)
            R.id.upload_image3_Button -> selectImage(IMAGE_REQUEST3)
            R.id.upload_image4_Button -> selectImage(IMAGE_REQUEST4)
            R.id.add_product_btn -> {
                helper()
                if (validDetails()) {
                    k = 0
                    dialog.show()
                    if (lastFragment != null && imageUriList.isNullOrEmpty()) {
                        uploadProductViewModel.pastImgUrls.forEach {
                            uploadProductViewModel.images.add(it.imageUrl)
                        }
                        uploadProduct()
                    } else {
                        if (imageUriList.filterNotNull().isEmpty())
                            Toast.makeText(
                                requireContext(),
                                "Please select image",
                                Toast.LENGTH_SHORT
                            ).show()
                        else {
                            imageUriList = imageUriList.filterNotNull().toTypedArray()
                            imageUriList[k]?.let { uploadImagesToFirebase(it) }
                        }
                    }
                }
            }
        }
    }

    private fun uploadImagesToFirebase(filePath: Uri) {
        val photoRef =
            FirebaseStorage.getInstance().reference.child("Products/${filePath.lastPathSegment}")
        photoRef.putFile(filePath).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { uri ->
                uploadProductViewModel.images.add(uri.toString())
                Log.w("Upload product fragment", "uploadImagesToFirebase: $uri")
                k++
                if (k == imageUriList.size) {
                    uploadProduct()
                } else {
                    uploadImagesToFirebase(imageUriList[k]!!)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun selectImage(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            requestCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode in 101..104 && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            try {
                when (requestCode) {
                    101 -> {
                        imageUriList[0] = data.data
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUriList[0]
                        )
                        binding.uploadImageButton.setImageBitmap(bitmap)
                        binding.uploadImageButton.scaleType = ImageView.ScaleType.FIT_CENTER
                    }

                    102 -> {
                        imageUriList[1] = data.data
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUriList[1]
                        )
                        binding.uploadImage2Button.setImageBitmap(bitmap)
                        binding.uploadImage2Button.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                    103 -> {
                        imageUriList[2] = data.data
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUriList[2]
                        )
                        binding.uploadImage3Button.setImageBitmap(bitmap)
                        binding.uploadImage3Button.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                    104 -> {
                        imageUriList[3] = data.data
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireContext().contentResolver,
                            imageUriList[3]
                        )
                        binding.uploadImage4Button.setImageBitmap(bitmap)
                        binding.uploadImage4Button.scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun helper() {
        with(binding) {
            productNameLayout.helperText = ""
            shortDescLayout.helperText = ""
            productDescLayout.helperText = ""
            priceLayout.helperText = ""
            offerLayout.helperText = ""
            quantityLayout.helperText = ""
        }
    }

    private fun validDetails(): Boolean {
        with(binding) {
            when {
                uploadProductViewModel.productName.isNullOrBlank() -> productNameLayout.helperText =
                    "Product name cannot be blank"
                uploadProductViewModel.shortDesc.isNullOrBlank() -> shortDescLayout.helperText =
                    "Product short description cannot be blank"
                uploadProductViewModel.productDesc.isNullOrBlank() -> productDescLayout.helperText =
                    "Product description cannot be blank"
                uploadProductViewModel.quantity.isNullOrBlank() || uploadProductViewModel.quantity?.toInt()!! < 1 -> quantityLayout.helperText =
                    "Quantity cannot be less than 1"
                uploadProductViewModel.basePrice.isNullOrBlank() || uploadProductViewModel.basePrice?.toInt()!! < 1 -> priceLayout.helperText =
                    "Price cannot be less than 1Rs"
                uploadProductViewModel.offer.isNullOrBlank() || uploadProductViewModel.offer?.toInt()!! < 0 || uploadProductViewModel.offer?.toInt()!! > 99 -> offerLayout.helperText =
                    "Discount can only range between 0 - 99%"

                uploadProductViewModel.category!! == "Category" -> Toast.makeText(
                    requireContext(),
                    "Please select one category",
                    Toast.LENGTH_SHORT
                ).show()

                else -> return true
            }
        }
        return false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUploadProductBinding.inflate(inflater, container, false)

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE

        arguments?.let {
            val product = it.getSerializable("ProductDetails") as ProductData
            lastFragment = "itemFragment"
            uploadProductViewModel.productName = product.title
            uploadProductViewModel.productDesc = product.description
            uploadProductViewModel.shortDesc = product.shortDescription
            uploadProductViewModel.quantity = product.stock.toString()
            uploadProductViewModel.basePrice = product.basePrice.toString()
            uploadProductViewModel.discPrice = product.discountedPrice
            uploadProductViewModel.offer = product.offers
            uploadProductViewModel.pastImgUrls = product.imgUrls
            uploadProductViewModel.prodId = product.id
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
    }
}