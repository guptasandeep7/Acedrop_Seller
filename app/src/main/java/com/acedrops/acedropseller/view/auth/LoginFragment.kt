package com.acedrops.acedropseller.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.acedrops.acedropseller.R
import com.acedrops.acedropseller.databinding.FragmentLoginBinding
import com.acedrops.acedropseller.model.Token
import com.acedrops.acedropseller.model.UserData
import com.acedrops.acedropseller.network.ServiceBuilder
import com.acedrops.acedropseller.repository.Datastore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
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

        datastore = Datastore(requireContext())

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
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=${e.statusCode}")
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            checkToken(account.idToken)
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Failed to sign with google", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkToken(idToken: String?) {
        if (idToken != null) {
            gSignUp(idToken)
        }
    }

    fun gSignUp(idToken: String) {
        val request = ServiceBuilder.buildService(null)
        val call = request.gSignUp(Token(idToken))
        call.enqueue(object : Callback<UserData?> {
            override fun onResponse(call: Call<UserData?>, response: Response<UserData?>) {
                when {
                    response.isSuccessful -> {
                        val responseBody = response.body()!!
                        if (responseBody.status == -1) {
                            errorMessage("Invalid Email Id")
                        } else {
                            runBlocking {
                                datastore.saveToDatastore(responseBody, requireContext())
                                binding.progressBar.visibility = View.GONE
                                if (responseBody.status == 3) {
                                    activity?.finish()
                                    findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity)
                                } else {
                                    responseBody.status?.let { checkStatus(it) }
                                }
                            }
                        }
                    }
                    response.code() == 503 -> errorMessage(response.message())
                    else -> errorMessage(response.message())
                }
            }

            override fun onFailure(call: Call<UserData?>, t: Throwable) {
                errorMessage(t.message.toString())
            }
        })
    }

    private fun errorMessage(it: String) {
        binding.progressBar.visibility = View.GONE
        binding.signinBtn.isEnabled = true
        Toast.makeText(
            requireContext(),
            it,
            Toast.LENGTH_SHORT
        ).show()
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
            progressBar.visibility = View.VISIBLE
            loginApi(email = email, pass = pass)
        } else binding.signinBtn.isEnabled = true
    }

    private fun loginApi(email: String, pass: String) {
        val request = ServiceBuilder.buildService(null)
        val call = request.login(UserData(email = email, password = pass))
        call.enqueue(object : Callback<UserData?> {
            override fun onResponse(call: Call<UserData?>, response: Response<UserData?>) {
                when {
                    response.isSuccessful -> {
                        val responseBody = response.body()!!
                        if (responseBody.status == -1) {
                            errorMessage("Invalid Email Id")
                        } else {
                            runBlocking {
                                datastore.saveToDatastore(responseBody, requireContext())
                                binding.progressBar.visibility = View.GONE
                                if (responseBody.status == 3) {
                                    activity?.finish()
                                    findNavController().navigate(R.id.action_loginFragment_to_dashboardActivity)
                                } else {
                                    responseBody.status?.let { checkStatus(it) }
                                }
                            }
                        }
                    }
                    response.code() == 401 -> errorMessage("Wrong password")
                    response.code() == 422 -> errorMessage("Enter correct email and password")
                    response.code() == 404 -> errorMessage("User does not exists please signup")
                    else -> errorMessage("User not registered")
                }
            }

            override fun onFailure(call: Call<UserData?>, t: Throwable) {
                errorMessage(t.message.toString())
            }
        })
    }

    private fun checkStatus(status: Int) {
        when (status) {
            0 -> {
                findNavController().navigate(R.id.action_loginFragment_to_businessDetailsFragment)
                Toast.makeText(requireContext(), "Business Details Pending", Toast.LENGTH_SHORT)
                    .show()
            }
            2 -> {
                findNavController().navigate(R.id.action_loginFragment_to_sellerPhotoFragment)
                Toast.makeText(
                    requireContext(),
                    "Upload seller picture pending",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            1 -> {
                findNavController().navigate(R.id.action_loginFragment_to_aadharFragment)
                Toast.makeText(requireContext(), "Upload Aadhar picture", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}