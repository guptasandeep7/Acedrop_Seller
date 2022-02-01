package com.example.acedropseller.view.dash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentUploadProductBinding
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.view.auth.AuthActivity
import com.example.acedropseller.viewmodel.UploadProductViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class UploadProductFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentUploadProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var uploadProductViewModel: UploadProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUploadProductBinding.inflate(inflater, container, false)

        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE

        return binding.root
    }

//    private fun saveObserver() {

//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadProductViewModel =
            ViewModelProvider((context as FragmentActivity?)!!)[UploadProductViewModel::class.java]

        binding.viewmodel = uploadProductViewModel

        binding.backBtn.setOnClickListener(this)
        binding.addProductBtn.setOnClickListener {
            helper()
            if (validDetails()) {
                uploadProduct()
            }
        }
    }

    private fun uploadProduct() {
        uploadProductViewModel.uploadProduct(requireContext()).observe(viewLifecycleOwner, {
            when (it) {
                is ApiResponse.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Product Successfully added",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is ApiResponse.Loading -> binding.progressBar.visibility = View.VISIBLE

                is ApiResponse.TokenExpire -> {
                    startActivity(Intent(activity, AuthActivity::class.java))
                }
                is ApiResponse.Error -> Toast.makeText(
                    requireContext(),
                    it.errorMessage ?: "Something went wrong!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_btn -> findNavController().popBackStack()
        }
    }

    private fun helper() {
        with(binding) {
            productNameLayout.helperText = ""
            productDescLayout.helperText = ""
            quantityLayout.helperText = ""
            priceLayout.helperText = ""
        }
    }

    private fun validDetails(): Boolean {
        with(binding) {
            when {
                uploadProductViewModel.productName.value.isNullOrBlank() -> productNameLayout.helperText =
                    "Product Name cannot be blank"
                uploadProductViewModel.productDesc.value.isNullOrBlank() -> productDescLayout.helperText =
                    "Product Description cannot be blank"
                uploadProductViewModel.quantity.value!! < 1 -> quantityLayout.helperText =
                    "Quantity cannot be less than 1"
                uploadProductViewModel.basePrice.value!! < 1 -> priceLayout.helperText =
                    "Price cannot be less than 1Rs"
                else -> return true
            }
        }
        return false
    }

}