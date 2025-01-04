package com.kouhoang.music.ui.common.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.base.viewmodel.BaseViewModel
import com.kouhoang.music.common.TITLE_DEVICE_SONGS
import com.kouhoang.music.common.TITLE_FAVOURITE_SONGS
import com.kouhoang.music.common.TITLE_ONLINE_SONGS
import com.kouhoang.music.data.common.Response
import com.kouhoang.music.data.common.sortListAscending
import com.kouhoang.music.data.common.toSongModel
import com.kouhoang.music.data.models.Category
import com.kouhoang.music.data.models.Photo
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.data.repositories.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songRepository: SongRepository
) : BaseViewModel() {

    val onlineSongs = MutableLiveData<List<Song>>()

    val favouriteSongs = MutableLiveData<List<Song>>()

    val deviceSongs = MutableLiveData<List<Song>>()

    val photos = MutableLiveData<List<Photo>>()

    private val onlineSongsShow = MutableLiveData<List<Song>>()

    val favouriteSongsShow = MutableLiveData<List<Song>>()

    private val deviceSongsShow = MutableLiveData<List<Song>>()

    val optionSong = MutableLiveData<Song>()

    init {
        getOnlineSongsFromRemote()
        getFavouriteSongsFromRemote()
    }

    private fun getOnlineSongsFromRemote() {
        job = viewModelScope.launch(exceptionHandler) {
            val response = songRepository.getOnlineSongsFromLocal()
            if (response is Response.Success) {
                val songs = response.data?.map { it.toSongModel() }
                sortListAscending(songs as MutableList<Song>)
                songs.let {
                    onlineSongs.value = it
                }
            }

            val networkResponse = songRepository.getOnlineSongs()
            if (networkResponse is Response.Success) {
                val songs = networkResponse.data?.map { it.toSongModel() }
                sortListAscending(songs as MutableList<Song>)
                songs.let {
                    onlineSongs.value = it
                    songRepository.insertOnlineSongsToLocal(it)
                }
                Log.e("NetworkResponse", "Get data success")
            } else if (networkResponse is Response.Error) {
                networkResponse.message?.let {
                    Log.e("NetworkResponse", it)
                }
            }
        }
    }

    fun getFavouriteSongsFromRemote() {

        if (FirebaseAuth.getInstance().currentUser != null) {
            job = viewModelScope.launch(exceptionHandler) {
                val response = songRepository.getFavouriteSongsFromLocal()
                if (response is Response.Success) {
                    val songs = response.data?.map { it.toSongModel() }
                    sortListAscending(songs as MutableList<Song>)
                    songs.let {
                        favouriteSongs.value = it
                    }
                }
                songRepository.getFavouriteSongs {
                    favouriteSongs.value = it
                    job = viewModelScope.launch(exceptionHandler) {
                        songRepository.deleteAllFavouritesFromLocal()
                        songRepository.insertFavouriteSongsToLocal(it)
                        Log.e("SongViewModel", "saving data to local")
                    }
                }
            }
        }
    }

    fun deleteAllFavourites() {
        job = viewModelScope.launch(exceptionHandler) {
            songRepository.deleteAllFavouritesFromLocal()
        }
    }

    fun getLocalData() {
        job = viewModelScope.launch(exceptionHandler) {
            val list = songRepository.getDeviceSongs()
            if (list.isNotEmpty()) {
                deviceSongs.postValue(list)
            }
        }
    }

    fun getListPhotos() {
        val list = mutableListOf<Photo>()
        if (!onlineSongs.value.isNullOrEmpty()) {
            val listSongs = onlineSongs.value!!.take(5)
            listSongs.forEach { song ->
                song.imageUri?.let { list.add(Photo(it)) }
            }
            photos.value = list
        }
    }

    fun getListCategories(): MutableList<Category> {
        onlineSongsShow.value = getSongsShow(onlineSongs.value as MutableList<Song>?)
        favouriteSongsShow.value = getSongsShow(favouriteSongs.value as MutableList<Song>?)
        deviceSongsShow.value = getSongsShow(deviceSongs.value as MutableList<Song>?)
        val list = mutableListOf<Category>()
        if (!onlineSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_ONLINE_SONGS, onlineSongsShow.value as MutableList<Song>))
        }
        if (!favouriteSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_FAVOURITE_SONGS, favouriteSongsShow.value as MutableList<Song>))
        }
        if (!deviceSongsShow.value.isNullOrEmpty()) {
            list.add(Category(TITLE_DEVICE_SONGS, deviceSongsShow.value as MutableList<Song>))
        }
        return list
    }

    private fun getSongsShow(songs: MutableList<Song>?): MutableList<Song> {
        var list = mutableListOf<Song>()
        if (!songs.isNullOrEmpty()) {
            list = songs.take(10) as MutableList<Song>
        }
        return list
    }

    fun addSongToFavourites(song: Song) {
        // remote
        FirebaseAuth.getInstance().currentUser?.email?.let {
            songRepository.pushSongToFavourites(it, song, callback = {
                job = viewModelScope.launch(exceptionHandler) {
                    songRepository.insertFavouriteSongToLocal(song)
                }
            })
        }
    }

    fun removeSongFromFavourites(song: Song) {
        // remote
        FirebaseAuth.getInstance().currentUser?.email?.let {
            songRepository.removeSongOnFavourites(it, song, callback = {
                job = viewModelScope.launch(exceptionHandler) {
                    songRepository.deleteFavouriteSongFromLocal(song)
                    getFavouriteSongsFromRemote()
                }
            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("SongViewModel", "onCleared")
        job?.cancel()
    }
}