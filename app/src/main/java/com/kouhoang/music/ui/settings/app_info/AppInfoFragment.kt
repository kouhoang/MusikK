package com.kouhoang.music.ui.settings.app_info

import android.os.Bundle
import android.view.View
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.TITLE_APP_INFO
import com.kouhoang.music.databinding.FragmentAppInfoBinding


class AppInfoFragment : BaseMainFragment<FragmentAppInfoBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_app_info

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        mainActivity?.let {
            it.binding.toolBarTitle.text = TITLE_APP_INFO
        }
    }
}