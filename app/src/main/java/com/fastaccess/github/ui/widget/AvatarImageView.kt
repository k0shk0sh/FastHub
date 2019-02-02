package com.fastaccess.github.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.fastaccess.github.R
import com.fastaccess.github.extensions.getDrawableCompat
import com.fastaccess.github.platform.glide.GlideApp
import com.fastaccess.github.utils.extensions.route

/**
 * Created by Kosh on 27.12.18.
 */
class AvatarImageView constructor(context: Context,
                                  attrs: AttributeSet? = null,
                                  defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    fun loadAvatar(url: String? = null, userUrl: String? = null) {
        setBackgroundResource(R.drawable.circle_shape)
        if (url.isNullOrEmpty()) {
            setImageResource(R.drawable.ic_profile)
            scaleType = ScaleType.CENTER_CROP
        } else {
            val drawable = context.getDrawableCompat(R.drawable.ic_profile)
            GlideApp.with(this)
                .load(url)
                .circleCrop()
                .fallback(drawable)
                .error(drawable)
                .dontAnimate()
                .into(this)
        }
        userUrl?.let { setOnClickListener { it.context.route(userUrl) } }
    }
}