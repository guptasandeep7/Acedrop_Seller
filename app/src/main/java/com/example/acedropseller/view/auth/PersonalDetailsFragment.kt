package com.example.acedropseller.view.auth

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentPersonalDetailsBinding
import com.example.acedropseller.model.BusinessDetails
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.auth.ShopDetailsRepository
import kotlinx.coroutines.launch
import java.util.*


class PersonalDetailsFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    View.OnClickListener {

    lateinit var datastore: Datastore
    private var _binding: FragmentPersonalDetailsBinding? = null
    private val binding get() = _binding!!
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    lateinit var dob: String
    lateinit var shopDetailsRepository: ShopDetailsRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.dobBtn.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    DatePickerDialog(requireContext(), this, year, month, day)
                } else {
                    TODO("VERSION.SDK_INT < N")
                }
            datePickerDialog.show()
        }
        binding.nextBtn.setOnClickListener(this)
        return view
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        binding.dobBtn.setText("${year}-${month + 1}-${day}")
        dob = "${year}-${month + 1}-${day}"
    }

    private fun isValid(phnNumber: String, aadharNo: String, fName: String): Boolean {
        return when {
            phnNumber.length != 10 -> {
                binding.phoneLayout.helperText = "Enter valid phone number"
                false
            }
            aadharNo.length != 12 -> {
                binding.aadharLayout.helperText = "Enter valid aadhar number"
                false
            }
            fName.isBlank() -> {
                binding.fatherNameLayout.helperText = "Enter Father's name"
                false
            }
            else -> true
        }
    }

    private fun helper() = with(binding) {
        phoneLayout.helperText = ""
        aadharLayout.helperText = ""
        fatherNameLayout.helperText = ""
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_btn -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.nextBtn.isEnabled = false
                val phnNumber = binding.phoneNumber.text.toString().trim()
                val aadhaarNo = binding.aadharNumber.text.toString().trim()
                val fName = binding.fatherName.text.toString().trim()
                helper()
                if (isValid(phnNumber, aadhaarNo, fName)) {
                    val businessDetails =
                        arguments?.getSerializable("BusinessDetails") as BusinessDetails
                    shopDetailsRepository = ShopDetailsRepository()
                    shopDetailsRepository.createShop(
                        businessDetails.shopName!!,
                        phnNumber,
                        businessDetails.member!!,
                        businessDetails.desc!!,
                        businessDetails.address!!,
                        fName,
                        aadhaarNo,
                        dob
                    )
                    shopDetailsRepository.message.observe(viewLifecycleOwner, {
                        binding.progressBar.visibility = View.GONE
                        findNavController().navigate(R.id.action_personalDetails_to_aadharFragment)
                    })
                    shopDetailsRepository.errorMessage.observe(viewLifecycleOwner, {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                        binding.nextBtn.isEnabled = true
                    })
                } else {
                    binding.nextBtn.isEnabled = true
                    binding.progressBar.visibility = View.GONE

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}