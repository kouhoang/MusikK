package com.kouhoang.music.ui.settings.appearance

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.DARK_MODE
import com.kouhoang.music.common.LIGHT_MODE
import com.kouhoang.music.common.SYSTEM_MODE
import com.kouhoang.music.common.TITLE_APPEARANCE
import com.kouhoang.music.databinding.FragmentAppearanceBinding

class AppearanceFragment : BaseMainFragment<FragmentAppearanceBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_appearance

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setLayout()

        initListener()
    }

    private fun setLayout() {
        when (mainViewModel?.themeMode?.value) {
            SYSTEM_MODE -> binding?.rdBtGroup?.check(R.id.rd_bt_follow_system)

            LIGHT_MODE -> binding?.rdBtGroup?.check(R.id.rd_bt_light)

            DARK_MODE -> binding?.rdBtGroup?.check(R.id.rd_bt_dark)
        }
    }

    private fun initListener() {
        binding?.btApply?.setOnClickListener {
            binding?.rdBtGroup?.checkedRadioButtonId?.let { it1 -> changeThemeMode(it1) }
        }
    }

    private fun changeThemeMode(position: Int) {
        when (position) {
            R.id.rd_bt_follow_system ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, SYSTEM_MODE)

            R.id.rd_bt_light ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_NO, LIGHT_MODE)

            R.id.rd_bt_dark ->
                setThemeMode(AppCompatDelegate.MODE_NIGHT_YES, DARK_MODE)
        }
    }

    private fun setThemeMode(mode: Int, value: String) {
        AppCompatDelegate.setDefaultNightMode(mode)
        mainViewModel?.themeMode?.value = value
    }

    override fun onResume() {
        super.onResume()
        mainActivity?.let {
            it.binding.toolBarTitle.text = TITLE_APPEARANCE
        }
    }
}