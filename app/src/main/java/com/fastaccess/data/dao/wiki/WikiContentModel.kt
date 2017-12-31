package com.fastaccess.data.dao.wiki

import android.os.Parcel
import com.fastaccess.helper.KotlinParcelable
import com.fastaccess.helper.parcelableCreator

/**
 * Created by Kosh on 13 Jun 2017, 8:06 PM
 */
data class WikiContentModel(val content: String? = null, private val footer: String? = null,
                            val sidebar: ArrayList<WikiSideBarModel>) : KotlinParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(::WikiContentModel)
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.createTypedArrayList(WikiSideBarModel.CREATOR)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(content)
        writeString(footer)
        writeTypedList(sidebar)
    }
}
