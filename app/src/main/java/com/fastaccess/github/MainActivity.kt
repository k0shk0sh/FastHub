package com.fastaccess.github

import android.os.Bundle
import com.fastaccess.github.base.BaseActivity
import com.fastaccess.github.platform.glide.GlideRequests
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject lateinit var glide: GlideRequests

    override fun layoutRes(): Int = R.layout.activity_main

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Timber.e("here")
    }

    override fun onActivityCreatedWithUser(savedInstanceState: Bundle?) {
        Timber.e("here")
    }
}
