package com.kouhoang.music.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.GUEST
import com.kouhoang.music.common.TITLE_ACCOUNT
import com.kouhoang.music.common.TITLE_APPEARANCE
import com.kouhoang.music.common.TITLE_APP_INFO
import com.kouhoang.music.common.TITLE_CONTACT
import com.kouhoang.music.databinding.FragmentSettingOptionsBinding



class SettingOptionsFragment : BaseMainFragment<FragmentSettingOptionsBinding>() {

    @SuppressLint("CommitTransaction")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        registerObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun registerObserver() {

        binding?.tvName?.text = if (FirebaseAuth.getInstance().currentUser != null) {
            "( ${FirebaseAuth.getInstance().currentUser?.displayName} )"
        } else {
            "( $GUEST )"
        }

        accountViewModel?.user?.observe(viewLifecycleOwner) { user ->
            binding?.tvName?.text = if (user != null) {
                "( ${FirebaseAuth.getInstance().currentUser?.displayName} )"
            } else {
                "( $GUEST )"
            }
        }

        mainViewModel?.themeMode?.observe(viewLifecycleOwner) { mode ->
            binding?.tvTheme?.text = "( $mode )"
        }
    }

    private fun initListener() {
        binding?.layoutAccount?.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAccountFragment()
            goToFragment(action, TITLE_ACCOUNT)
        }

        binding?.layoutAppearance?.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAppearanceFragment()
            goToFragment(action, TITLE_APPEARANCE)
        }

        binding?.layoutAppInfo?.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToAppInfoFragment()
            goToFragment(action, TITLE_APP_INFO)
        }

        binding?.layoutContact?.setOnClickListener {
            val action =
                SettingOptionsFragmentDirections.actionSettingOptionsFragmentToContactFragment()
            goToFragment(action, TITLE_CONTACT)
        }
    }

    private fun goToFragment(action: NavDirections, title: String) {
        findNavController().navigate(action)
        mainActivity?.let {
            it.binding.toolBarTitle.text = title
        }
    }

    override val layoutRes: Int
        get() = R.layout.fragment_setting_options

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}