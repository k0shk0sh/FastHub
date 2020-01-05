package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 20.01.19.
 */
data class FilterSearchModel(
    var searchQuery: String = "",
    var searchBy: SearchBy = SearchBy.NONE,
    var filterByRepo: FilterByRepo = FilterByRepo(),
    var filterIssuesPrsModel: FilterIssuesPrsModel = FilterIssuesPrsModel(),
    var filterByUser: FilterByUser = FilterByUser()

) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: "",
        SearchBy.values()[source.readInt()],
        source.readParcelable<FilterByRepo>(FilterByRepo::class.java.classLoader) ?: FilterByRepo(),
        source.readParcelable<FilterIssuesPrsModel>(FilterIssuesPrsModel::class.java.classLoader) ?: FilterIssuesPrsModel(),
        source.readParcelable<FilterByUser>(FilterByUser::class.java.classLoader) ?: FilterByUser()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(searchQuery)
        writeInt(searchBy.ordinal)
        writeParcelable(filterByRepo, 0)
        writeParcelable(filterIssuesPrsModel, 0)
        writeParcelable(filterByUser, 0)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<FilterSearchModel> = object : Parcelable.Creator<FilterSearchModel> {
            override fun createFromParcel(source: Parcel): FilterSearchModel = FilterSearchModel(source)
            override fun newArray(size: Int): Array<FilterSearchModel?> = arrayOfNulls(size)
        }
    }

    enum class SearchBy { NONE, REPOS, ISSUES, PRS, USERS }

}

data class FilterByUser(var name: String? = "") : Parcelable {
    constructor(source: Parcel) : this(
        source.readString() ?: ""
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<FilterByUser> = object : Parcelable.Creator<FilterByUser> {
            override fun createFromParcel(source: Parcel): FilterByUser = FilterByUser(source)
            override fun newArray(size: Int): Array<FilterByUser?> = arrayOfNulls(size)
        }
    }
}

data class FilterByRepo(
    var filterByRepoIn: FilterByRepoIn = FilterByRepoIn.ALL,
    var filterByRepoLimitBy: FilterByRepoLimitBy? = null,
    var name: String? = null,
    var language: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        FilterByRepoIn.values()[source.readInt()],
        source.readValue(Int::class.java.classLoader)?.let { FilterByRepoLimitBy.values()[it as Int] },
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(filterByRepoIn.ordinal)
        writeValue(filterByRepoLimitBy?.ordinal)
        writeString(name)
        writeString(language)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<FilterByRepo> = object : Parcelable.Creator<FilterByRepo> {
            override fun createFromParcel(source: Parcel): FilterByRepo = FilterByRepo(source)
            override fun newArray(size: Int): Array<FilterByRepo?> = arrayOfNulls(size)
        }
    }

    enum class FilterByRepoIn { ALL, NAME, DESCRIPTION, README }
    enum class FilterByRepoLimitBy { USERNAME, ORG }
}