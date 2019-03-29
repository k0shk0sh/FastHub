package com.fastaccess.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 24.01.19.
 */
data class ShortUserModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("login") var login: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("bio") var bio: String? = null,
    @SerializedName(value = "avatar_url", alternate = ["avatarUrl"]) var avatarUrl: String? = null,
    @SerializedName("viewerCanFollow") var viewerCanFollow: Boolean? = null,
    @SerializedName("viewerIsFollowing") var viewerIsFollowing: Boolean? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(login)
        parcel.writeString(url)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(bio)
        parcel.writeString(avatarUrl)
        parcel.writeValue(viewerCanFollow)
        parcel.writeValue(viewerIsFollowing)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShortUserModel> {
        override fun createFromParcel(parcel: Parcel): ShortUserModel {
            return ShortUserModel(parcel)
        }

        override fun newArray(size: Int): Array<ShortUserModel?> {
            return arrayOfNulls(size)
        }
    }
}