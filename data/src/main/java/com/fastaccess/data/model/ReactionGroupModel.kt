package com.fastaccess.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReactionGroupModel(
    @SerializedName("content") var content: ReactionContent? = null,
    @SerializedName("createdAt") var createdAt: Date? = null,
    @SerializedName("users") var users: CountModel? = null,
    @SerializedName("viewerHasReacted") var viewerHasReacted: Boolean? = false
)


fun ReactionContent?.getEmoji(): String = when (this ?: ReactionContent.`$UNKNOWN`) {
    ReactionContent.THUMBS_UP -> String(Character.toChars(0x1f44d))
    ReactionContent.THUMBS_DOWN -> String(Character.toChars(0x1f44e))
    ReactionContent.LAUGH -> String(Character.toChars(0x1F601))
    ReactionContent.HOORAY -> String(Character.toChars(0x1f389))
    ReactionContent.CONFUSED -> String(Character.toChars(0x1F615))
    ReactionContent.HEART -> String(Character.toChars(0x2764))
    ReactionContent.ROCKET -> String(Character.toChars(0x1f680))
    ReactionContent.EYES -> String(Character.toChars(0x1f440))
    ReactionContent.`$UNKNOWN` -> ""
}

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