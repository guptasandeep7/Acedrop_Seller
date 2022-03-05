package com.example.acedropseller.view.auth

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.acedropseller.model.Message
import com.example.acedropseller.model.ShopDetails
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.utill.ProgressDialog
import com.example.acedropseller.utill.generateToken
import com.example.acedropseller.view.auth.BusinessDetailsFragment.Companion.businessDetails
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class PersonalDetailsFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    View.OnClickListener {

    lateinit var datastore: Datastore
    private var _binding: FragmentPersonalDetailsBinding? = null
    private val binding get() = _binding!!
    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0
    private var dob: String? = null
    lateinit var dialog:Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog = ProgressDialog.progressDialog(requireContext())

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
        binding.dobBtn.text = "${year}-${month + 1}-${day}"
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
            dob.isNullOrBlank() -> {
                Toast.makeText(requireContext(), "Please enter date of birth", Toast.LENGTH_SHORT).show()
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
                dialog.show()
                binding.nextBtn.isEnabled = false
                val phnNumber = binding.phoneNumber.text.toString().trim()
                val aadhaarNo = binding.aadharNumber.text.toString().trim()
                val fName = binding.fatherName.text.toString().trim()
                helper()
                if (isValid(phnNumber, aadhaarNo, fName)) {

                    lifecycleScope.launch {
                        createShop(
                            businessDetails.shopName!!,
                            phnNumber.toLong(),
                            businessDetails.member!!.toInt(),
                            businessDetails.desc!!,
                            businessDetails.address!!,
                            fName,
                            aadhaarNo,
                            dob!!,
                            context = requireContext()
                        )
                    }
                } else {
                    binding.nextBtn.isEnabled = true
                    dialog.cancel()
                }
            }
        }
    }

    private suspend fun createShop(
        shopName: String,
        phno: Long,
        noOfMembers: Int,
        desc: String,
        address: String,
        fName: String,
        aadhaarNo: String,
        dob: String,
        context: Context
    ) {
        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        Log.w("PERSONAL ACC TOKEN", "createShop: $token", )
        val request = ServiceBuilder.buildService(token)
        val call = request.createShop(
            ShopDetails(
                shopName = shopName,
                phno = phno,
                noOfMembers = noOfMembers,
                description = desc,
                address = address,
                fathersName = fName,
                aadhaarNo = aadhaarNo,
                dob = dob
            )
        )
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> {
                        dialog.cancel()
                        findNavController().navigate(R.id.action_personalDetails_to_aadharFragment)
                    }
                    response.code() == 404 -> errorMessage("Shop does not exists")

                    response.code() == 401 -> errorMessage("Aadhar number is invalid")

                    response.code() == 400 -> errorMessage("Shop already exists")

                    response.code() == 403 -> {
                        runBlocking {
                            generateToken(
                                token!!,
                                Datastore(context).getUserDetails(
                                    Datastore.REF_TOKEN_KEY
                                )!!, context
                            )
                            ShopDetails(
                                shopName = shopName,
                                phno = phno,
                                noOfMembers = noOfMembers,
                                description = desc,
                                address = address,
                                fathersName = fName,
                                aadhaarNo = aadhaarNo,
                                dob = dob
                            )
                        }
                    }
                    else -> errorMessage(response.code().toString())
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage(t.message.toString())
            }
        })

    }

    fun errorMessage(errorMessage: String) {
        dialog.cancel()
        Toast.makeText(this.context, errorMessage, Toast.LENGTH_SHORT).show()
        binding.nextBtn.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}