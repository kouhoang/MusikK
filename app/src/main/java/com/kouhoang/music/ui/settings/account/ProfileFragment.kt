package com.kouhoang.music.ui.settings.account

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.kouhoang.music.R
import com.kouhoang.music.base.dialogs.ProgressDialog
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.TITLE_ACCOUNT
import com.kouhoang.music.common.hideKeyboard
import com.kouhoang.music.databinding.FragmentProfileBinding


class ProfileFragment : BaseMainFragment<FragmentProfileBinding>() {

//    private lateinit var binding: FragmentProfileBinding

    private var uriImage: Uri? = null

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Log.e("FRAGMENT_NAME", "Permission denied")
            }
        }

    private val activityResultGetImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data ?: return@registerForActivityResult
                val uri = intent.data
                if (uri != null) {
                    uriImage = uri
                }
            }
        }
    override val layoutRes: Int
        get() = R.layout.fragment_profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()

        initListener()
    }

    private fun registerObserver() {
        accountViewModel?.user?.observe(viewLifecycleOwner) { user ->
            accountViewModel?.isShowSignOut?.postValue(user != null)

            if (user != null) {
                binding?.apply {
                    Glide.with(this@ProfileFragment).load(user.photoUrl)
                        .error(R.drawable.img_avatar_error)
                        .into(imgUser)
                    edtName.setText(user.displayName)
                    tvEmail.text = user.email
                }
            }
        }
    }

    private fun initListener() {
        binding?.imgUser?.setOnClickListener {
            requestPermissionReadStorage()
        }
        binding?.btUpdateProfile?.setOnClickListener {
            onClickUpdateProfile()
        }
    }

    private fun requestPermissionReadStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                // get list song from device and send to music device fragment
                openGallery()
            } else {
                activityResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // get list song from device and send to music device fragment
                openGallery()
            } else {
                activityResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        activityResultGetImage
            .launch(Intent.createChooser(intent, "Select picture"))
    }

    @SuppressLint("SetTextI18n")
    private fun onClickUpdateProfile() {
        val dialog = ProgressDialog(requireContext(), "Updating...")
        hideKeyboard(requireContext(), binding?.root)
        val name = binding?.edtName?.text?.trim().toString()
        if (name.isEmpty() || name.isBlank()) {
            binding?.tvError?.text = "Name can't blank"
            binding?.tvError?.visibility = View.VISIBLE
            return
        }
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val profileUpdates = userProfileChangeRequest {
            displayName = name
            if (uriImage != null) {
                photoUri = uriImage
            }
        }
        dialog.show()
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                dialog.dismiss()
                if (task.isSuccessful) {
                    Toast.makeText(
                        mainActivity,
                        "Update profile success.",
                        Toast.LENGTH_LONG
                    ).show()
                    accountViewModel?.user?.postValue(FirebaseAuth.getInstance().currentUser)
                    findNavController().popBackStack(R.id.accountFragment, false)
                }
            }
        uriImage = null
    }

    override fun onResume() {
        super.onResume()
        mainActivity?.let {
            it.binding.toolBarTitle.text = TITLE_ACCOUNT
        }

        uriImage?.let { setImageViewForUser(it) }
    }

    private fun setImageViewForUser(uri: Uri) {
        Log.e("AccountFragment", uri.toString())
        binding?.let { Glide.with(this).load(uri).into(it.imgUser) }
    }
}