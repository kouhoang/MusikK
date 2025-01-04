package com.kouhoang.music.data.services

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.kouhoang.music.base.service.BaseService
import com.kouhoang.music.common.DEVICE_SONGS_FRAGMENT_TAG
import com.kouhoang.music.common.isExistSameName
import com.kouhoang.music.data.common.Response
import com.kouhoang.music.data.common.sortListAscending
import com.kouhoang.music.data.data_source.database.daos.FavouriteSongDAO
import com.kouhoang.music.data.data_source.database.daos.SongDAO
import com.kouhoang.music.data.data_source.database.entities.FavouriteSongEntity
import com.kouhoang.music.data.data_source.database.entities.SongEntity
import com.kouhoang.music.data.models.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SongLocalService @Inject constructor(
    @ApplicationContext val context: Context,
    private val songDAO: SongDAO,
    private val favouriteSongDao: FavouriteSongDAO
) : BaseService() {

    @SuppressLint("Recycle")
    fun getLocalMusic(): List<Song> {
        val list = mutableListOf<Song>()
        val contentResolver: ContentResolver = context.contentResolver
        val selection = ("${MediaStore.Audio.Media.IS_MUSIC} != 0" +
                " AND ${MediaStore.Audio.Media.IS_RINGTONE} == 0" +
                " AND ${MediaStore.Audio.Media.IS_ALARM} == 0" +
                " AND ${MediaStore.Audio.Media.IS_NOTIFICATION} == 0")
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(
            uri,
            null, selection,
            null, null
        )

        if (cursor == null) {
            Toast.makeText(context, "Something Went Wrong.", Toast.LENGTH_SHORT).show()
        } else if (!cursor.moveToFirst()) {
            Log.e(DEVICE_SONGS_FRAGMENT_TAG, "No Music Found on Device")
        } else {
            //get columns
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

            do {
                val resourceId = cursor.getLong(idColumn)
                val name = cursor.getString(titleColumn)
                val singer = cursor.getString(artistColumn)

                val resourceUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, resourceId
                ).toString()

                val song = Song(name, singer, resourceUri, null)
                if (!isExistSameName(list, song)) {
                    list.add(song)
                }
            } while (cursor.moveToNext())

            sortListAscending(list)
        }
        return list
    }

    suspend fun getOnlineSongs(): Response<List<SongEntity>> {
        return safeCallDao {
            songDAO.getAllSongs()
        }
    }

    suspend fun insertOnlineSongs(list: List<SongEntity>): Response<Unit> {
        return safeCallDao {
            songDAO.insertSongs(list)
        }
    }

    suspend fun getFavouriteSongs(): Response<List<FavouriteSongEntity>> {
        return safeCallDao {
            favouriteSongDao.getAllSongs()
        }
    }

    suspend fun insertFavouriteSongs(list: List<FavouriteSongEntity>): Response<Unit> {
        return safeCallDao {
            favouriteSongDao.insertSongs(list)
        }
    }

    suspend fun insertFavouriteSong(song: FavouriteSongEntity): Response<Unit> {
        return safeCallDao {
            favouriteSongDao.insertSong(song)
        }
    }

    suspend fun deleteFavouriteSong(song: FavouriteSongEntity) {
        safeCallDao {
            favouriteSongDao.deleteSong(song)
        }
    }

    suspend fun deleteAllFavourites() {
        safeCallDao {
            favouriteSongDao.deleteAllSongs()
        }
    }
}