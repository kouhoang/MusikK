package com.kouhoang.music.activity

import android.content.Intent
import android.os.Bundle
import com.kouhoang.music.R
import com.kouhoang.music.base.activity.BaseActivity
import com.kouhoang.music.common.hideKeyboard
import com.kouhoang.music.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    override val TAG = LoginActivity::class.java.simpleName.toString()

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnClickListener()

        window.statusBarColor = getColor(R.color.status_bar_color_login)
    }

    private fun setOnClickListener() {
        binding.layoutMain.setOnClickListener {
            hideKeyboard(this, binding.root)
        }
    }

    fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}