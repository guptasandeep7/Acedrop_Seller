package com.example.acedropseller.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.acedropseller.R
import com.example.acedropseller.databinding.FragmentLoginBinding
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.auth.GoogleSignRepository
import com.example.acedropseller.repository.auth.LoginRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch


class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginRepository: LoginRepository
    private lateinit var googleSignRepository: GoogleSignRepository
    lateinit var datastore: Datastore

    //google sign in
    var gso: GoogleSignInOptions? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    var RC_SIGN_IN = 101
    var TAG = "AuthActivity"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.signinToSignup.setOnClickListener(this)
        binding.forgotTxt.setOnClickListener(this)
        binding.signinBtn.setOnClickListener(this)
        binding.googleBtn.setOnClickListener(this)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso!!)
    }

    override fun onClick(view: View?) {
        val navController = findNavController()
        when (view?.id) {
            R.id.signin_to_signup -> navController.navigate(R.id.action_loginFragment_to_signupFragment)
            R.id.forgot_txt -> navController.navigate(R.id.action_loginFragment_to_forgotFragment)
            R.id.signin_btn -> login()
            R.id.google_btn -> googleSignIn()
        }
    }

    private fun googleSignIn() {
        binding.progressBar.visibility = View.VISIBLE
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=${e.statusCode}")
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            checkToken(account.idToken)
        } else Toast.makeText(requireContext(), "Failed to sign with google", Toast.LENGTH_SHORT)
            .show()
    }

    private fun checkToken(idToken: String?) {
        googleSignRepository = GoogleSignRepository()
        if (idToken != null) {
            googleSignRepository.gSignUp(idToken)
        }

        googleSignRepository.errorMessage.observe(viewLifecycleOwner, {
            binding.progressBar.visibility = View.GONE
            binding.signinBtn.isEnabled = true
            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
        })

        googleSignRepository.userData.observe(viewLifecycleOwner, {
            binding.progressBar.visibility = View.GONE
            datastore = Datastore(requireContext())
            lifecycleScope.launch {
                datastore.saveToDatastore(it, requireContext())
                activity?.finish()
                findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity)
            }
        })
    }

    //Check details are valid or not
    private fun isValid(email: String, pass: String): Boolean {
        return when {
            email.isBlank() -> {
                binding.emailLayout.helperText = "Enter Email Id"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.emailLayout.helperText = "Enter valid Email Id"
                false
            }
            else -> true
        }
    }

    //Remove the helper text
    private fun helper() = with(binding) {
        emailLayout.helperText = ""
        passLayout.helperText = ""
    }

    //Custom Login
    private fun login() {
        binding.signinBtn.isEnabled = false
        val email = binding.email.text.toString().trim()
        val pass = binding.pass.text.toString().trim()
        val progressBar = binding.progressBar
        helper()
        if (isValid(email, pass)) {
            loginRepository = LoginRepository()
            progressBar.visibility = View.VISIBLE
            loginRepository.login(email = email, pass = pass)

            loginRepository.userDetails.observe(this, {
                progressBar.visibility = View.GONE
                if(it.status!=3)
                checkStatus(it.status!!)
                else{
                    datastore = Datastore(requireContext())
                    lifecycleScope.launch {
                        datastore.saveToDatastore(it, requireContext())
                        activity?.finish()
                        findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity)
                    }
                }

            })

            loginRepository.errorMessage.observe(this, {
                progressBar.visibility = View.GONE
                binding.signinBtn.isEnabled = true
                Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
            })
        } else binding.signinBtn.isEnabled = true
    }

    private fun checkStatus(status:Int) {
        when(status){
            0 -> {
                findNavController().navigate(R.id.action_loginFragment_to_businessDetailsFragment)
                Toast.makeText(requireContext(), "Business Details Pending", Toast.LENGTH_SHORT).show()
            }
            -1 -> Toast.makeText(requireContext(), "Invalid Email/Password", Toast.LENGTH_SHORT).show()
            2 -> {
                findNavController().navigate(R.id.action_loginFragment_to_sellerPhotoFragment)
                Toast.makeText(requireContext(), "Upload seller pic pending", Toast.LENGTH_SHORT).show()
            }
            1 -> {
                findNavController().navigate(R.id.action_loginFragment_to_aadharFragment)
                Toast.makeText(requireContext(), "upload aadhar pic", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}