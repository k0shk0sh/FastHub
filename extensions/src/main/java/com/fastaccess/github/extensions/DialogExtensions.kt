package com.fastaccess.github.extensions

import android.content.Context
import androidx.appcompat.app.AlertDialog


fun Context.showYesNoDialog(title: Int, callback: (yesOrNo: Boolean) -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(R.string.confirm_message)
        .setNegativeButton(R.string.no) { dialog, _ ->
            callback.invoke(false)
            dialog.dismiss()
        }
        .setPositiveButton(R.string.yes) { dialog, _ ->
            callback.invoke(true)
            dialog.dismiss()
        }
        .show()
}