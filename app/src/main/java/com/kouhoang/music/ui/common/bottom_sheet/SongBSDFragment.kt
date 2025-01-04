package com.kouhoang.music.ui.common.bottom_sheet

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.R
import com.kouhoang.music.activity.MainActivity
import com.kouhoang.music.common.ACTION_START
import com.kouhoang.music.common.TITLE_FAVOURITE_SONGS
import com.kouhoang.music.common.TITLE_ONLINE_SONGS
import com.kouhoang.music.common.isSongExists
import com.kouhoang.music.common.sdk33AndUp
import com.kouhoang.music.common.sendDataToService
import com.kouhoang.music.common.sendListSongToService
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.LayoutSongBottomSheetBinding
import com.kouhoang.music.ui.common.viewmodel.MainViewModel
import com.kouhoang.music.ui.common.viewmodel.SongViewModel

class SongBSDFragment : BottomSheetDialogFragment() {

    private lateinit var binding: LayoutSongBottomSheetBinding

    private lateinit var songViewModel: SongViewModel

    private lateinit var mainViewModel: MainViewModel

    private lateinit var mainActivity: MainActivity

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                songViewModel.optionSong.value?.let { playMusic(it) }
            } else {
                Toast.makeText(
                    requireContext(),
                    "You need allow this app to send notification to start playing music",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("BottomSheetDialogFragment", "onCreateView")
        binding = LayoutSongBottomSheetBinding.inflate(inflater, container, false)

        mainActivity = requireActivity() as MainActivity
        songViewModel = ViewModelProvider(mainActivity)[SongViewModel::class.java]
        mainViewModel = ViewModelProvider(mainActivity)[MainViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setData()
    }

    private fun setData() {
        binding.apply {
            songViewModel.optionSong.value?.let { itSong ->
                song = itSong
                Glide.with(this@SongBSDFragment)
                    .load(Uri.parse(itSong.imageUri))
                    .into(imgSong)
            }
        }
        setUIAddFavourites()
    }

    @SuppressLint("SetTextI18n")
    private fun setUIAddFavourites() {
        binding.apply {
            if (
                isSongExists(
                    songViewModel.favouriteSongs.value,
                    songViewModel.optionSong.value
                )
            ) {
                tvAddFavourites.text = "Remove this song from your favourites"
                imgFavourite.setImageResource(R.drawable.ic_favourite_red)
            } else {
                tvAddFavourites.text = "Add this song to your favourites"
                imgFavourite.setImageResource(R.drawable.ic_favourite_bored_red)
            }

            layoutAddFavourites.setOnClickListener {
                onClickAddFavourites()
            }

            layoutPlay.setOnClickListener {
                songViewModel.optionSong.value?.let {
                    sdk33AndUp {
                        requestPermissionPostNotification(it)
                    } ?: playMusic(it)
                }
            }
        }
    }

    private fun onClickAddFavourites() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                mainActivity,
                "You must sign in to use this feature.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        songViewModel.apply {
            if (
                isSongExists(
                    favouriteSongs.value,
                    optionSong.value
                )
            ) {
                optionSong.value?.let {
                    removeSongFromFavourites(it)
                    dismiss()
                }
            } else {
                optionSong.value?.let {
                    addSongToFavourites(it)
                    dismiss()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionPostNotification(song: Song) {
        if (mainActivity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            playMusic(song)
        } else {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun playMusic(song: Song) {
        dismiss()
        mainViewModel.apply {
            isShowMusicPlayer.postValue(true)
            isServiceRunning.postValue(true)
        }

        when (mainActivity.binding.bottomNav.selectedItemId) {
            R.id.online -> {
                mainViewModel.currentListName = TITLE_ONLINE_SONGS
                songViewModel.onlineSongs.value?.let { sendListSongToService(mainActivity, it) }
            }


            R.id.favourite -> {
                mainViewModel.currentListName = TITLE_FAVOURITE_SONGS
                songViewModel.favouriteSongs.value?.let { sendListSongToService(mainActivity, it) }
            }

        }

        sendDataToService(mainActivity, song, ACTION_START)
    }
}