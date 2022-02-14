package com.example.acedropseller.view.dash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentHomeBinding
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.Datastore.Companion.ACCESS_TOKEN_KEY
import com.example.acedropseller.repository.Datastore.Companion.EMAIL_KEY
import com.example.acedropseller.repository.Datastore.Companion.NAME_KEY
import com.example.acedropseller.repository.Datastore.Companion.REF_TOKEN_KEY
import com.example.acedropseller.repository.auth.SignOutRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var signOutRepository: SignOutRepository
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val datastore = activity?.let { Datastore(it) }

        lifecycleScope.launch {
            binding.textviewName.let {
                it.text = "Hello, ${datastore?.getUserDetails(NAME_KEY)}"
            }
            Log.w("HOME FRAGMENT", "ACCESS TOKEN : ${datastore?.getUserDetails(ACCESS_TOKEN_KEY)}", )
            Log.w("HOME FRAGMENT", "refresh token : ${datastore?.getUserDetails(REF_TOKEN_KEY)}", )
        }

        binding.signOutBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            signOutRepository = SignOutRepository()
            signOutRepository.let { it ->
                lifecycleScope.launch {
                    it.signOut(datastore?.getUserDetails(REF_TOKEN_KEY)!!)
                }
                it.message.observe(viewLifecycleOwner, {
                    lifecycleScope.launch {
                        datastore?.changeLoginState(false)
                        signout()
                        activity?.finish()
                        findNavController().navigate(R.id.action_homeFragment_to_authActivity)
                    }

                })

                it.errorMessage.observe(viewLifecycleOwner, {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                })
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signout(): Boolean {
        var result: Boolean = false
        googleSignInClient.signOut().addOnCompleteListener {
            result = true
        }.addOnCanceledListener {
            result = false
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}