package com.fastaccess.github.ui.modules.issuesprs.edit.labels.create

import android.content.Context
import android.os.Bundle
import android.view.View
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseDialogFragment
import com.fastaccess.github.base.extensions.asString
import com.fastaccess.github.ui.adapter.LabelColorAdapter
import kotlinx.android.synthetic.main.add_label_layout.*

/**
 * Created by Kosh on 07.03.19.
 */
class CreateLabelFragment : com.fastaccess.github.base.BaseDialogFragment() {

    private var callback: OnCreateLabelCallback? = null

    private val adapter by lazy {
        LabelColorAdapter(resources.getStringArray(R.array.label_colors).toList()) {
            color.editText?.setText(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            parentFragment is OnCreateLabelCallback -> parentFragment as OnCreateLabelCallback
            context is OnCreateLabelCallback -> context
            else -> throw IllegalAccessError("$context must impl OnCreateLabelCallback")
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun layoutRes(): Int = R.layout.add_label_layout
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.adapter = adapter
        setupToolbar(getString(R.string.create_label), R.menu.submit_menu) { item ->
            val colorText = color.editText?.asString()
            val nameText = name.editText?.asString()
            color.error = if (colorText.isNullOrEmpty()) getString(R.string.required_field) else null
            name.error = if (nameText.isNullOrEmpty()) getString(R.string.required_field) else null
            if (!colorText.isNullOrEmpty() && !nameText.isNullOrEmpty()) {
                callback?.onCreateLabel(nameText, colorText.replace("#", ""))
                dismissDialog()
            }
        }
    }

    companion object {
        fun newInstance() = CreateLabelFragment()
    }

    interface OnCreateLabelCallback {
        fun onCreateLabel(name: String, color: String)
    }
}