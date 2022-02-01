package com.example.acedropseller.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentBusinessDetailsBinding
import com.example.acedropseller.model.BusinessDetails

class BusinessDetailsFragment : Fragment() {

    private var _binding: FragmentBusinessDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBusinessDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.nextBtn.setOnClickListener {
            val shopName = binding.shopName.text.toString().trim()
            val member = binding.shopName.text.toString().trim()
            val desc = binding.description.text.toString().trim()
            val address = binding.address.text.toString().trim()
            helper()
            if (isValid(shopName, member, desc, address)) {
                val data = BusinessDetails(shopName, member, desc, address)
                val bundle = bundleOf("BusinessDetails" to data)
                view.findNavController()
                    .navigate(R.id.action_businessDetailsFragment_to_personalDetails, bundle)
            }
        }
        return view
    }

    private fun helper() = with(binding) {
        shopNameLayout.helperText = ""
        memberLayout.helperText = ""
        descriptionLayout.helperText = ""
        addressLayout.helperText = ""
    }

    private fun isValid(shop: String, member: String, desc: String, address: String): Boolean {
        return when {
            shop.isBlank() -> {
                binding.shopNameLayout.helperText = "Enter shop name"
                false
            }
            desc.isBlank() -> {
                binding.descriptionLayout.helperText = "Enter description"
                false
            }
            address.isBlank() -> {
                binding.addressLayout.helperText = "Enter address"
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}