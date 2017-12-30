package com.fastaccess.helper

import android.os.Parcelable

interface KParcelable : Parcelable {
    override fun describeContents() = 0
}
