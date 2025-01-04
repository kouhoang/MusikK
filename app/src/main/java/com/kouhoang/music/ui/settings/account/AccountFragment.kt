package com.kouhoang.music.ui.settings.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.GUEST
import com.kouhoang.music.common.GUEST_EMAIL
import com.kouhoang.music.common.TITLE_ACCOUNT
import com.kouhoang.music.common.TITLE_FAVOURITE_SONGS
import com.kouhoang.music.common.sendListSongToService
import com.kouhoang.music.data.models.Song
import com.kouhoang.music.databinding.FragmentAccountBinding

class AccountFragment : BaseMainFragment<FragmentAccountBinding>() {

    override val TAG = AccountFragment::class.java.simpleName.toString()
    override val layoutRes: Int
        get() = R.layout.fragment_account

    override fun init() {
        super.init()
        binding?.viewModel = accountViewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        registerObserver()
    }

    private fun initListener() {
        binding?.apply {
            btSignIn.setOnClickListener {
                mainActivity?.goToLoginActivity()
            }

            btChangeProfile.setOnClickListener {
                val action = AccountFragmentDirections.actionAccountFragmentToProfileFragment()
                findNavController().navigate(action)
            }

            btChangePassword.setOnClickListener {
                val action =
                    AccountFragmentDirections.actionAccountFragmentToChangePasswordFragment()
                findNavController().navigate(action)
            }

            btSignOut.setOnClickListener {
                onClickSignOut()
            }
        }
    }

    private fun onClickSignOut() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                Firebase.auth.signOut()
                accountViewModel?.user?.value = null
                songViewModel?.favouriteSongs?.value = null
                songViewModel?.deleteAllFavourites()
                if (mainViewModel?.currentListName == TITLE_FAVOURITE_SONGS) {
                    val list = mutableListOf<Song>()
                    sendListSongToService(requireContext(), list)
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .show()
    }

    private fun registerObserver() {
        accountViewModel?.user?.observe(viewLifecycleOwner) { user ->
            accountViewModel?.isShowSignOut?.postValue(user != null)
            if (user != null) {
                binding?.apply {
                    Glide.with(this@AccountFragment).load(user.photoUrl)
                        .error(R.drawable.img_avatar_error)
                        .into(imgUser)
                    tvName.text = user.displayName
                    tvEmail.text = user.email

                    songViewModel?.getFavouriteSongsFromRemote()
                }
            } else {
                binding?.apply {
                    Glide.with(this@AccountFragment).load(R.drawable.img_avatar_error)
                        .into(imgUser)
                    tvName.text = GUEST
                    tvEmail.text = GUEST_EMAIL
                }

                songViewModel?.favouriteSongs?.value = null
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity?.let {
            it.binding.toolBarTitle.text = TITLE_ACCOUNT
        }

        val user = FirebaseAuth.getInstance().currentUser
        accountViewModel?.user?.postValue(user)
    }
}