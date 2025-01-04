package com.kouhoang.music.ui.common.myinterface

import com.kouhoang.music.data.models.Song


interface IClickSongListener {

    fun onClickSong(song: Song)
    fun onLongClickSong(song: Song)
}