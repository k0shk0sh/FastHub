package com.fastaccess.data.dao

import android.os.Parcel
import com.fastaccess.helper.KParcelable
import com.fastaccess.helper.parcelableCreator

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
                             val isEdit: Boolean) : KParcelable {
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

    companion object {
        @JvmField val CREATOR = parcelableCreator(::EditRepoFileModel)
    }
}
