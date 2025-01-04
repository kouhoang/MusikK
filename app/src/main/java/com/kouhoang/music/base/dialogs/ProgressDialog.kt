package com.kouhoang.music.base.dialogs

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.kouhoang.music.R

class ProgressDialog(context: Context, message: String) {

    private val dialog = Dialog(context)

    init {
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCanceledOnTouchOutside(false)
        val messageTextView = dialog.findViewById<TextView>(R.id.message_text_view)
        messageTextView.text = message
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

}