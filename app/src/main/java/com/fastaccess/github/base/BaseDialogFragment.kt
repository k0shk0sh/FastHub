package com.fastaccess.github.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getDrawable
import dagger.android.support.DaggerDialogFragment

/**
 * Created by Kosh on 07.03.19.
 */
abstract class BaseDialogFragment : DaggerDialogFragment() {

    @LayoutRes abstract fun layoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeOverlay_AppCompat_Dialog_Alert)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val contextThemeWrapper = ContextThemeWrapper(requireContext(), requireContext().theme)
        val themeAwareInflater = inflater.cloneInContext(contextThemeWrapper)
        return themeAwareInflater.inflate(layoutRes(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    fun dismissDialog() = dialog?.dismiss()

    fun setupToolbar(title: String, menuId: Int? = null, onMenuItemClick: ((item: MenuItem) -> Unit)? = null) {
        view?.findViewById<Toolbar?>(R.id.toolbar)?.apply {
            navigationIcon = getDrawable(R.drawable.ic_clear)
            val titleText = findViewById<TextView?>(R.id.toolbarTitle)
            if (titleText != null) {
                titleText.text = title
            } else {
                setTitle(title)
            }
            setNavigationOnClickListener { dismissDialog() }
            menuId?.let { menuResId ->
                inflateMenu(menuResId)
                onMenuItemClick?.let { onClick ->
                    setOnMenuItemClickListener {
                        onClick.invoke(it)
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }
}