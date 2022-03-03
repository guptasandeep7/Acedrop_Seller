package com.example.acedropseller.view.dash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentChangePasswordBinding
import com.example.acedropseller.model.Message
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.utill.validPass
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment() {
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.signBtn.setOnClickListener {
            it.isEnabled = false
            val oldPass = binding.oldPass.text.toString().trim()
            val newPass = binding.newPass.text.toString().trim()
            binding.oldPassLayout.helperText = ""
            binding.newPassLayout.helperText = ""

            val error1: String? = validPass(oldPass)
            val error2: String? = validPass(newPass)

            when {
                error1 != null -> {
                    binding.oldPassLayout.helperText = error1
                    it.isEnabled = true
                }
                error2 != null -> {
                    binding.newPassLayout.helperText = error2
                    it.isEnabled = true
                }
                else -> {
                    lifecycleScope.launch {
                        changePass(oldPass, newPass)
                    }
                }
            }
        }
        return binding.root
    }

    private suspend fun changePass(oldPass: String, newPass: String) {
        binding.progressBar.visibility = View.VISIBLE
        val userEmail = Datastore(requireContext()).getUserDetails(Datastore.EMAIL_KEY).toString()

        val request = ServiceBuilder.buildService(null)
        val call = request.changePass(email = userEmail, oldPass = oldPass, newPass = newPass)
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signBtn.isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            "Password changed successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
                    }
                    response.code() == 422 -> errorMessage("Enter valid password")
                    response.code() == 401 -> errorMessage("Session expired")
                    response.code() == 400 -> errorMessage("Try again")
                    else -> errorMessage("Something went wrong! Try again")
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage(t.message.toString())
            }
        })
    }

    private fun errorMessage(it: String) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        binding.signBtn.isEnabled = true
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