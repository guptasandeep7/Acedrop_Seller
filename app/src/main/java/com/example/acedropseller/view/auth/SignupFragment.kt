package com.example.acedropseller.view.auth

import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentSignupBinding
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.auth.SignupRepository
import com.example.acedropseller.utill.validPass

class SignupFragment : Fragment(), View.OnClickListener {
    companion object {
        lateinit var Email: String
        lateinit var Pass: String
        lateinit var Name: String
    }

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val signupRepository = SignupRepository()
    lateinit var datastore: Datastore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val view = binding.root
        datastore = Datastore(requireContext())
        binding.signupBtn.setOnClickListener(this)
        binding.signupToSignin.setOnClickListener(this)
        return view
    }

    private fun isValid(email: String, name: String, pass: String, confirmPass: String): Boolean {
        return when {
            name.isBlank() -> {
                binding.nameLayout.helperText = "Enter Name"
                false
            }
            email.isBlank() -> {
                binding.emailLayout.helperText = "Enter Email Id"
                false
            }
            !EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.helperText = "Enter valid Email Id"
                false
            }
            validPass(pass) != null -> {
                binding.passLayout.helperText = validPass(pass)
                false
            }
            confirmPass.isBlank() -> {
                binding.confPassLayout.helperText = "Enter Confirm Password"
                false
            }
            confirmPass != pass -> {
                binding.confPassLayout.helperText = "Confirm Password doesn't match"
                false
            }
            else -> true
        }
    }

    private fun helper() = with(binding) {
        emailLayout.helperText = ""
        nameLayout.helperText = ""
        passLayout.helperText = ""
        confPassLayout.helperText = ""
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.signup_to_signin -> findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            R.id.signup_btn -> signUp()
        }
    }

    private fun signUp() {
        val btn = binding.signupBtn
        val progressBar = binding.progressBar
        val email = binding.email.text.toString().trim()
        val name = binding.name.text.toString().trim()
        val pass = binding.pass.text.toString().trim()
        val confirmPass = binding.confirmPass.text.toString().trim()
        btn.isEnabled = false
        helper()
        if (isValid(email, name, pass, confirmPass)) {
            progressBar.visibility = View.VISIBLE
            signupRepository.signUp(email = email, name = name)
            signupRepository.message.observe(this, {
                progressBar.visibility = View.GONE
                Email = email
                Name = name
                Pass = pass
                ForgotFragment.forgot = false
                findNavController().navigate(R.id.action_signupFragment_to_otpFragment)
            })
            signupRepository.errorMessage.observe(this, {
                Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                btn.isEnabled = true
            })
        } else btn.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}