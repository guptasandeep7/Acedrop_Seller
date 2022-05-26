package com.acedrops.acedropseller.view.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.FragmentBusinessDetailsBinding
import com.acedrops.acedropseller.model.BusinessDetails
import com.acedrops.acedropseller.repository.Datastore
import kotlinx.coroutines.launch

class BusinessDetailsFragment : Fragment() {

    private var _binding: FragmentBusinessDetailsBinding? = null
    private val binding get() = _binding!!

    companion object{
        lateinit var businessDetails:BusinessDetails
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBusinessDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.nextBtn.setOnClickListener {
            val shopName = binding.shopName.text.toString().trim()
            val member = binding.noOfMember.text.toString().trim()
            val desc = binding.description.text.toString().trim()
            val address = binding.address.text.toString().trim()
            helper()
            if (isValid(shopName, member, desc, address)) {
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