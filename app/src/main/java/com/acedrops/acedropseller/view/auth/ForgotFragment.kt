package com.acedrops.acedropseller.view.auth

import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.FragmentForgotBinding
import com.acedrops.acedropseller.model.Message
import com.acedrops.acedropseller.model.UserData
import com.acedrops.acedropseller.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ForgotFragment : Fragment() {

    companion object {
        var forgot = false
    }

    private var _binding: FragmentForgotBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.nextBtn.setOnClickListener {
            val btn = binding.nextBtn
            btn.isEnabled = false
            val email = binding.email.text.toString().trim()
            binding.emailLayout.helperText = ""
            if (isValid(email)) {
                binding.progressBar.visibility = View.VISIBLE
                forgot(email)
            } else btn.isEnabled = true
        }
        return view
    }


    private fun forgot(email: String) {
        val request = ServiceBuilder.buildService(null)
        val call = request.forgotPass(UserData(email = email))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> {
                        SignupFragment.Email = email
                        forgot = true
                        findNavController().navigate(R.id.action_forgotFragment_to_otpFragment)
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

    private fun errorMessage(it: String) {
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
        binding.nextBtn.isEnabled = true
    }

    private fun isValid(email: String): Boolean {
        return when {
            email.isBlank() -> {
                binding.emailLayout.helperText = "Enter Email Address"
                false
            }
            !EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.helperText = "Enter valid Email Address"
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