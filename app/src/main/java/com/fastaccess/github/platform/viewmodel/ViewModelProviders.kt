package com.fastaccess.github.platform.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

object ViewModelProviders {
    fun of(owner: ViewModelStoreOwner, factory: ViewModelProvider.Factory): ViewModelProvider = ViewModelProvider(owner, factory)
}