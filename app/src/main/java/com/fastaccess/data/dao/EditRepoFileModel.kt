package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Hashemsergani on 01/09/2017.
 */
data class EditRepoFileModel(val login: String,
                             val repoId: String,
                             val path: String?,
                             val ref: String,
                             val sha: String?,
                             val contentUrl: String?,
                             val fileName: String?,
                             val isEdit: Boolean) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeString(repoId)
        parcel.writeString(path)
        parcel.writeString(ref)
        parcel.writeString(sha)
        parcel.writeString(contentUrl)
        parcel.writeString(fileName)
        parcel.writeByte(if (isEdit) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EditRepoFileModel> {
        override fun createFromParcel(parcel: Parcel): EditRepoFileModel {
            return EditRepoFileModel(parcel)
        }

        override fun newArray(size: Int): Array<EditRepoFileModel?> {
            return arrayOfNulls(size)
        }
    }
}