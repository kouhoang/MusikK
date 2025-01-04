package com.kouhoang.music.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.R
import com.kouhoang.music.base.activity.BaseActivity
import com.kouhoang.music.common.DARK_MODE
import com.kouhoang.music.common.LIGHT_MODE
import com.kouhoang.music.common.SYSTEM_MODE
import com.kouhoang.music.data.data_source.app_data.DataLocalManager
import com.kouhoang.music.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    override val TAG = SplashActivity::class.java.simpleName.toString()

    @Inject
    lateinit var dataLocalManager: DataLocalManager

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setThemeMode()

        addAnimation()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            nextActivity()
        } else {
            Log.e("SplashActivity", "Go to Login")
            Handler(Looper.myLooper()!!).postDelayed({ nextActivity() }, 2500)
        }
    }

    private fun addAnimation() {
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_splash)
        binding.imgIcon.startAnimation(slideInAnimation)
    }

    private fun setThemeMode() {
        when (dataLocalManager.getStringThemeMode()) {
            SYSTEM_MODE ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

            LIGHT_MODE ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)

            DARK_MODE ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)

            else ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun nextActivity() {
        val intent = if (FirebaseAuth.getInstance().currentUser != null) {
            Intent(this@SplashActivity, MainActivity::class.java)
        } else {
            Intent(this@SplashActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}