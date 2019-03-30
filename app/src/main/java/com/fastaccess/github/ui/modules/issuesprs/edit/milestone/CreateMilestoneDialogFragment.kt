package com.fastaccess.github.ui.modules.issuesprs.edit.milestone

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.fastaccess.datetimepicker.DatePickerFragmentDialog
import com.fastaccess.datetimepicker.callback.DatePickerCallback
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseDialogFragment
import com.fastaccess.github.extensions.DatePrettifier
import com.fastaccess.github.utils.extensions.asString
import kotlinx.android.synthetic.main.add_milestone_layout.*
import java.util.*

/**
 * Created by Kosh on 30.03.19.
 */
class CreateMilestoneDialogFragment : BaseDialogFragment(), DatePickerCallback {
    private var callback: OnAddNewMilestone? = null

    override fun layoutRes(): Int = R.layout.add_milestone_layout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = when {
            parentFragment is OnAddNewMilestone -> parentFragment as OnAddNewMilestone
            context is OnAddNewMilestone -> context
            else -> throw IllegalAccessException("woops, your $context or $parentFragment must impl OnAddNewMilestone")
        }
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(getString(R.string.create_milestone), R.menu.submit_menu) {
            if (it.itemId == R.id.submit) {
                val timestamp = dueOn.tag as? Long
                val title = milestoneTitle.editText?.asString()
                val description = milestoneDescription.editText?.asString()
                milestoneTitle.error = if (title.isNullOrEmpty()) getString(R.string.required_field) else null
                dueOn.error = if (timestamp == null) getString(R.string.required_field) else null
                if (timestamp != null && !title.isNullOrEmpty()) {
                    callback?.addNewMilestone(title, Date(timestamp), description)
                    dismissDialog()
                }
            }
        }
        dueOn.editText?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                DatePickerFragmentDialog.newInstance().show(childFragmentManager, "DatePickerFragmentDialog")
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
    }

    override fun onDateSet(date: Long) {
        dueOn.tag = date
        dueOn.editText?.setText(DatePrettifier.prettifyDate(date))
    }

    interface OnAddNewMilestone {
        fun addNewMilestone(title: String, dueOn: Date, description: String?)
    }
}