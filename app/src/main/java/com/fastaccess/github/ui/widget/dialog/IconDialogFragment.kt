package com.fastaccess.github.ui.widget.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseDialogFragment
import com.fastaccess.github.base.utils.*
import com.fastaccess.github.extensions.show
import kotlinx.android.synthetic.main.icon_dialog_layout.*


/**
 * Created by Kosh on 26.01.19.
 */
class IconDialogFragment : BaseDialogFragment() {
    override fun layoutRes(): Int = R.layout.icon_dialog_layout

    private var callback: IconDialogClickListener? = null

    private val bundleDrawable by lazy { arguments?.getInt(EXTRA) ?: R.drawable.ic_info_outline }
    private val bundleTitle by lazy { arguments?.getString(EXTRA_TWO) }
    private val bundleDescription by lazy { arguments?.getString(EXTRA_THREE) }
    private val bundlePositiveBtnText by lazy { arguments?.getString(EXTRA_FOUR) }
    private val bundleNegativeBtnText by lazy { arguments?.getString(EXTRA_FIVE) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            parentFragment is IconDialogClickListener -> parentFragment as IconDialogClickListener
            context is IconDialogClickListener -> context
            else -> null
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogIcon.setImageResource(bundleDrawable)
        dialogTitle.text = bundleTitle
        dialogDescription.text = bundleDescription
        dialogPositiveBtn.text = bundlePositiveBtnText
        dialogNegativeBtn.text = bundleNegativeBtnText

        dialogPositiveBtn.setOnClickListener {
            callback?.onClick(true)
            dialog?.cancel()
        }
        dialogNegativeBtn.setOnClickListener {
            callback?.onClick(false)
            dialog?.cancel()
        }
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            drawable: Int,
            title: String,
            description: String,
            positiveBtnText: String,
            negativeBtnText: String
        ): IconDialogFragment = IconDialogFragment().apply {
            arguments = bundleOf(
                EXTRA to drawable,
                EXTRA_TWO to title,
                EXTRA_THREE to description,
                EXTRA_FOUR to positiveBtnText,
                EXTRA_FIVE to negativeBtnText
            )
            show(fragmentManager)
        }
    }

    interface IconDialogClickListener {
        fun onClick(positive: Boolean)
    }
}