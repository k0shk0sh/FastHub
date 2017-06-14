package com.fastaccess.data.dao.wiki

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 13 Jun 2017, 8:06 PM
 */
data class WikiContentModel(val content: String? = null, val footer: String? = null,
                            val sidebar: ArrayList<WikiSideBarModel>) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<WikiContentModel> = object : Parcelable.Creator<WikiContentModel> {
            override fun createFromParcel(source: Parcel): WikiContentModel = WikiContentModel(source)
            override fun newArray(size: Int): Array<WikiContentModel?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.createTypedArrayList(WikiSideBarModel.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(content)
        dest.writeString(footer)
        dest.writeTypedList(sidebar)
    }
}