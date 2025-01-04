package com.kouhoang.music.data.data_source.api.dto

data class SongDto(
    val name: String,
    val singer: String,
    val resourceUri: String,
    val imageUri: String
)