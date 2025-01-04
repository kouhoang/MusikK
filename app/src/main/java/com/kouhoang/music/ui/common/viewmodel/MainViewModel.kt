package com.kouhoang.music.ui.common.viewmodel


import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.kouhoang.music.base.viewmodel.BaseViewModel
import com.kouhoang.music.common.KEY_ACTION
import com.kouhoang.music.common.KEY_CURRENT_TIME
import com.kouhoang.music.common.KEY_FINAL_TIME
import com.kouhoang.music.common.KEY_SONG
import com.kouhoang.music.common.KEY_STATUS_LOOP
import com.kouhoang.music.common.KEY_STATUS_MUSIC
import com.kouhoang.music.common.KEY_STATUS_SHUFFLE
import com.kouhoang.music.common.SEND_CURRENT_TIME
import com.kouhoang.music.data.data_source.app_data.DataLocalManager
import com.kouhoang.music.data.models.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    dataLocalManager: DataLocalManager
) : BaseViewModel() {

    var currentSong = MutableLiveData<Song>()
    var currentListName: String? = null

    val isShowMusicPlayer = MutableLiveData(false)
    val isServiceRunning = MutableLiveData(false)
    val isShowMiniPlayer = MutableLiveData(false)
    val isShowBtPlayAll = MutableLiveData(false)

    val isPlaying = MutableLiveData(false)
    val isLooping = MutableLiveData(false)
    val isShuffling = MutableLiveData(false)

    val actionMusic = MutableLiveData(0)
    val finalTime = MutableLiveData(0)
    val currentTime = MutableLiveData(0)
    var themeMode = MutableLiveData<String>(dataLocalManager.getStringThemeMode())

    fun receiveDataFromReceiver(intent: Intent) {
        val bundle = intent.extras ?: return
        currentSong.postValue(bundle.getSerializable(KEY_SONG) as Song?)
        isPlaying.postValue(bundle.getBoolean(KEY_STATUS_MUSIC))
        isLooping.postValue(bundle.getBoolean(KEY_STATUS_LOOP))
        isShuffling.postValue(bundle.getBoolean(KEY_STATUS_SHUFFLE))
        actionMusic.postValue(bundle.getInt(KEY_ACTION))
        finalTime.postValue(bundle.getInt(KEY_FINAL_TIME))
    }

    fun receiveCurrentTime(intent: Intent) {
        if (intent.action == SEND_CURRENT_TIME) {
            val bundle = intent.extras
            if (bundle != null) {
                currentTime.postValue(bundle.getInt(KEY_CURRENT_TIME))
            }
        }
    }
}