package com.acedrops.acedropseller.view.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.FragmentOtpBinding
import com.acedrops.acedropseller.model.Message
import com.acedrops.acedropseller.model.UserData
import com.acedrops.acedropseller.network.ServiceBuilder
import com.acedrops.acedropseller.repository.Datastore
import com.acedrops.acedropseller.view.auth.ForgotFragment.Companion.forgot
import com.acedrops.acedropseller.view.auth.SignupFragment.Companion.Email
import com.acedrops.acedropseller.view.auth.SignupFragment.Companion.Name
import com.acedrops.acedropseller.view.auth.SignupFragment.Companion.Pass
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var timerCountDown: CountDownTimer
    lateinit var datastore: Datastore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.nextBtn.setOnClickListener(this)
        binding.resendOtp.setOnClickListener(this)

        timerCountDown = object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.visibility = View.VISIBLE
                binding.resendOtp.isEnabled = false
                binding.timer.text = when {
                    millisUntilFinished / 1000 > 9 -> "00:${(millisUntilFinished / 1000)}"
                    else -> "00:0${(millisUntilFinished / 1000)}"
                }

            }

            override fun onFinish() {
                binding.timer.visibility = View.GONE
                binding.resendOtp.isEnabled = true
            }
        }.start()

        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.next_btn -> {
                next()
            }
            R.id.resend_otp -> {
                binding.resendOtp.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                if (forgot) resendForgotOtp(Email)
                else resendOtp(email = Email, name = Name)
            }
        }
    }

    private fun resendForgotOtp(email: String) {
        val request = ServiceBuilder.buildService(null)
        val call = request.forgotPass(UserData(email = email))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> {
                        Toast.makeText(
                            requireContext(),
                            "OTP resend successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.resendOtp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        timerCountDown.start()
                    }
                    response.code() == 422 -> errorMessage("Enter correct email id")
                    response.code() == 404 -> errorMessage("Email id is not registered")
                    else -> errorMessage("Incorrect Email Id")
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage(t.message.toString())
            }
        })
    }

    private fun next() {
        val progressBar = binding.progressBar
        val otp = binding.otp.text.toString().trim()
        val btn = binding.nextBtn
        btn.isEnabled = false
        if (otp.isNotBlank()) {
            progressBar.visibility = View.VISIBLE

            if (forgot)
                forgotOtp(email = Email, otp)

            else
                otp(email = Email, pass = Pass, name = Name, otp = otp)
        }
        else {
            btn.isEnabled = true
            binding.otpLayout.helperText = "Enter OTP"
        }
    }

    private fun otp(email: String, pass: String, name: String, otp: String) {
        val request = ServiceBuilder.buildService(null)
        val call = request.signUpVerify(
            UserData(
                email = email,
                password = pass,
                name = name,
                otp = otp,
                isShop = true
            )
        )
        call.enqueue(object : Callback<UserData?> {
            override fun onResponse(call: Call<UserData?>, response: Response<UserData?>) {
                when {
                    response.isSuccessful -> {
                        binding.progressBar.visibility = View.GONE
                        timerCountDown.cancel()
                        datastore = Datastore(requireContext())
                        lifecycleScope.launch {
                            datastore.saveToDatastore(
                                response.body()!!,
                                requireContext()
                            )
                            findNavController().navigate(R.id.action_otpFragment_to_businessDetailsFragment)
                        }
                    }
                    response.code() == 422 -> errorMessage("OTP is incorrect")
                    response.code() == 400 -> errorMessage("This email is already registered")
                    response.code() == 404 -> errorMessage("Wrong OTP")
                    response.code() == 401 -> errorMessage("Wrong otp")
                    else -> errorMessage("Something went wrong! Try again")
                }
            }

            override fun onFailure(call: Call<UserData?>, t: Throwable) {
                errorMessage(t.message)
            }
        })
    }


    private fun resendOtp(email: String, name: String) {
        val request = ServiceBuilder.buildService(null)
        val call = request.signup(UserData(email = email, name = name))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> {
                        Toast.makeText(
                            requireContext(),
                            "OTP resend successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.resendOtp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        timerCountDown.start()
                    }
                    response.code() == 422 -> {
                        binding.resendOtp.isEnabled = true
                        errorMessage("Enter Correct details")
                    }
                    response.code() == 400 -> {
                        binding.resendOtp.isEnabled = true
                        errorMessage("This Email is already registered")
                    }
                    else -> {
                        binding.resendOtp.isEnabled = true
                        errorMessage(
                            response.body()?.message ?: "Something went wrong! Try again"
                        )
                    }
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                binding.resendOtp.isEnabled = true
                errorMessage(t.message)
            }
        })
    }

    private fun forgotOtp(email: String, otp: String) {

        val request = ServiceBuilder.buildService(null)
        val call = request.forgotVerify(UserData(email = email, otp = otp))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> {
                        timerCountDown.cancel()
                        findNavController().navigate(R.id.action_otpFragment_to_passwordFragment)
                    }
                    response.code() == 422 -> errorMessage("validation error")
                    response.code() == 401 -> errorMessage("Wrong OTP")
                    else -> errorMessage(response.body()?.message ?: "try again")
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage(t.message)

            }
        })
    }

    private fun errorMessage(errorMessage: String?) {
        Toast.makeText(this.activity, errorMessage, Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
        binding.nextBtn.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerCountDown.cancel()
        _binding = null
    }
}