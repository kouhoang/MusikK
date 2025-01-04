package com.kouhoang.music.data.common

import com.kouhoang.music.data.data_source.api.dto.SongDto
import com.kouhoang.music.data.data_source.database.entities.FavouriteSongEntity
import com.kouhoang.music.data.data_source.database.entities.SongEntity
import com.kouhoang.music.data.models.Song

fun SongEntity.toSongModel() = Song(
    name,
    singer,
    resourceUri,
    imageUri
)

fun Song.toSongEntity() = SongEntity(
    id = resourceUri.hashCode(),
    name = name!!,
    singer = singer!!,
    resourceUri = resourceUri!!,
    imageUri = imageUri!!
)

fun FavouriteSongEntity.toSongModel() = Song(
    name,
    singer,
    resourceUri,
    imageUri
)

fun Song.toFavouriteSongEntity() = FavouriteSongEntity(
    id = resourceUri.hashCode(),
    name = name!!,
    singer = singer!!,
    resourceUri = resourceUri!!,
    imageUri = imageUri!!
)

fun SongDto.toSongModel() = Song(
    name = name,
    singer = singer,
    resourceUri = resourceUri,
    imageUri = imageUri
)

fun Song.toSongDto() = SongDto(
    name = name!!,
    singer = singer!!,
    resourceUri = resourceUri!!,
    imageUri = imageUri!!
)