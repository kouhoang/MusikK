package com.kouhoang.music.data.data_source.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kouhoang.music.data.data_source.database.entities.SongEntity

@Dao
interface SongDAO {

    @Query("select * from online_songs")
    suspend fun getAllSongs(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)
}