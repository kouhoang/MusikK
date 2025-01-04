package com.kouhoang.music.data.data_source.app_data

import android.annotation.SuppressLint
import android.content.Context
import com.kouhoang.music.common.MY_SHARED_PREFERENCES
import com.kouhoang.music.common.SYSTEM_MODE

class MySharedPreferences(private val context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    @SuppressLint("CommitPrefEdits")
    fun putStringValue(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getStringValue(key: String): String? {
        return sharedPreferences.getString(key, SYSTEM_MODE)
    }
}