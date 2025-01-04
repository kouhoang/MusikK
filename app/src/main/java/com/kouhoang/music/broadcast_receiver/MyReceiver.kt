package com.kouhoang.music.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kouhoang.music.common.ACTION_MUSIC_NAME
import com.kouhoang.music.common.KEY_ACTION
import com.kouhoang.music.service.MusicService

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // receive action from notification and send to service
        val actionMusic = intent?.getIntExtra(ACTION_MUSIC_NAME, 0)

        val intentService = Intent(context, MusicService::class.java)
        intentService.putExtra(KEY_ACTION, actionMusic)
        context?.startService(intentService)
    }
}