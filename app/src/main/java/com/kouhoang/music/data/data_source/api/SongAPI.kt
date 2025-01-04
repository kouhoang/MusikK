package com.kouhoang.music.data.data_source.api

import com.kouhoang.music.data.data_source.api.dto.SongDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SongAPI {

    @GET("/all_songs.json")
    suspend fun getOnlineSongs(): List<SongDto>

    @GET("/users/{email}/favourite_songs.json")
    suspend fun getFavouriteSongs(
        @Path("email") email: String,
        @Query("auth") idToken: String
    ): List<SongDto>

    @PUT("/users/{email}/favourite_songs/{name}.json")
    suspend fun addFavouriteSong(
        @Path("email") email: String,
        @Path("name") name: String,
        @Query("auth") idToken: String
    )

    @POST("/users/{email}/favourite_songs.json")
    suspend fun pushFavouriteSongs(
        @Path("email") email: String,
        @Query("auth") idToken: String,
        @Body songs: List<SongDto>
    )

    @DELETE("/users/{email}/favourite_songs.json")
    suspend fun deleteFavouriteSong(
        @Path("email") email: String,
        @Path("name") name: String,
        @Query("auth") idToken: String
    )
}