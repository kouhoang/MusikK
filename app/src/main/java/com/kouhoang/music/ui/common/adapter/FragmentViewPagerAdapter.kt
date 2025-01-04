package com.kouhoang.music.ui.common.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kouhoang.music.ui.device_songs.DeviceSongsFragment
import com.kouhoang.music.ui.favourite_songs.FavouriteSongsFragment
import com.kouhoang.music.ui.home.HomeFragment
import com.kouhoang.music.ui.online_songs.OnlineSongsFragment
import com.kouhoang.music.ui.settings.SettingsFragment


class FragmentViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> OnlineSongsFragment()
            2 -> FavouriteSongsFragment()
            3 -> DeviceSongsFragment()
            else -> SettingsFragment()
        }
    }
}