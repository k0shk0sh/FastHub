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
    val number: Int? = null,
    val isPr: Boolean? = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        listOf<T>().apply {
            parcel.readList(this, this.javaClass.classLoader)
        },
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeString(repo)
        parcel.writeTypedList(items)
        parcel.writeValue(number)
        parcel.writeValue(isPr)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<LoginRepoParcelableModel<Parcelable>> = object :
            Parcelable.Creator<LoginRepoParcelableModel<Parcelable>> {
            override fun createFromParcel(source: Parcel): LoginRepoParcelableModel<Parcelable> = LoginRepoParcelableModel(source)
            override fun newArray(size: Int): Array<LoginRepoParcelableModel<Parcelable>?> = arrayOfNulls(size)
        }
    }
}