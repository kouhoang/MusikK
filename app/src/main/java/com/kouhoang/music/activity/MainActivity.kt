package com.kouhoang.music.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.kouhoang.music.R
import com.kouhoang.music.base.activity.BaseActivity
import com.kouhoang.music.common.ACTION_CLEAR
import com.kouhoang.music.common.ACTION_NEXT
import com.kouhoang.music.common.ACTION_PAUSE
import com.kouhoang.music.common.ACTION_PREVIOUS
import com.kouhoang.music.common.ACTION_RELOAD_DATA
import com.kouhoang.music.common.ACTION_RESUME
import com.kouhoang.music.common.ACTION_START
import com.kouhoang.music.common.SEND_CURRENT_TIME
import com.kouhoang.music.common.SEND_DATA
import com.kouhoang.music.common.TITLE_ACCOUNT
import com.kouhoang.music.common.TITLE_APPEARANCE
import com.kouhoang.music.common.TITLE_APP_INFO
import com.kouhoang.music.common.TITLE_CONTACT
import com.kouhoang.music.common.TITLE_DEVICE_SONGS
import com.kouhoang.music.common.TITLE_FAVOURITE_SONGS
import com.kouhoang.music.common.TITLE_HOME
import com.kouhoang.music.common.TITLE_MUSIC_PLAYER
import com.kouhoang.music.common.TITLE_ONLINE_SONGS
import com.kouhoang.music.common.TITLE_SETTINGS
import com.kouhoang.music.common.getBitmapFromUri
import com.kouhoang.music.common.hideKeyboard
import com.kouhoang.music.common.isSongExists
import com.kouhoang.music.common.sendActionToService
import com.kouhoang.music.common.sendDataToService
import com.kouhoang.music.common.sendListSongToService
import com.kouhoang.music.data.data_source.app_data.DataLocalManager
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.ActivityMainBinding
import com.kouhoang.music.ui.common.adapter.FragmentViewPagerAdapter
import com.kouhoang.music.ui.common.viewmodel.MainViewModel
import com.kouhoang.music.ui.common.viewmodel.SongViewModel
import com.kouhoang.music.ui.music_player.MusicPlayerFragment
import com.kouhoang.music.ui.transformer.ZoomOutPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import java.util.Random
import javax.inject.Inject
import kotlin.text.Typography.dagger

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val TAG = MainActivity::class.java.simpleName.toString()

    lateinit var binding: ActivityMainBinding

    private lateinit var mainViewModel: MainViewModel

    private var songViewModel: SongViewModel? = null

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var dataLocalManager: DataLocalManager

    private val serviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                mainViewModel.receiveDataFromReceiver(intent)
            }
        }
    }

    private val currentTimeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                mainViewModel.receiveCurrentTime(intent)
            }
        }
    }

    private val activityResultLauncherGetData =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                songViewModel?.getLocalData()
                Toast.makeText(
                    this,
                    "You can see your list songs from device in Device Songs tab.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "You need allow this app to access music and audio to get music on device",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val activityResultLauncherNotification =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                playMusic()
            } else {
                Toast.makeText(
                    this,
                    "You need allow this app to send notification to start playing music",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        checkServiceIsRunning()

        setBottomNavigationWithViewPager()

        registerButtonListener()

        registerObserver()

        onBackPressCallBack()
    }

    private fun init() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        songViewModel = ViewModelProvider(this)[SongViewModel::class.java]
        requestPermissionReadStorage()
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(serviceReceiver, IntentFilter(SEND_DATA))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(currentTimeReceiver, IntentFilter(SEND_CURRENT_TIME))
    }

    private fun requestPermissionReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) {
                songViewModel?.getLocalData()
            } else {
                activityResultLauncherGetData.launch(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                songViewModel?.getLocalData()
            } else {
                activityResultLauncherGetData.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun checkServiceIsRunning() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)
        for (serviceInfo in runningServices) {
            if (serviceInfo.service.className
                == "com.vuongvanduy.music.service.MusicService"
            ) {
                sendActionToService(this, ACTION_RELOAD_DATA)
                break
            }
        }
    }

    private fun setBottomNavigationWithViewPager() {
        val viewPagerAdapter = FragmentViewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = viewPagerAdapter
            isUserInputEnabled = false
            setPageTransformer(ZoomOutPageTransformer())
        }


        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setLayout(0, getColor(R.color.home_bg_color), TITLE_HOME)
                R.id.online -> setLayout(1, getColor(R.color.online_bg_color), TITLE_ONLINE_SONGS)
                R.id.favourite -> setLayout(
                    2,
                    getColor(R.color.favourite_bg_color),
                    TITLE_FAVOURITE_SONGS
                )

                R.id.device -> setLayout(3, getColor(R.color.device_bg_color), TITLE_DEVICE_SONGS)
                R.id.settings -> setLayout(4, getColor(R.color.settings_bg_color), TITLE_SETTINGS)
            }
            true
        }
        binding.bottomNav.selectedItemId = R.id.home
    }

    private fun setLayout(item: Int, color: Int, title: String) {
        binding.apply {
            viewPager.currentItem = item
            bottomNav.setBackgroundColor(color)
            toolBar.setBackgroundColor(color)
            toolBarTitle.text = title
        }
        window.statusBarColor = color

        when (item) {
            1, 2, 3 -> mainViewModel.isShowBtPlayAll.postValue(true)
            else -> mainViewModel.isShowBtPlayAll.postValue(false)
        }
    }

    private fun registerButtonListener() {
        binding.apply {

            imgFavourite.setOnClickListener {
                onClickAddFavourites()
            }

            imgBack.setOnClickListener {
                onBackPressedCallback.handleOnBackPressed()
            }

            imgPlay.setOnClickListener {
                if (mainViewModel.isPlaying.value == true) {
                    sendActionToService(this@MainActivity, ACTION_PAUSE)
                } else {
                    sendActionToService(this@MainActivity, ACTION_RESUME)
                }
            }

            imgPrevious.setOnClickListener {
                sendActionToService(this@MainActivity, ACTION_PREVIOUS)
            }

            imgNext.setOnClickListener {
                sendActionToService(this@MainActivity, ACTION_NEXT)
            }

            imgClear.setOnClickListener {
                sendActionToService(this@MainActivity, ACTION_CLEAR)
            }

            miniPlayer.setOnClickListener {
                mainViewModel.isShowMusicPlayer.postValue(true)
                if (mainViewModel.isPlaying.value == false) {
                    sendActionToService(this@MainActivity, ACTION_RESUME)
                }
            }

            btPlayAll.setOnClickListener {
                requestPermissionPostNotification()
            }

            layoutMain.setOnClickListener {
                hideKeyboard(this@MainActivity, binding.root)
            }
        }
    }

    private fun requestPermissionPostNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                playMusic()
            } else {
                activityResultLauncherNotification.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            playMusic()
        }
    }

    private fun playMusic() {
        when (binding.bottomNav.selectedItemId) {
            R.id.online -> {

                if (!songViewModel?.onlineSongs?.value.isNullOrEmpty()) {
                    mainViewModel.currentListName = TITLE_ONLINE_SONGS
                    val list = songViewModel?.onlineSongs?.value as MutableList<Song>
                    playList(list)
                }
            }

            R.id.favourite -> {
                if (!songViewModel?.favouriteSongs?.value.isNullOrEmpty()) {
                    mainViewModel.currentListName = TITLE_FAVOURITE_SONGS
                    val list = songViewModel?.favouriteSongs?.value as MutableList<Song>
                    playList(list)
                }
            }

            R.id.device -> {
                if (!songViewModel?.deviceSongs?.value.isNullOrEmpty()) {
                    mainViewModel.currentListName = TITLE_DEVICE_SONGS
                    val list = songViewModel?.deviceSongs?.value as MutableList<Song>
                    playList(list)
                }
            }

            else -> {
            }
        }
    }

    private fun playList(list: MutableList<Song>) {
        val index: Int = if (mainViewModel.isShuffling.value == true) {
            Random().nextInt(list.size)
        } else {
            0
        }
        sendListSongToService(this, list)
        sendDataToService(this, list[index], ACTION_START)
        mainViewModel.isShowMusicPlayer.postValue(true)
        mainViewModel.isServiceRunning.postValue(true)
    }

    @SuppressLint("CommitTransaction")
    private fun registerObserver() {
        mainViewModel.apply {
            isServiceRunning.observe(this@MainActivity) { isRunning ->
                isShowMiniPlayer.postValue(isRunning && isShowMusicPlayer.value == false)
            }

            isShowMusicPlayer.observe(this@MainActivity) { isShow ->
                if (!isShow) {

                    isShowMiniPlayer.postValue(isServiceRunning.value)

                    supportFragmentManager.popBackStack()

                    setToolbarTitle()

                    when (binding.bottomNav.selectedItemId) {
                        R.id.online, R.id.favourite, R.id.device ->
                            isShowBtPlayAll.postValue(true)
                    }
                } else {
                    //replace fragment
                    replaceMusicPlayer()

                    isShowMiniPlayer.postValue(false)
                    isShowBtPlayAll.postValue(false)

                    binding.toolBarTitle.text = TITLE_MUSIC_PLAYER

                    hideKeyboard(this@MainActivity, binding.root)
                }
            }

            actionMusic.observe(this@MainActivity) { value ->
                when (value) {
                    ACTION_START, ACTION_NEXT, ACTION_PREVIOUS ->
                        currentSong.value?.let {
                            setLayoutMiniPlayer(it)
                        }

                    ACTION_CLEAR -> {
                        isPlaying.postValue(false)
                        isServiceRunning.postValue(false)
                        popMusicPlayer()
                    }

                    ACTION_RELOAD_DATA -> {
                        currentSong.value?.let {
                            setLayoutMiniPlayer(it)
                        }
                        isShowMiniPlayer.postValue(true)
                        isServiceRunning.postValue(true)
                    }

                    else -> {}
                }
            }

            isPlaying.observe(this@MainActivity) {
                if (it) {
                    binding.imgPlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.imgPlay.setImageResource(R.drawable.ic_play)
                }
            }

            currentTime.observe(this@MainActivity) {
                val current = mainViewModel.currentTime.value
                if (current != null) {
                    binding.progressBar.progress = current
                }
            }

            themeMode.observe(this@MainActivity) {
                mainViewModel.themeMode.value?.let { dataLocalManager.putStringThemeMode(it) }
            }
        }

        songViewModel?.apply {
            favouriteSongs.observe(this@MainActivity) {
                setUIAddFavourites(null)
            }
        }
    }

    private fun setToolbarTitle() {
        when (binding.bottomNav.selectedItemId) {
            R.id.home -> binding.toolBarTitle.text = TITLE_HOME
            R.id.online -> binding.toolBarTitle.text = TITLE_ONLINE_SONGS
            R.id.favourite -> binding.toolBarTitle.text = TITLE_FAVOURITE_SONGS
            R.id.device -> binding.toolBarTitle.text = TITLE_DEVICE_SONGS
            R.id.settings -> setToolbarTitleSettings()
        }
    }

    private fun setToolbarTitleSettings() {
        if (isFragmentInBackStack(R.id.accountFragment)) {
            binding.toolBarTitle.text = TITLE_ACCOUNT
        } else if (isFragmentInBackStack(R.id.appearanceFragment)) {
            binding.toolBarTitle.text = TITLE_APPEARANCE
        } else if (isFragmentInBackStack(R.id.appInfoFragment)) {
            binding.toolBarTitle.text = TITLE_APP_INFO
        } else if (isFragmentInBackStack(R.id.contactFragment)) {
            binding.toolBarTitle.text = TITLE_CONTACT
        } else {
            binding.toolBarTitle.text = TITLE_SETTINGS
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setLayoutMiniPlayer(song: Song) {
        // set layout
        binding.apply {
            if (song.imageUri != null) {
                val imageUri = Uri.parse(song.imageUri)
                Glide.with(this@MainActivity).load(imageUri).into(imgMusic)
                Glide.with(this@MainActivity).load(imageUri).into(imgBgMiniPlayer)
            } else {
                val bitmap = getBitmapFromUri(this@MainActivity, song.resourceUri)
                Glide.with(this@MainActivity).load(bitmap).into(imgMusic)
                    .onLoadFailed(getDrawable(R.drawable.icon_app))
                Glide.with(this@MainActivity).load(bitmap).into(imgBgMiniPlayer)
                    .onLoadFailed(getDrawable(R.drawable.icon_app))
            }
            setUIAddFavourites(song)
            tvMusicName.isSelected = true
            tvSinger.isSelected = true
            progressBar.isEnabled = false
        }
    }

    private fun setUIAddFavourites(song: Song?) {
        if (song != null) {
            if (song.resourceUri?.contains("https://firebasestorage.googleapis.com") == false) {
                binding.imgFavourite.visibility = View.GONE
                return
            }
        }
        binding.apply {
            if (
                isSongExists(
                    songViewModel?.favouriteSongs?.value,
                    mainViewModel.currentSong.value
                )
            ) {
                imgFavourite.setImageResource(R.drawable.ic_favourite_mini_player)
            } else {
                imgFavourite.setImageResource(R.drawable.ic_favourite_bored)
            }
        }
    }

    private fun onClickAddFavourites() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(
                this,
                "You must sign in to use this feature.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        songViewModel?.apply {
            if (
                isSongExists(
                    favouriteSongs.value,
                    mainViewModel.currentSong.value
                )
            ) {
                mainViewModel.currentSong.value?.let {
                    removeSongFromFavourites(it)
                }
            } else {
                mainViewModel.currentSong.value?.let {
                    addSongToFavourites(it)
                }
            }
        }
    }

    private fun replaceMusicPlayer() {
        val slideInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)

        slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.layout_music_player, MusicPlayerFragment())
                    .addToBackStack("MusicPlayerFragment")
                    .commit()
            }

            override fun onAnimationEnd(animation: Animation) {
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        binding.layoutMusicPlayer.startAnimation(slideInAnimation)
    }

    private fun onBackPressCallBack() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (mainViewModel.isShowMusicPlayer.value == true) {
                    popMusicPlayer()
                } else if (binding.bottomNav.selectedItemId != R.id.home) {

                    if (isSettingsFragment()) {

                        val navController = findNavController(R.id.nav_host_fragment)
                        if (!navController.popBackStack()) {
                            binding.bottomNav.selectedItemId = R.id.home
                        } else {
                            if (isSettingOptionsFragment()) {
                                binding.toolBarTitle.text = TITLE_SETTINGS
                            }
                        }
                    } else {
                        binding.bottomNav.selectedItemId = R.id.home
                    }
                } else {
                    finish()
                }
            }
        }
    }

    private fun isSettingsFragment() = binding.bottomNav.selectedItemId == R.id.settings

    @SuppressLint("RestrictedApi")
    private fun isSettingOptionsFragment(): Boolean {
        if (!isSettingsFragment()) {
            return false
        }
        val navController = findNavController(R.id.nav_host_fragment)
        return (navController.currentDestination
                == navController.findDestination(R.id.settingOptionsFragment))
    }

    private fun isFragmentInBackStack(destinationId: Int) =
        try {
            findNavController(R.id.nav_host_fragment).getBackStackEntry(destinationId)
            true
        } catch (e: Exception) {
            false
        }

    private fun popMusicPlayer() {

        val slideOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)

        slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                mainViewModel.isShowMusicPlayer.postValue(false)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.layoutMusicPlayer.startAnimation(slideOutAnimation)
    }

    fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onPause() {
        super.onPause()
        onBackPressedCallback.remove()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(currentTimeReceiver)
        songViewModel = null
    }
}