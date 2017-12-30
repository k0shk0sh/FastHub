package com.fastaccess.data.dao

import android.os.Parcel
import com.fastaccess.helper.KParcelable
import com.fastaccess.helper.parcelableCreator

data class TrendingModel(
        val title: String? = null,
        val description: String? = null,
        val language: String? = null,
        val stars: String? = null,
        val forks: String? = null,
        val todayStars: String? = null) : KParcelable {
    companion object {
        @JvmField val CREATOR = parcelableCreator(::TrendingModel)
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(title)
        writeString(description)
        writeString(language)
        writeString(stars)
        writeString(forks)
        writeString(todayStars)
    }
}
