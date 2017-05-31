package com.fastaccess.data.dao.kot

import android.os.Parcel
import android.os.Parcelable

data class Owner(
        val gistsUrl: String? = null,
        val reposUrl: String? = null,
        val followingUrl: String? = null,
        val starredUrl: String? = null,
        val login: String? = null,
        val followersUrl: String? = null,
        val type: String? = null,
        val url: String? = null,
        val subscriptionsUrl: String? = null,
        val receivedEventsUrl: String? = null,
        val avatarUrl: String? = null,
        val eventsUrl: String? = null,
        val htmlUrl: String? = null,
        val siteAdmin: Boolean? = null,
        val id: Int? = null,
        val gravatarId: String? = null,
        val organizationsUrl: String? = null
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Owner> = object : Parcelable.Creator<Owner> {
            override fun createFromParcel(source: Parcel): Owner = Owner(source)
            override fun newArray(size: Int): Array<Owner?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(gistsUrl)
        dest.writeString(reposUrl)
        dest.writeString(followingUrl)
        dest.writeString(starredUrl)
        dest.writeString(login)
        dest.writeString(followersUrl)
        dest.writeString(type)
        dest.writeString(url)
        dest.writeString(subscriptionsUrl)
        dest.writeString(receivedEventsUrl)
        dest.writeString(avatarUrl)
        dest.writeString(eventsUrl)
        dest.writeString(htmlUrl)
        dest.writeValue(siteAdmin)
        dest.writeValue(id)
        dest.writeString(gravatarId)
        dest.writeString(organizationsUrl)
    }
}
