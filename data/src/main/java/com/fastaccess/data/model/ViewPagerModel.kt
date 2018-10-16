package com.fastaccess.data.model

import androidx.fragment.app.Fragment

/**
 * Created by Kosh on 05.10.18.
 */
data class ViewPagerModel(
        var text: CharSequence = "",
        val fragment: Fragment,
        val fragmentType: FragmentType,
        var icon: Int? = null
)

enum class FragmentType(val tabName: String? = null) {
    FEEDS(""), REPOS("repositories"), STARRED("stars"),
    GISTS("gists"), FOLLOWERS("followers"),
    FOLLOWINGS("following");

    companion object {
        fun getTypeSafely(tabName: String): FragmentType? = try {
            if (tabName.isEmpty()) FragmentType.FEEDS
            valueOf(tabName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}