package com.fastaccess.data.dao

import android.os.Parcel
import com.fastaccess.helper.KotlinParcelable
import com.fastaccess.helper.parcelableCreator
import com.fastaccess.helper.readBoolean
import com.fastaccess.helper.writeBoolean

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
                             val isEdit: Boolean) : KotlinParcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readBoolean())

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(login)
        writeString(repoId)
        writeString(path)
        writeString(ref)
        writeString(sha)
        writeString(contentUrl)
        writeString(fileName)
        writeBoolean(isEdit)
    }

    companion object {
        @JvmField val CREATOR = parcelableCreator(::EditRepoFileModel)
    }
}
