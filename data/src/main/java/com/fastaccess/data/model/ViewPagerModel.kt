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
    FEEDS(""), REPOS("repositories"),
    STARRED("stars"), GISTS("gists"),
    FOLLOWERS("followers"), FOLLOWINGS("following"),
    UNREAD_NOTIFICATIONS("unread_notification"), ALL_NOTIFICATIONS("all_notification"),
    FILTER_ISSUES("filter_issues"), FILTER_PRS("filter_prs"),
    COMMITS(""), FILES(""), COMMENTS("");

    companion object {
        fun getTypeSafely(tabName: String): FragmentType? = try {
            values().firstOrNull { it.tabName == tabName }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

enum class ActivityType {
    FEEDS, NOTIFICATION, FILTER_ISSUE, FILTER_PR, SEARCH;

    companion object {
        fun getTypeSafely(activity: String?): ActivityType? = try {
            ActivityType.values().firstOrNull { it.name.equals(activity, true) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}