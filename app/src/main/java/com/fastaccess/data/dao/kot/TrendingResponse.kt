package com.fastaccess.data.dao.kot

import android.os.Parcel
import android.os.Parcelable

data class TrendingResponse(
        val stargazersCount: Int? = null,
        val pushedAt: String? = null,
        val subscriptionUrl: String? = null,
        val language: String? = null,
        val branchesUrl: String? = null,
        val issueCommentUrl: String? = null,
        val labelsUrl: String? = null,
        val subscribersUrl: String? = null,
        val releasesUrl: String? = null,
        val svnUrl: String? = null,
        val subscribersCount: Int? = null,
        val id: Int? = null,
        val forks: Int? = null,
        val archiveUrl: String? = null,
        val gitRefsUrl: String? = null,
        val forksUrl: String? = null,
        val statusesUrl: String? = null,
        val networkCount: Int? = null,
        val sshUrl: String? = null,
        val fullName: String? = null,
        val size: Int? = null,
        val languagesUrl: String? = null,
        val htmlUrl: String? = null,
        val collaboratorsUrl: String? = null,
        val cloneUrl: String? = null,
        val name: String? = null,
        val pullsUrl: String? = null,
        val defaultBranch: String? = null,
        val hooksUrl: String? = null,
        val treesUrl: String? = null,
        val tagsUrl: String? = null,
        val jsonMemberPrivate: Boolean? = null,
        val contributorsUrl: String? = null,
        val hasDownloads: Boolean? = null,
        val notificationsUrl: String? = null,
        val openIssuesCount: Int? = null,
        val description: String? = null,
        val createdAt: String? = null,
        val watchers: Int? = null,
        val keysUrl: String? = null,
        val deploymentsUrl: String? = null,
        val hasProjects: Boolean? = null,
        val hasWiki: Boolean? = null,
        val updatedAt: String? = null,
        val commentsUrl: String? = null,
        val stargazersUrl: String? = null,
        val gitUrl: String? = null,
        val hasPages: Boolean? = null,
        val owner: Owner? = null,
        val organization: Owner? = null,
        val commitsUrl: String? = null,
        val compareUrl: String? = null,
        val gitCommitsUrl: String? = null,
        val blobsUrl: String? = null,
        val gitTagsUrl: String? = null,
        val mergesUrl: String? = null,
        val downloadsUrl: String? = null,
        val hasIssues: Boolean? = null,
        val url: String? = null,
        val contentsUrl: String? = null,
        val mirrorUrl: String? = null,
        val milestonesUrl: String? = null,
        val teamsUrl: String? = null,
        val fork: Boolean? = null,
        val issuesUrl: String? = null,
        val eventsUrl: String? = null,
        val issueEventsUrl: String? = null,
        val assigneesUrl: String? = null,
        val openIssues: Int? = null,
        val watchersCount: Int? = null,
        val homepage: String? = null,
        val forksCount: Int? = null
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<TrendingResponse> = object : Parcelable.Creator<TrendingResponse> {
            override fun createFromParcel(source: Parcel): TrendingResponse = TrendingResponse(source)
            override fun newArray(size: Int): Array<TrendingResponse?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
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
            source.readString(),
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readString(),
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readParcelable<Owner>(Owner::class.java.classLoader) as Owner?,
            source.readParcelable<Owner>(Owner::class.java.classLoader) as Owner?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Boolean::class.java.classLoader) as Boolean?,
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readString(),
            source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(stargazersCount)
        dest.writeString(pushedAt)
        dest.writeString(subscriptionUrl)
        dest.writeString(language)
        dest.writeString(branchesUrl)
        dest.writeString(issueCommentUrl)
        dest.writeString(labelsUrl)
        dest.writeString(subscribersUrl)
        dest.writeString(releasesUrl)
        dest.writeString(svnUrl)
        dest.writeValue(subscribersCount)
        dest.writeValue(id)
        dest.writeValue(forks)
        dest.writeString(archiveUrl)
        dest.writeString(gitRefsUrl)
        dest.writeString(forksUrl)
        dest.writeString(statusesUrl)
        dest.writeValue(networkCount)
        dest.writeString(sshUrl)
        dest.writeString(fullName)
        dest.writeValue(size)
        dest.writeString(languagesUrl)
        dest.writeString(htmlUrl)
        dest.writeString(collaboratorsUrl)
        dest.writeString(cloneUrl)
        dest.writeString(name)
        dest.writeString(pullsUrl)
        dest.writeString(defaultBranch)
        dest.writeString(hooksUrl)
        dest.writeString(treesUrl)
        dest.writeString(tagsUrl)
        dest.writeValue(jsonMemberPrivate)
        dest.writeString(contributorsUrl)
        dest.writeValue(hasDownloads)
        dest.writeString(notificationsUrl)
        dest.writeValue(openIssuesCount)
        dest.writeString(description)
        dest.writeString(createdAt)
        dest.writeValue(watchers)
        dest.writeString(keysUrl)
        dest.writeString(deploymentsUrl)
        dest.writeValue(hasProjects)
        dest.writeValue(hasWiki)
        dest.writeString(updatedAt)
        dest.writeString(commentsUrl)
        dest.writeString(stargazersUrl)
        dest.writeString(gitUrl)
        dest.writeValue(hasPages)
        dest.writeParcelable(owner, flags)
        dest.writeParcelable(organization, flags)
        dest.writeString(commitsUrl)
        dest.writeString(compareUrl)
        dest.writeString(gitCommitsUrl)
        dest.writeString(blobsUrl)
        dest.writeString(gitTagsUrl)
        dest.writeString(mergesUrl)
        dest.writeString(downloadsUrl)
        dest.writeValue(hasIssues)
        dest.writeString(url)
        dest.writeString(contentsUrl)
        dest.writeString(mirrorUrl)
        dest.writeString(milestonesUrl)
        dest.writeString(teamsUrl)
        dest.writeValue(fork)
        dest.writeString(issuesUrl)
        dest.writeString(eventsUrl)
        dest.writeString(issueEventsUrl)
        dest.writeString(assigneesUrl)
        dest.writeValue(openIssues)
        dest.writeValue(watchersCount)
        dest.writeString(homepage)
        dest.writeValue(forksCount)
    }
}
