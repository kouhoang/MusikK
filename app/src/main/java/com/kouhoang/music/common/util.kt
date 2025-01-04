package com.kouhoang.music.common

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.DialogLoginBinding
import com.kouhoang.music.service.MusicService
import java.io.Serializable

const val CHANNEL_ID = "channel_service"

const val ACTION_MUSIC_NAME = "action_music"

const val SEND_DATA = "send_data"
const val SEND_CURRENT_TIME = "send_current_time"

const val ACTION_PREVIOUS = 1
const val ACTION_PAUSE = 2
const val ACTION_RESUME = 3
const val ACTION_NEXT = 4
const val ACTION_CLEAR = 5
const val ACTION_START = 6
const val ACTION_CONTROL_SEEK_BAR = 7
const val ACTION_SHUFFLE = 8
const val ACTION_LOOP = 9
const val ACTION_RELOAD_DATA = 10

const val KEY_SONG = "key_song"
const val KEY_LIST_SONGS = "key_list_song"
const val KEY_ACTION = "key_action"
const val KEY_FINAL_TIME = "key_final_time"
const val KEY_CURRENT_TIME = "key_current_time"
const val KEY_STATUS_MUSIC = "key_status_music"
const val KEY_STATUS_SHUFFLE = "key_status_shuffle"
const val KEY_STATUS_LOOP = "key_status_loop"
const val KEY_PROGRESS = "key_status_music"


const val MUSIC_SERVICE_TAG = "MusicService"
const val MAIN_ACTIVITY_TAG = "MainActivity"
const val HOME_FRAGMENT_TAG = "HomeFragment"
const val ONLINE_SONGS_FRAGMENT_TAG = "OnlineMusicFragment"
const val FAVOURITE_SONGS_FRAGMENT_TAG = "FavouriteMusicFragment"
const val MUSIC_PLAYER_FRAGMENT_TAG = "MusicPlayerFragment"
const val DEVICE_SONGS_FRAGMENT_TAG = "DeviceMusicFragment"
const val SETTINGS_FRAGMENT_TAG = "SettingsFragment"

const val KEY_THEME_MODE = "KEY_THEME_MODE"
const val SYSTEM_MODE = "Follow system"
const val LIGHT_MODE = "Light"
const val DARK_MODE = "Dark"

const val MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES"

/*const val HOME_FRAGMENT = 1
const val ONLINE_SONGS_FRAGMENT = 2
const val FAVOURITE_SONGS_FRAGMENT = 3
const val DEVICE_SONGS_FRAGMENT = 4
const val ACCOUNT_FRAGMENT = 5
const val APPEARANCE_FRAGMENT = 6
const val APP_INFO_FRAGMENT = 7
const val CONTACT_FRAGMENT = 8
const val MUSIC_PLAYER_FRAGMENT = 9
const val CHANGE_PASSWORD_FRAGMENT = 10*/

const val TITLE_HOME = "Home"
const val TITLE_ONLINE_SONGS = "Online Songs"
const val TITLE_FAVOURITE_SONGS = "Favourite Songs"
const val TITLE_DEVICE_SONGS = "Device Songs"
const val TITLE_SETTINGS = "Settings"
const val TITLE_ACCOUNT = "Account"
const val TITLE_APPEARANCE = "Appearance"
const val TITLE_APP_INFO = "About"
const val TITLE_CONTACT = "Contact"
const val TITLE_MUSIC_PLAYER = "Music Player"

/*const val EMPTY_LIST_SONG_TEXT_ONLINE = "No currentSong in list. " +
        "Please check your internet connection."
const val EMPTY_LIST_SONG_TEXT_DEVICE = "No currentSong in list. " +
        "Please allow access photos and " +
        "media on your device or add new currentSong to your media storage."
const val EMPTY_LIST_SONG_TEXT_FAVOURITE = "No currentSong in list. " +
        "Please check your internet connection or sign in to see your favourites."*/

const val EMAIL_CONTACT = "icebergrosr31@gmail.com"
const val MICROSOFT_CONTACT = ""
const val URI_FB = "https://www.facebook.com/kouhoangg"
const val URI_ZALO = ""

const val GUEST = "Guest"
const val GUEST_EMAIL = "someone@gmail.com"

const val DATABASE_NAME = "my_db"

fun hideKeyboard(context: Context, view: View?) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}

fun isSongExists(songList: List<Song>?, song: Song?): Boolean {
    if (songList == null || song == null || songList.isEmpty()) {
        return false
    }
    for (s in songList) {
        if (s.resourceUri == song.resourceUri) {
            return true
        }
    }
    return false
}

fun isExistSameName(songList: List<Song>, song: Song): Boolean {
    for (s in songList) {
        if (s.name == song.name && s.singer == song.singer) {
            return true
        }
    }
    return false
}

fun sendListSongToService(context: Context, songs: List<Song>) {
    val intent = Intent(context, MusicService::class.java)
    val bundle = Bundle()
    bundle.putSerializable(KEY_LIST_SONGS, songs as Serializable)
    intent.putExtras(bundle)
    context.startService(intent)
}

fun sendDataToService(context: Context, currentSong: Song, action: Int) {
    val intent = Intent(context, MusicService::class.java)
    intent.putExtra(KEY_ACTION, action)
    val bundle = Bundle()
    bundle.putSerializable(KEY_SONG, currentSong)
    intent.putExtras(bundle)
    context.startService(intent)
}

fun sendActionToService(context: Context, action: Int) {
    val intent = Intent(context, MusicService::class.java)
    intent.putExtra(KEY_ACTION, action)
    context.startService(intent)
}

fun sendCurrentTimeToService(context: Context, progress: Int) {
    val intentActivity = Intent(context, MusicService::class.java)
    intentActivity.putExtra(KEY_ACTION, ACTION_CONTROL_SEEK_BAR)
    intentActivity.putExtra(KEY_PROGRESS, progress)
    context.startService(intentActivity)
}

fun showDialog(context: Context, inflater: LayoutInflater, message: String) {
    val builder = AlertDialog.Builder(context)
    val dialogLogin = DialogLoginBinding.inflate(inflater)
    dialogLogin.tvMessage.text = message
    builder.setView(dialogLogin.root)
    val dialog = builder.create()
    dialog.show()
}

fun getBitmapFromUri(context: Context, uri: String?): Bitmap? {
    val mmr = MediaMetadataRetriever()

    try {
        mmr.setDataSource(context, Uri.parse(uri))
    } catch (e: Exception) {
        Log.e("Get bitmap mmr", e.message.toString())
    }

    return mmr.embeddedPicture?.size?.let {
        BitmapFactory.decodeByteArray(mmr.embeddedPicture, 0, it)
    }
}

inline fun <T> sdk33AndUp(onSdk33: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onSdk33()
    } else null
}