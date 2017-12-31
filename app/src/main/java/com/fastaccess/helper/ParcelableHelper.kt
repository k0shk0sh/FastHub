package com.fastaccess.helper

import android.os.Parcel
import android.os.Parcelable

interface KotlinParcelable : Parcelable {
    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel, flags: Int)
}

inline fun <reified T> parcelableCreator(crossinline create: (Parcel) -> T) = object : Parcelable.Creator<T> {
    override fun createFromParcel(source: Parcel) = create(source)
    override fun newArray(size: Int) = arrayOfNulls<T>(size)
}

fun Parcel.readBoolean() = readInt() != 0

fun Parcel.writeBoolean(value: Boolean) = writeInt(if (value) 1 else 0)
