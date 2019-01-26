package com.fastaccess.github.ui.widget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.fastaccess.github.R
import com.fastaccess.github.utils.*
import kotlinx.android.synthetic.main.icon_dialog_layout.*


/**
 * Created by Kosh on 26.01.19.
 */
class IconDialogFragment : DialogFragment() {

    private var callback: IconDialogClickListener? = null

    private val bundleDrawable by lazy { arguments?.getInt(EXTRA) ?: com.fastaccess.github.R.drawable.ic_info_outline }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), requireContext().theme)
        val themeAwareInflater = inflater.cloneInContext(contextThemeWrapper)
        return themeAwareInflater.inflate(com.fastaccess.github.R.layout.icon_dialog_layout, container, false)

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
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
            arguments = bundleOf(EXTRA to drawable,
                EXTRA_TWO to title,
                EXTRA_THREE to description,
                EXTRA_FOUR to positiveBtnText,
                EXTRA_FIVE to negativeBtnText)
            show(fragmentManager, this::class.java.simpleName)
        }
    }

    interface IconDialogClickListener {
        fun onClick(positive: Boolean)
    }
}