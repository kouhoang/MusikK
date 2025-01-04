package com.kouhoang.music.base.fragment

import androidx.lifecycle.ViewModelProvider
import com.kouhoang.music.activity.LoginActivity
import com.kouhoang.music.ui.settings.account.AccountViewModel

open class BaseLoginFragment : BaseFragment() {

    lateinit var loginActivity: LoginActivity

    open fun init() {
        loginActivity = requireActivity() as LoginActivity
        accountViewModel = ViewModelProvider(loginActivity)[AccountViewModel::class.java]
    }
}