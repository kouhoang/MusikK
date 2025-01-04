package com.kouhoang.music.ui.music_player

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.ACTION_CLEAR
import com.kouhoang.music.common.ACTION_LOOP
import com.kouhoang.music.common.ACTION_NEXT
import com.kouhoang.music.common.ACTION_PAUSE
import com.kouhoang.music.common.ACTION_PREVIOUS
import com.kouhoang.music.common.ACTION_RESUME
import com.kouhoang.music.common.ACTION_SHUFFLE
import com.kouhoang.music.common.getBitmapFromUri
import com.kouhoang.music.common.isSongExists
import com.kouhoang.music.common.sendActionToService
import com.kouhoang.music.common.sendCurrentTimeToService
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.FragmentMusicPlayerBinding

class MusicPlayerFragment : BaseMainFragment<FragmentMusicPlayerBinding>() {

    override val TAG = MusicPlayerFragment::class.java.simpleName.toString()

    private lateinit var runnable: Runnable

    override val layoutRes: Int
        get() = R.layout.fragment_music_player

    override fun init() {
        super.init()
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = mainViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()

        setClickButtonListener()
    }

    private fun registerObserver() {
        mainViewModel?.apply {
            isPlaying.observe(viewLifecycleOwner) {
                if (it) {
                    startAnimation()
                } else {
                    stopAnimation()
                }
            }

            actionMusic.observe(viewLifecycleOwner) {
                when (it) {
                    ACTION_CLEAR -> {}

                    else -> mainViewModel?.currentSong?.value?.let { song ->
                        setLayoutForMusicPlayer(song)
                    }
                }
            }

            isPlaying.observe(viewLifecycleOwner) {
                if (it) {
                    binding?.imgPlay?.setImageResource(R.drawable.ic_pause)
                    startAnimation()
                } else {
                    binding?.imgPlay?.setImageResource(R.drawable.ic_play)
                    stopAnimation()
                }
            }

            isLooping.observe(viewLifecycleOwner) {
                if (it) {
                    binding?.btLoop?.setImageResource(R.drawable.ic_is_looping)
                } else {
                    binding?.btLoop?.setImageResource(R.drawable.ic_loop)
                }
            }

            isShuffling.observe(viewLifecycleOwner) {
                if (it) {
                    binding?.btShuffle?.setImageResource(R.drawable.ic_is_shuffling)
                } else {
                    binding?.btShuffle?.setImageResource(R.drawable.ic_shuffle)
                }
            }

            currentTime.observe(viewLifecycleOwner) {
                val current = mainViewModel?.currentTime?.value
                if (current != null) {
                    binding?.seekBarMusic?.progress = current
                    val minutes: Int = current / 1000 / 60
                    val seconds: Int = current / 1000 % 60

                    @SuppressLint("DefaultLocale")
                    val str = String.format("%02d:%02d", minutes, seconds)
                    binding?.tvCurrentTime?.text = str
                }
            }

            finalTime.observe(viewLifecycleOwner) {
                val final = mainViewModel?.finalTime?.value
                if (final != null) {
                    binding?.seekBarMusic?.max = final
                    val minutes: Int = final / 1000 / 60
                    val seconds: Int = final / 1000 % 60

                    @SuppressLint("DefaultLocale")
                    val str = String.format("%02d:%02d", minutes, seconds)
                    binding?.tvFinalTime?.text = str
                }
            }
        }

        songViewModel?.apply {
            favouriteSongs.observe(viewLifecycleOwner) {
                setUIAddFavourites(null)
            }
        }
    }

    private fun setClickButtonListener() {
        binding?.apply {
            imgPrevious.setOnClickListener {
                sendActionToService(requireContext(), ACTION_PREVIOUS)
            }
            imgPlay.setOnClickListener {
                if (mainViewModel?.isPlaying?.value == true) {
                    sendActionToService(requireContext(), ACTION_PAUSE)
                } else {
                    sendActionToService(requireContext(), ACTION_RESUME)
                }
            }

            imgNext.setOnClickListener {
                sendActionToService(requireContext(), ACTION_NEXT)
            }

            btLoop.setOnClickListener {
                sendActionToService(requireContext(), ACTION_LOOP)
            }

            btShuffle.setOnClickListener {
                sendActionToService(requireContext(), ACTION_SHUFFLE)
            }

            btAddFavourites.setOnClickListener {
                onClickAddFavourites()
            }
        }
    }

    private fun onClickAddFavourites() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                context,
                "You must sign in to use this feature.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        songViewModel?.apply {
            if (
                isSongExists(
                    favouriteSongs.value,
                    mainViewModel?.currentSong?.value
                )
            ) {
                mainViewModel?.currentSong?.value?.let {
                    removeSongFromFavourites(it)
                }
            } else {
                mainViewModel?.currentSong?.value?.let {
                    addSongToFavourites(it)
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setLayoutForMusicPlayer(song: Song) {
        binding?.apply {
            tvMusicName.isSelected = true
            tvSinger.isSelected = true
            if (song.imageUri != null) {
                val imageUri = Uri.parse(song.imageUri)
                Glide.with(this@MusicPlayerFragment).load(imageUri).into(circleImageView)
                Glide.with(this@MusicPlayerFragment).load(imageUri).into(imgBackGround)
            } else {
                val bitmap = getBitmapFromUri(requireContext(), song.resourceUri)
                if (bitmap == null) {
                    binding?.let {
                        it.imgBackGround.setImageResource(R.drawable.icon_app)
                        it.circleImageView.setImageResource(R.drawable.icon_app)
                    }
                } else {
                    binding?.let {
                        it.imgBackGround.setImageBitmap(bitmap)
                        it.circleImageView.setImageBitmap(bitmap)
                    }
                }
            }
        }
        setUIAddFavourites(song)
        setSeekBarStatus()
    }

    @SuppressLint("SetTextI18n")
    private fun setUIAddFavourites(song: Song?) {
        if (song != null) {
            if (song.resourceUri?.contains("https://firebasestorage.googleapis.com") == false) {
                binding?.btAddFavourites?.visibility = View.GONE
                return
            }
        }
        binding?.apply {
            if (
                isSongExists(
                    songViewModel?.favouriteSongs?.value,
                    mainViewModel?.currentSong?.value
                )
            ) {
                btAddFavourites.setImageResource(R.drawable.ic_favourite_red)
            } else {
                btAddFavourites.setImageResource(R.drawable.ic_favourite_bored_red)
            }
        }
    }

    private fun setSeekBarStatus() {
        binding?.seekBarMusic?.apply {

            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        // gui current time lai cho service
                        sendCurrentTimeToService(requireContext(), progress)
                        if (mainViewModel?.isPlaying?.value == false) {
                            sendActionToService(requireContext(), ACTION_RESUME)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun startAnimation() {
        runnable = object : Runnable {
            override fun run() {
                binding
                    ?.circleImageView
                    ?.animate()
                    ?.rotationBy(360f)
                    ?.withEndAction(this)
                    ?.setDuration(10000)
                    ?.setInterpolator(LinearInterpolator())
                    ?.start()
            }
        }
        binding
            ?.circleImageView
            ?.animate()
            ?.rotationBy(360f)
            ?.withEndAction(runnable)
            ?.setDuration(10000)
            ?.setInterpolator(LinearInterpolator())
            ?.start()
    }

    private fun stopAnimation() {
        binding?.circleImageView?.animate()?.cancel()
    }

    override fun onDestroyView() {
        binding?.circleImageView?.animate()?.cancel()
//        binding?.circleImageView?.removeCallbacks(runnable)
        super.onDestroyView()
    }
}