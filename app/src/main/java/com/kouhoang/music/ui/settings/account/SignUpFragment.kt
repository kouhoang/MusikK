package com.kouhoang.music.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.kouhoang.music.R
import com.kouhoang.music.base.dialogs.ProgressDialog
import com.kouhoang.music.base.fragment.BaseLoginFragment
import com.kouhoang.music.common.showDialog
import com.kouhoang.music.databinding.FragmentSignUpBinding

class SignUpFragment : BaseLoginFragment() {

    private lateinit var binding: FragmentSignUpBinding

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()


    }

    private fun initListener() {

        binding.btSignUp.setOnClickListener {
            onClickSignUp()
        }

        binding.layoutSignIn.setOnClickListener {
            goToSignIn()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClickSignUp() {
        progressDialog = ProgressDialog(loginActivity, "Signing up...")

        binding.tvError.apply {
            text = ""
            visibility = View.GONE
        }

        val email = binding.edtEmail.text?.trim().toString()
        val name = binding.edtName.text?.trim().toString()
        val password = binding.edtPassword.text?.trim().toString()
        val confirmPassword = binding.edtConfirmPassword.text?.trim().toString()

        ValidationUtils.checkValidSignUpInput(email, name, password, confirmPassword)?.let {
            binding.apply {
                tvError.text = it
                tvError.visibility = View.VISIBLE
                if (it.contains("email", true)) {
                    edtEmail.setText("")
                } else if (it.contains("name", true)) {
                    edtName.setText("")
                } else if (it.contains("confirm password", true)) {
                    edtConfirmPassword.setText("")
                } else if (it.contains("password", true)
                    || it.contains("passwords", true)
                ) {
                    edtConfirmPassword.setText("")
                    edtPassword.setText("")
                }
            }
            return
        }

        progressDialog.show()
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (!signInMethods.isNullOrEmpty()) {
                        // email is exists
                        binding.apply {
                            tvError.text = "Email already exists"
                            edtEmail.setText("")
                            tvError.visibility = View.VISIBLE
                        }
                        return@addOnCompleteListener
                    } else {
                        // available for sign up
                        signUp(email, password, name)
                    }
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun signUp(email: String, password: String, name: String) {
        val auth = FirebaseAuth.getInstance()
        progressDialog.show()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    setNameForUser(name)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        loginActivity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthInvalidUserException) {
                    binding.tvError.apply {
                        text = "Email already exists"
                        visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun setNameForUser(name: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()
        val nameUpdates = userProfileChangeRequest {
            displayName = name
        }
        user?.updateProfile(nameUpdates)?.addOnCompleteListener {
            progressDialog.dismiss()
            if (it.isSuccessful) {
                //back to account fragment
                loginActivity.goToMainActivity()
                //show dialog
                val message = "Sign up success. " +
                        "Please check your email (${user.email}) to verify account."
                showDialog(loginActivity, layoutInflater, message)
            }
        }
        FirebaseAuth.getInstance().signOut()
    }

    private fun goToSignIn() {
        if (isFragmentInBackStack(R.id.signInFragment)) {
            findNavController().popBackStack(R.id.signInFragment, false)
        } else {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }
}