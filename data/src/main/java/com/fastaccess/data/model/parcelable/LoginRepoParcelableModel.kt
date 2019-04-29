package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 25.02.19.
 */
data class LoginRepoParcelableModel<T : Parcelable>(
    val login: String,
    val repo: String,
    val items: List<T>? = null,
    val number: Int? = null
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString() ?: "",
        source.readString() ?: "",
        arrayListOf<T>().apply {
            source.readList(this, this.javaClass.classLoader)
        },
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(login)
        writeString(repo)
        writeTypedList(items)
        writeInt(number ?: -1)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<LoginRepoParcelableModel<Parcelable>> = object :
            Parcelable.Creator<LoginRepoParcelableModel<Parcelable>> {
            override fun createFromParcel(source: Parcel): LoginRepoParcelableModel<Parcelable> = LoginRepoParcelableModel(source)
            override fun newArray(size: Int): Array<LoginRepoParcelableModel<Parcelable>?> = arrayOfNulls(size)
        }
    }
}