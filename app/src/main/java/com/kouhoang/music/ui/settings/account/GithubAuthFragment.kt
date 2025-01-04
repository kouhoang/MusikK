package com.kouhoang.music.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.kouhoang.music.base.dialogs.ProgressDialog
import com.kouhoang.music.base.fragment.BaseLoginFragment
import com.kouhoang.music.databinding.FragmentGithubAuthBinding

class GithubAuthFragment : BaseLoginFragment() {

    private lateinit var binding: FragmentGithubAuthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGithubAuthBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.btSignIn.setOnClickListener {
            onClickSignIn()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClickSignIn() {
        val email = binding.edtEmail.text.toString()
        ValidationUtils.checkValidEmail(email)?.let {
            binding.apply {
                tvError.text = it
                tvError.visibility = View.VISIBLE
                edtEmail.setText("")
            }
            return
        }

        binding.tvError.visibility = View.GONE
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("login", email)
        provider.scopes = listOf("user:email")

        val progressDialog = ProgressDialog(loginActivity, "Loading...")
        progressDialog.show()
        val pendingResultTask = FirebaseAuth.getInstance().pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    loginActivity.goToMainActivity()
                }
                .addOnFailureListener {
                    Log.e("GithubAuthFragment", "pendingResultTask: ${it.message.toString()}")
                }
        } else {
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(loginActivity, provider.build())
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    loginActivity.goToMainActivity()
                }
                .addOnFailureListener {
                    Log.e("GithubAuthFragment", it.message.toString())
                    progressDialog.dismiss()
                    if (it.message?.contains("An account already exists") == true) {
                        binding.tvError.text = "Email is already registered with another account."
                        binding.tvError.visibility = View.VISIBLE
                    }
                }
        }
    }
}