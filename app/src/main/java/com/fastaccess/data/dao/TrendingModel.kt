package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

data class TrendingModel(
        val title: String? = null,
        val description: String? = null,
        val language: String? = null,
        val stars: String? = null,
        val forks: String? = null,
        val todayStars: String? = null) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<TrendingModel> = object : Parcelable.Creator<TrendingModel> {
            override fun createFromParcel(source: Parcel): TrendingModel = TrendingModel(source)
            override fun newArray(size: Int): Array<TrendingModel?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(language)
        dest.writeString(stars)
        dest.writeString(forks)
        dest.writeString(todayStars)
    }
}