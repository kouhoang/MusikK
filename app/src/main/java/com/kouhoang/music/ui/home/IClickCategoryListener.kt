package com.kouhoang.music.ui.home

import com.kouhoang.music.data.models.Song


interface IClickCategoryListener {

    fun clickButtonViewAll(categoryName: String)

    fun onClickSong(song: Song, categoryName: String)
}