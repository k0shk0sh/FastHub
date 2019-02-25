package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LabelModel(
    @SerializedName("name") var name: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("isDefault") var isDefault: Boolean? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(color)
        parcel.writeString(url)
        parcel.writeValue(isDefault)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LabelModel> {
        override fun createFromParcel(parcel: Parcel): LabelModel {
            return LabelModel(parcel)
        }

        override fun newArray(size: Int): Array<LabelModel?> {
            return arrayOfNulls(size)
        }
    }
}