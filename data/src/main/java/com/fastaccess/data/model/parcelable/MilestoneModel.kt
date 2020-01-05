package com.fastaccess.data.model.parcelable

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by Kosh on 25.02.19.
 */
data class MilestoneModel(
    @SerializedName("id") var id: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("number") var number: Int? = null,
    @SerializedName("closed") var closed: Boolean? = null,
    @SerializedName("dueOn") var dueOn: Date? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Boolean::class.java.classLoader) as Boolean?,
        source.readSerializable() as Date?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(title)
        writeString(description)
        writeString(state)
        writeString(url)
        writeValue(number)
        writeValue(closed)
        writeSerializable(dueOn)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<MilestoneModel> = object : Parcelable.Creator<MilestoneModel> {
            override fun createFromParcel(source: Parcel): MilestoneModel = MilestoneModel(source)
            override fun newArray(size: Int): Array<MilestoneModel?> = arrayOfNulls(size)
        }
    }
}