package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 24.01.19.
 */
data class FilterTrendingModel(
    var lang: String = "",
    var since: TrendingSince = TrendingSince.DAILY
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString() ?: "",
        TrendingSince.values()[source.readInt()]
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(lang)
        writeInt(since.ordinal)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<FilterTrendingModel> = object : Parcelable.Creator<FilterTrendingModel> {
            override fun createFromParcel(source: Parcel): FilterTrendingModel = FilterTrendingModel(source)
            override fun newArray(size: Int): Array<FilterTrendingModel?> = arrayOfNulls(size)
        }
    }

    enum class TrendingSince {
        DAILY, WEEKLY, MONTHLY;

        companion object {
            fun getSince(since: String?): TrendingSince {
                return values().firstOrNull { it.name.equals(since, true) } ?: TrendingSince.DAILY
            }
        }
    }
}