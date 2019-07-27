package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 2019-07-27.
 */
data class EditIssuePrBundleModel(
    val login: String,
    val repo: String,
    val number: Int,
    var title: String? = null,
    var description: String? = null,
    val isCreate: Boolean = true,
    val isPr: Boolean = false,
    val isOwner: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(
        parcel: Parcel,
        flags: Int
    ) {
        parcel.writeString(login)
        parcel.writeString(repo)
        parcel.writeInt(number)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeByte(if (isCreate) 1 else 0)
        parcel.writeByte(if (isPr) 1 else 0)
        parcel.writeByte(if (isOwner) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EditIssuePrBundleModel> {
        override fun createFromParcel(parcel: Parcel): EditIssuePrBundleModel {
            return EditIssuePrBundleModel(parcel)
        }

        override fun newArray(size: Int): Array<EditIssuePrBundleModel?> {
            return arrayOfNulls(size)
        }
    }
}