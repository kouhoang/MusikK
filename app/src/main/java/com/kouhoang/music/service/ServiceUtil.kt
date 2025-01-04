package com.kouhoang.music.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.kouhoang.music.activity.MainActivity
import com.kouhoang.music.broadcast_receiver.MyReceiver
import com.kouhoang.music.common.ACTION_MUSIC_NAME
import com.kouhoang.music.data.models.Song
import java.text.Collator
import java.util.Locale

fun getPendingIntent(context: Context, action: Int): PendingIntent? {
    val intent = Intent(context, MyReceiver::class.java)
    intent.putExtra(ACTION_MUSIC_NAME, action)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(
            context.applicationContext,
            action, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        PendingIntent.getBroadcast(
            context.applicationContext,
            action, intent, PendingIntent.FLAG_IMMUTABLE
        )
    }
}

fun getPendingIntentClickNotification(context: Context): PendingIntent {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    } else {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}

fun isListSortedAscending(list: MutableList<Song>): Boolean {
    val collator = Collator.getInstance(Locale("vi"))
    for (i in 1 until list.size) {
        if (collator.compare(list[i].name!!, list[i - 1].name!!) < 0) {
            return false
        }
    }
    return true
}