package com.kouhoang.music.data.data_source.app_data

import com.kouhoang.music.common.KEY_THEME_MODE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataLocalManager @Inject constructor(
    private val sharedPreferences: MySharedPreferences
) {

    fun putStringThemeMode(value: String) {
        sharedPreferences.putStringValue(KEY_THEME_MODE, value)
    }

    fun getStringThemeMode(): String? {
        return sharedPreferences.getStringValue(KEY_THEME_MODE)
    }
}