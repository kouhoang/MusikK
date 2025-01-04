package com.kouhoang.music.data.repositories

import com.kouhoang.music.data.common.toFavouriteSongEntity
import com.kouhoang.music.data.common.toSongEntity
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.data.services.SongLocalService
import com.kouhoang.music.data.services.SongRemoteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongRepository @Inject constructor(
    private val songRemoteService: SongRemoteService,
    private val songLocalService: SongLocalService
) {

    suspend fun getOnlineSongs() = withContext(Dispatchers.IO) {
        songRemoteService.getOnlineSongs()
    }

    fun getFavouriteSongs(callback: (List<Song>) -> Unit) {
        songRemoteService.getFavouriteSongsFromFirebase(callback)
    }

    suspend fun getDeviceSongs() = withContext(Dispatchers.IO) {
        songLocalService.getLocalMusic()
    }

    fun pushSongToFavourites(email: String, song: Song, callback: () -> Unit) {
        songRemoteService.pushSongToFirebase(email, song, callback)
    }

    fun removeSongOnFavourites(email: String, song: Song, callback: () -> Unit) {
        songRemoteService.removeSongOnFirebase(email, song, callback)
    }

    // cache data
    // online song
    suspend fun getOnlineSongsFromLocal() = withContext(Dispatchers.IO) {
        songLocalService.getOnlineSongs()
    }

    suspend fun insertOnlineSongsToLocal(list: List<Song>) = withContext(Dispatchers.IO) {
        songLocalService.insertOnlineSongs(list.map {
            it.toSongEntity()
        })
    }

    // favourite song
    suspend fun getFavouriteSongsFromLocal() = withContext(Dispatchers.IO) {
        songLocalService.getFavouriteSongs()
    }

    suspend fun insertFavouriteSongsToLocal(list: List<Song>) = withContext(Dispatchers.IO) {
        songLocalService.insertFavouriteSongs(list.map {
            it.toFavouriteSongEntity()
        })
    }

    suspend fun insertFavouriteSongToLocal(song: Song) = withContext(Dispatchers.IO) {
        songLocalService.insertFavouriteSong(song.toFavouriteSongEntity())
    }

    suspend fun deleteAllFavouritesFromLocal() = withContext(Dispatchers.IO) {
        songLocalService.deleteAllFavourites()
    }

    suspend fun deleteFavouriteSongFromLocal(song: Song) = withContext(Dispatchers.IO) {
        songLocalService.deleteFavouriteSong(song.toFavouriteSongEntity())
    }
}