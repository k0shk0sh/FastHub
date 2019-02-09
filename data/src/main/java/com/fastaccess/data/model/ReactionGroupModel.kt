package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReactionGroupModel(
    @SerializedName("content") var content: ReactionContent? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("users") var users: CountModel? = null,
    @SerializedName("viewerHasReacted") var viewerHasReacted: Boolean? = false
)

enum class ReactionContent(val value: String) {
    /**
     * Represents the 👍 emoji.
     */
    THUMBS_UP("THUMBS_UP"),

    /**
     * Represents the 👎 emoji.
     */
    THUMBS_DOWN("THUMBS_DOWN"),

    /**
     * Represents the 😄 emoji.
     */
    LAUGH("LAUGH"),

    /**
     * Represents the 🎉 emoji.
     */
    HOORAY("HOORAY"),

    /**
     * Represents the 😕 emoji.
     */
    CONFUSED("CONFUSED"),

    /**
     * Represents the ❤️ emoji.
     */
    HEART("HEART"),

    /**
     * Represents the 🚀 emoji.
     */
    ROCKET("ROCKET"),

    /**
     * Represents the 👀 emoji.
     */
    EYES("EYES"),

    /**
     * Auto generated constant for unknown enum values
     */
    `$UNKNOWN`("UNKNOWN");

    companion object {
        fun getByValue(value: String? = null): ReactionContent? = values().firstOrNull { it.value == value }
    }
}