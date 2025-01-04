package com.kouhoang.music.ui.settings.account

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.R
import com.kouhoang.music.base.dialogs.ProgressDialog
import com.kouhoang.music.base.fragment.BaseLoginFragment
import com.kouhoang.music.common.hideKeyboard
import com.kouhoang.music.common.showDialog
import com.kouhoang.music.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : BaseLoginFragment() {

    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        binding.btSend.setOnClickListener {
            onClickSendResetPassword()
        }

        binding.layoutSignIn.setOnClickListener {
            goToSignIn()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClickSendResetPassword() {
        hideKeyboard(loginActivity, binding.root)
        val dialog = ProgressDialog(loginActivity, "Loading...")

        val email = binding.edtEmail.text?.trim().toString()
        binding.tvError.apply {
            text = ""
            visibility = View.GONE
        }
        ValidationUtils.checkValidEmail(email)?.let {
            binding.apply {
                tvError.text = it
                tvError.visibility = View.VISIBLE
                edtEmail.setText("")
            }
            return
        }

        dialog.show()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    val message = "Email sent. Check your email to complete reset password."
                    showDialog(loginActivity, layoutInflater, message)
                } else {
                    val message = "Email sent fail. Please check your email or network connection."
                    showDialog(loginActivity, layoutInflater, message)
                }
            }
    }

    private fun goToSignIn() {
        if (isFragmentInBackStack(R.id.signInFragment)) {
            findNavController().popBackStack(R.id.signInFragment, false)
        } else {
            val action =
                ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }
}