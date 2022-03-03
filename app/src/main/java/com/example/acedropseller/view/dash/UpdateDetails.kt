package com.example.acedropseller.view.dash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentUpdateDetailsBinding
import com.example.acedropseller.model.BusinessDetails

class UpdateDetails: Fragment() {

    private var _binding: FragmentUpdateDetailsBinding? = null
    private val binding get() = _binding!!

    companion object{
        lateinit var businessDetails: BusinessDetails
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.nextBtn.setOnClickListener {
            val shopName = binding.shopName.text.toString().trim()
            val member = binding.noOfMember.text.toString().trim()
            val phnNumber = binding.phnNumber.text.toString().trim()
            val desc = binding.description.text.toString().trim()
            val address = binding.address.text.toString().trim()
            helper()
            if (isValid(shopName, member, desc, address,phnNumber)) {
                businessDetails = BusinessDetails(shopName, member, desc, address)
                view.findNavController()
                    .navigate(R.id.action_businessDetailsFragment_to_personalDetails)
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

    private fun isValid(
        shop: String,
        member: String,
        desc: String,
        address: String,
        phnNumber: String
    ): Boolean {
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
            phnNumber.length!=10 -> {
                binding.phnLayout.helperText = "Enter correct phone number"
                false
            }
            member.length!=10 -> {
                binding.phnLayout.helperText = "Enter correct phone number"
                false
            }
            else -> true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}