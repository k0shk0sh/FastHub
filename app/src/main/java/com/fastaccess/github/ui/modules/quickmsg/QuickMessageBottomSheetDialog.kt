package com.fastaccess.github.ui.modules.quickmsg

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseBottomSheetDialogFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.extensions.asString
import kotlinx.android.synthetic.main.appbar_center_title_round_background_layout.*
import kotlinx.android.synthetic.main.quick_input_layout.*

class QuickMessageBottomSheetDialog : BaseBottomSheetDialogFragment() {

    private var callback: QuickMessageCallback? = null

    override fun viewModel(): BaseViewModel? = null
    override fun layoutRes(): Int = R.layout.quick_input_layout
    override fun isFullScreen(): Boolean = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            parentFragment is QuickMessageCallback -> parentFragment as QuickMessageCallback
            context is QuickMessageCallback -> context
            else -> throw IllegalAccessException("your $parentFragment or $context must implement QuickMessageCallback")
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        toolbarTitle.text = arguments?.getString(EXTRA) ?: getString(R.string.message)
        submit.setOnClickListener {
            val text = editText.asString()
            if (text.isNotEmpty()) {
                callback?.onMessageEntered(text, arguments)
                dismiss()
            }
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, bundle: Bundle) {
            QuickMessageBottomSheetDialog().apply {
                arguments = bundle
                show(fragmentManager, "QuickMessageBottomSheetDialog")
            }
        }
    }

    interface QuickMessageCallback {
        fun onMessageEntered(msg: String, bundle: Bundle?)
    }
}