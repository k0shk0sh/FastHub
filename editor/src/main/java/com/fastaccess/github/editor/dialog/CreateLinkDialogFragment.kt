package com.fastaccess.github.editor.dialog

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.fastaccess.github.base.extensions.asString
import com.fastaccess.github.base.utils.EXTRA
import com.fastaccess.github.base.viewmodel.ViewModelProviders
import com.fastaccess.github.editor.R
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.create_link_dialog_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 2019-07-29.
 */
class CreateLinkDialogFragment : com.fastaccess.github.base.BaseDialogFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(UploadPictureViewModel::class.java) }

    private val isImage by lazy { arguments?.getBoolean(EXTRA) ?: false }
    private var callback: OnLinkSelected? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = parentFragment as OnLinkSelected // crash if it isn't ;)
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.create_link_dialog_layout

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        isImage.isTrue {
            selectImage.isVisible = true
            selectImage.setOnClickListener {
                ImagePicker.with(this)
                    .start()
            }
        }
        cancel.setOnClickListener { dismiss() }
        submit.setOnClickListener {
            val title = titleEditText.asString()
            val link = linkEditText.asString()
            if (title.isNotBlank() && link.isNotBlank()) {
                if (isImage) {
                    viewModel.upload(title, link)
                } else {
                    callback?.onLinkSelected(title, link, false)
                    dismiss()
                }
            }
        }
        observeData()
    }

    private fun observeData() {
        viewModel.progress.observeNotNull(this) {
            buttonsLayout.isVisible = !it
            progress.isVisible = it
        }
        viewModel.error.observeNotNull(this) {
            Toast.makeText(requireContext().applicationContext, it.message, Toast.LENGTH_LONG).show()
        }
        viewModel.uploadedFileLiveData.observeNotNull(this) {
            val title = titleEditText.asString()
            callback?.onLinkSelected(title, it, isImage)
            dismiss()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val file = ImagePicker.getFilePath(data) ?: return
            linkEditText.setText(file)
        }
    }

    companion object {
        fun newInstance(isImage: Boolean = false) = CreateLinkDialogFragment().apply {
            arguments = bundleOf(EXTRA to isImage)
        }
    }

    interface OnLinkSelected {
        fun onLinkSelected(
            title: String,
            link: String,
            isImage: Boolean
        )
    }
}