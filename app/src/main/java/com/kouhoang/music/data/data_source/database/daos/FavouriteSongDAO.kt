package com.kouhoang.music.data.data_source.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.kouhoang.music.data.data_source.database.entities.FavouriteSongEntity

@Dao
interface FavouriteSongDAO {

    @androidx.room.Query("select * from favourite_songs")
    suspend fun getAllSongs(): List<FavouriteSongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<FavouriteSongEntity>)

    @androidx.room.Query("delete from favourite_songs")
    suspend fun deleteAllSongs()

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = FavouriteSongEntity::class)
    suspend fun insertSong(song: FavouriteSongEntity)

    @Delete(FavouriteSongEntity::class)
    suspend fun deleteSong(song: FavouriteSongEntity)
}