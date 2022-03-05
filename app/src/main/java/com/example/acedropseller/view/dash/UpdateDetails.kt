package com.example.acedropseller.view.dash

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentUpdateDetailsBinding
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.utill.ProgressDialog
import com.example.acedropseller.view.auth.AuthActivity
import com.example.acedropseller.viewmodel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class UpdateDetails : Fragment() {

    private var _binding: FragmentUpdateDetailsBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by activityViewModels()
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog = ProgressDialog.progressDialog(requireContext())

        val datastore = Datastore(requireContext())
        lifecycleScope.launch {
            binding.shopName.setText(datastore.getUserDetails(Datastore.SHOP_NAME_KEY))
            binding.description.setText(datastore.getUserDetails(Datastore.DESC_KEY))
            binding.phnNumber.setText(datastore.getUserDetails(Datastore.PHN_KEY))
            binding.address.setText(datastore.getUserDetails(Datastore.ADDRESS_KEY))
            binding.noOfMember.setText(datastore.getUserDetails(Datastore.NO_OF_MEMBERS))
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {
            val shopName = binding.shopName.text.toString().trim()
            val member = binding.noOfMember.text.toString().trim()
            val phnNumber = binding.phnNumber.text.toString().trim()
            val desc = binding.description.text.toString().trim()
            val address = binding.address.text.toString().trim()
            helper()
            if (isValid(shopName, member, desc, address, phnNumber)) {
                profileViewModel.updateShopDetails(
                    shopName,
                    phnNumber,
                    member,
                    desc,
                    address,
                    requireContext()
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiResponse.Success -> {
                            dialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                "Shop details updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ApiResponse.Loading -> dialog.show()

                        is ApiResponse.TokenExpire -> {
                            dialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                "Refresh Token Expire login again !!!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            startActivity(Intent(activity, AuthActivity::class.java))
                        }
                        is ApiResponse.Error -> {
                            dialog.cancel()
                            Toast.makeText(
                                requireContext(),
                                it.errorMessage ?: "Something went wrong!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
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
            phnNumber.length != 10 -> {
                binding.phnLayout.helperText = "Enter correct phone number"
                false
            }
            member.isEmpty() -> {
                binding.memberLayout.helperText = "Enter number of members"
                false
            }
            else -> true
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
    }
}