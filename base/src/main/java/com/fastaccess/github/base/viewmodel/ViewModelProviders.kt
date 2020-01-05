package com.fastaccess.github.base.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

object ViewModelProviders {
    fun of(owner: ViewModelStoreOwner, factory: ViewModelProvider.Factory): ViewModelProvider = ViewModelProvider(owner, factory)
}