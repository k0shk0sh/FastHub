package com.fastaccess.data.dao.wiki

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 13 Jun 2017, 8:03 PM
 */
data class WikiSideBarModel(val title: String? = null, val link: String? = null) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<WikiSideBarModel> = object : Parcelable.Creator<WikiSideBarModel> {
            override fun createFromParcel(source: Parcel): WikiSideBarModel = WikiSideBarModel(source)
            override fun newArray(size: Int): Array<WikiSideBarModel?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(link)
    }
}