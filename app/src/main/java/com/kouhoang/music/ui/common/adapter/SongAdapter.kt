package com.kouhoang.music.ui.common.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kouhoang.music.R
import com.kouhoang.music.common.getBitmapFromUri
import com.kouhoang.music.common.isSongExists
import com.kouhoang.music.data.common.containsIgnoreCaseWithDiacritics
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.ItemSongBinding
import com.kouhoang.music.ui.common.myinterface.IClickSongListener

class SongAdapter(
    private val context: Context,
    private val iClickSongListener: IClickSongListener
) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>(), Filterable {

    private var songs: List<Song>? = null
    private var listSongsOld: List<Song>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Song>) {
        this.songs = list
        this.listSongsOld = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding: ItemSongBinding =
            ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (!songs.isNullOrEmpty()) {
            songs!!.size
        } else 0
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        if (songs?.isEmpty() == true) {
            return
        }
        val song = songs?.get(position)
        if (song != null) {

            holder.bind(song)

            val bitmap = getBitmapFromUri(context, song.resourceUri)
            if (bitmap != null) {
                Glide.with(context).load(bitmap).into(holder.binding.imgMusicInList)
                    .onLoadFailed(context.getDrawable(R.drawable.icon_app))
            } else {
                Glide.with(context).load(R.drawable.icon_app).into(holder.binding.imgMusicInList)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val strSearch = constraint.toString()
                songs = if (strSearch.isEmpty()) {
                    listSongsOld
                } else {
                    val list = ArrayList<Song>()
                    listSongsOld?.forEach {
                        if (it.name?.let { it1 ->
                                containsIgnoreCaseWithDiacritics(it1, strSearch)
                            } == true && !isSongExists(list, it)) {
                            list.add(it)
                        }

                        if (it.singer?.let { it1 ->
                                containsIgnoreCaseWithDiacritics(it1, strSearch)
                            } == true && !isSongExists(list, it)) {
                            list.add(it)
                        }
                    }
                    list
                }

                val filterResult = FilterResults()
                filterResult.values = songs
                return filterResult
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    songs = results.values as List<Song>
                }
                notifyDataSetChanged()
            }
        }
    }

    inner class SongViewHolder(val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.song = song
            binding.listener = iClickSongListener
            binding.executePendingBindings()
        }
    }

}