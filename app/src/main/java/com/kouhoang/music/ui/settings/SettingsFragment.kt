package com.kouhoang.music.ui.settings

import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseMainFragment<FragmentSettingsBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_settings
}