package com.kouhoang.music.data.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.kouhoang.music.base.service.BaseService
import com.kouhoang.music.common.isSongExists
import com.kouhoang.music.data.common.Response
import com.kouhoang.music.data.common.sortListAscending
import com.kouhoang.music.data.data_source.api.SongAPI
import com.kouhoang.music.data.data_source.api.dto.SongDto
import com.kouhoang.music.data.models.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SongRemoteService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songAPI: SongAPI
) : BaseService() {
    private val database = Firebase.database

    suspend fun getOnlineSongs(): Response<List<SongDto>> {
        return safeCallApi {
            songAPI.getOnlineSongs()
        }
    }

    fun getFavouriteSongsFromFirebase(callback: (List<Song>) -> Unit) {

        val list = mutableListOf<Song>()
        val email = FirebaseAuth.getInstance().currentUser?.email?.substringBefore(".")
        val myRef = email?.let {
            Firebase.database.getReference("users")
                .child(it.substringBefore("."))
                .child("favourite_songs")
        }
        myRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val song = postSnapshot.getValue<Song>()
                    if (song != null) {
                        if (!isSongExists(list, song)) {
                            list.add(song)
                        }
                    }
                }
                sortListAscending(list)
                callback(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun pushSongToFirebase(email: String, song: Song, callback: () -> Unit) {
        val myRef = database.getReference("users")
            .child(email.substringBefore("."))
            .child("favourite_songs")
        val path = song.name!!.replace("/", "|")
        myRef.child(path).setValue(song).addOnSuccessListener {
            callback()
            Toast.makeText(
                context,
                "Add song from favourites success",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun removeSongOnFirebase(email: String, song: Song, callback: () -> Unit) {
        val myRef = database.getReference("users")
            .child(email.substringBefore("."))
            .child("favourite_songs")
        val path = song.name!!.replace("/", "|")
        myRef.child(path).removeValue { databaseError, _ ->
            if (databaseError == null) {
                // Removal successful
                callback()
                Toast.makeText(
                    context,
                    "Remove song from favourites success",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Removal failed
                Toast.makeText(
                    context,
                    "Failed to remove song: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}