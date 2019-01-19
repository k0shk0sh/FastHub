package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 19.01.19.
 */
data class FilterIssuesPrsModel(
    var searchBy: SearchBy = SearchBy.CREATED,
    var searchType: SearchType = SearchType.OPEN,
    var searchVisibility: SearchVisibility = SearchVisibility.PUBLIC,
    var searchSortBy: SearchSortBy = SearchSortBy.NEWEST
) : Parcelable {
    enum class SearchBy { CREATED, ASSIGNED, MENTIONED, REVIEW_REQUESTS }

    enum class SearchType { OPEN, CLOSED }

    enum class SearchVisibility { PUBLIC, PRIVATE }

    enum class SearchSortBy { NEWEST, OLDEST, MOST_COMMENTED, LEAST_COMMENTED, RECENTLY_UPDATED, LEAST_RECENTLY_UPDATED }

    constructor(source: Parcel) : this(
        SearchBy.values()[source.readInt()],
        SearchType.values()[source.readInt()],
        SearchVisibility.values()[source.readInt()],
        SearchSortBy.values()[source.readInt()]
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(searchBy.ordinal)
        writeInt(searchType.ordinal)
        writeInt(searchVisibility.ordinal)
        writeInt(searchSortBy.ordinal)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<FilterIssuesPrsModel> = object : Parcelable.Creator<FilterIssuesPrsModel> {
            override fun createFromParcel(source: Parcel): FilterIssuesPrsModel = FilterIssuesPrsModel(source)
            override fun newArray(size: Int): Array<FilterIssuesPrsModel?> = arrayOfNulls(size)
        }
    }
}