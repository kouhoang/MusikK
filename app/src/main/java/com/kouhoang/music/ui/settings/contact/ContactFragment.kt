package com.kouhoang.music.ui.settings.contact

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.kouhoang.music.R
import com.kouhoang.music.base.fragment.BaseMainFragment
import com.kouhoang.music.common.EMAIL_CONTACT
import com.kouhoang.music.common.MICROSOFT_CONTACT
import com.kouhoang.music.common.TITLE_CONTACT
import com.kouhoang.music.common.URI_FB
import com.kouhoang.music.common.URI_ZALO
import com.kouhoang.music.databinding.DialogMailBinding
import com.kouhoang.music.databinding.FragmentContactBinding

class ContactFragment : BaseMainFragment<FragmentContactBinding>() {

    override val layoutRes: Int
        get() = R.layout.fragment_contact

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawUI()

        setOnClickListener()
    }

    private fun drawUI() {
        binding?.let {
            Glide.with(this).load(R.drawable.img_fb).into(it.imgFacebook)
            Glide.with(this).load(R.drawable.img_zalo).into(it.imgZalo)
            Glide.with(this).load(R.drawable.img_gmail).into(it.imgGmail)
            Glide.with(this).load(R.drawable.img_microsoft).into(it.imgMicrosoft)
        }
    }

    private fun setOnClickListener() {
        binding?.apply {
            itemFacebook.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(URI_FB)
                startActivity(intent)
            }

            itemZalo.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(URI_ZALO)
                startActivity(intent)
            }

            itemGmail.setOnClickListener {
                showDialog(EMAIL_CONTACT)
            }

            itemMicrosoft.setOnClickListener {
                showDialog(MICROSOFT_CONTACT)
            }
        }
    }

    private fun showDialog(title: String) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogMailBinding = DialogMailBinding.inflate(layoutInflater)
        dialogMailBinding.tvEmail.text = title
        dialogMailBinding.tvEmail.isSelected = true
        dialogMailBinding.btCopyEmail.setOnClickListener {
            val clipboard =
                mainActivity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Email", dialogMailBinding.tvEmail.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(mainActivity, "Email copied", Toast.LENGTH_SHORT).show()
        }
        builder.setView(dialogMailBinding.root)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        mainActivity?.let {
            it.binding.toolBarTitle.text = TITLE_CONTACT
        }
    }
}