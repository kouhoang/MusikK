package com.kouhoang.music.ui.device_songs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.ACTION_START
import com.kouhoang.music.common.TITLE_DEVICE_SONGS
import com.kouhoang.music.common.hideKeyboard
import com.kouhoang.music.common.sdk33AndUp
import com.kouhoang.music.common.sendDataToService
import com.kouhoang.music.common.sendListSongToService
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.FragmentDeviceSongsBinding
import com.kouhoang.music.ui.common.adapter.SongAdapter
import com.kouhoang.music.ui.common.myinterface.IClickSongListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceSongsFragment : BaseMainFragment<FragmentDeviceSongsBinding>() {

    override val TAG = DeviceSongsFragment::class.java.simpleName.toString()

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                mainViewModel?.currentSong?.value?.let { playMusic(it) }
            } else {
                Toast.makeText(
                    mainActivity,
                    "You need allow this app to send notification to start playing music",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override val layoutRes: Int
        get() = R.layout.fragment_device_songs

    override fun init() {
        super.init()
        binding?.lifecycleOwner = viewLifecycleOwner
        binding?.viewModel = songViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerViewSongs()

        registerObserverFetchDataFinish()

        setOnClickBtSearchView()
    }

    private fun setRecyclerViewSongs() {
        songAdapter = SongAdapter(requireContext(), object : IClickSongListener {
            override fun onClickSong(song: Song) {
                mainViewModel?.currentSong?.postValue(song)
                sdk33AndUp {
                    requestPermissionPostNotification(song)
                } ?: playMusic(song)
            }

            override fun onLongClickSong(song: Song) {
            }
        })
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding?.rcvListSongs?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(decoration)
            adapter = songAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        hideKeyboard(requireContext(), binding?.root)
                    }
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionPostNotification(song: Song) {
        if (requireContext().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            playMusic(song)
        } else {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun playMusic(song: Song) {
        mainViewModel?.apply {
            isShowMusicPlayer.postValue(true)
            isServiceRunning.postValue(true)
            currentListName = TITLE_DEVICE_SONGS
        }
        songViewModel?.deviceSongs?.value?.let { sendListSongToService(requireContext(), it) }
        sendDataToService(requireContext(), song, ACTION_START)
    }

    private fun registerObserverFetchDataFinish() {
        songViewModel?.deviceSongs?.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                songViewModel?.isLoadingDevice?.postValue(false)
                songAdapter.setData(it)
            }
        }
    }

    private fun setOnClickBtSearchView() {
        binding?.edtSearch?.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard(requireContext(), binding?.root)
                    return@setOnEditorActionListener true
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    songAdapter.filter.filter(s)
                }
            })
        }
    }
}