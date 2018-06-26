package com.fastaccess.data.persistence.models

import com.fastaccess.domain.response.enums.EventsType
import com.google.gson.annotations.SerializedName
import java.util.*


/**
 * Created by Kosh on 23.06.18.
 */
data class FeedModel(@SerializedName("id") var id: Long = 0L,
                     @SerializedName("type") var type: EventsType? = null,
                     @SerializedName("created_at") var createdAt: Date? = null,
                     @SerializedName("public") var isPublic: Boolean? = null,
                     @SerializedName("actor") var actor: UserModel? = null,
                     @SerializedName("repo") var repo: RepositoryResponse? = null,
                     @SerializedName("payload") var payload: PayloadResponse? = null,
                     @SerializedName("login") var login: String? = null)

data class PayloadResponse(@SerializedName("action") var action: String? = null,
                           @SerializedName("forkee") var forkee: RepositoryResponse? = null,
                           @SerializedName("ref_type") var refType: String? = null,
                           @SerializedName("target") var target: UserModel? = null,
                           @SerializedName("member") var member: UserModel? = null,
                           @SerializedName("description") var description: String? = null,
                           @SerializedName("before") var before: String? = null,
                           @SerializedName("head") var head: String? = null,
                           @SerializedName("ref") var ref: String? = null,
                           @SerializedName("size") var size: Int? = 0,
                           @SerializedName("user") var user: UserModel? = null,
                           @SerializedName("blocked_user") var blockedUser: UserModel? = null,
                           @SerializedName("organization") var organization: UserModel? = null,
                           @SerializedName("invitation") var invitation: UserModel? = null,
                           @SerializedName("issue") var issue: IssueResponse? = null,
                           @SerializedName("pull_request") var pullRequest: IssueResponse? = null,
                           @SerializedName("release") var release: ReleaseResponse? = null,
                           @SerializedName("commits") var commits: List<CommitResponse>? = null)

data class IssueResponse(@SerializedName("id") var id: Long,
                         @SerializedName("title") var title: String? = null,
                         @SerializedName("body") var body: String? = null,
                         @SerializedName("url") var url: String? = null,
                         @SerializedName("html_url") var htmlUrl: String? = null,
                         @SerializedName("repo_url") var repoUrl: String? = null,
                         @SerializedName("locked") var locked: Boolean = false,
                         @SerializedName("closed_at") var closedAt: Date? = null,
                         @SerializedName("created_at") var createdAt: Date? = null,
                         @SerializedName("updated_at") var UpdatedAt: Date? = null,
                         @SerializedName("issue_state") var issueState: String? = null,
                         @SerializedName("user") var user: UserModel? = null,
                         @SerializedName("assignee") var assignee: UserModel? = null,
                         @SerializedName("closedBy") var closedBy: UserModel? = null,
                         @SerializedName("repository") var repo: RepositoryResponse? = null,
                         @SerializedName("wiki") var pages: List<WikiResponse>? = null,
                         @SerializedName("team") var team: TeamResponse? = null)

data class WikiResponse(@SerializedName("title") var title: String? = null,
                        @SerializedName("summery") var summery: String? = null,
                        @SerializedName("action") var action: String? = null,
                        @SerializedName("sha") var sha: String? = null,
                        @SerializedName("html_url") var htmlUrl: String? = null,
                        @SerializedName("package_name") var packageName: String? = null)

data class TeamResponse(@SerializedName("id") var id: Long? = null,
                        @SerializedName("url") var url: String? = null,
                        @SerializedName("name") var name: String? = null,
                        @SerializedName("description") var description: String? = null,
                        @SerializedName("slug") var slug: String? = null,
                        @SerializedName("location") var location: String? = null,
                        @SerializedName("privacy") var privacy: String? = null,
                        @SerializedName("permission") var permission: String? = null,
                        @SerializedName("members_url") var membersUrl: String? = null,
                        @SerializedName("repositories_url") var repositoriesUrl: Int? = null)

data class ReleaseResponse(@SerializedName("id") var id: Long? = null,
                           @SerializedName("url") var url: String? = null,
                           @SerializedName("name") var name: String? = null,
                           @SerializedName("tag_name") var tagName: String? = null,
                           @SerializedName("target_commitish") var targetCommitish: String? = null,
                           @SerializedName("tarball_url") var tarballUrl: String? = null,
                           @SerializedName("body_html") var body: String? = null,
                           @SerializedName("zipball_url") var zipBallUrl: String? = null,
                           @SerializedName("draft") var draft: Boolean? = null,
                           @SerializedName("pre_release") var preRelease: Boolean? = null,
                           @SerializedName("created_at") var createdAt: Date? = null,
                           @SerializedName("published_at") var publishedAt: Date? = null,
                           @SerializedName("author") var author: UserModel? = null)

data class CommitResponse(@SerializedName("sha") var sha: String? = null,
                          @SerializedName("url") var url: String? = null,
                          @SerializedName("message") var message: String? = null,
                          @SerializedName("author") var author: UserModel? = null,
                          @SerializedName("committer") var committer: UserModel? = null,
                          @SerializedName("tree") var tree: UserModel? = null,
                          @SerializedName("distinct") var distincted: Boolean = false,
                          @SerializedName("parents") var parents: List<CommitResponse>? = null,
                          @SerializedName("comment_count") var commentCount: Int? = null)

data class RepositoryResponse(@SerializedName("id") var id: Int? = null,
                              @SerializedName("node_id") var nodeId: String? = null,
                              @SerializedName("owner") var owner: UserModel? = null,
                              @SerializedName("name") var name: String? = null,
                              @SerializedName("full_name") var fullName: String? = null,
                              @SerializedName("description") var description: String? = null,
                              @SerializedName("private") var private: Boolean? = null,
                              @SerializedName("fork") var fork: Boolean? = null,
                              @SerializedName("url") var url: String? = null,
                              @SerializedName("html_url") var htmlUrl: String? = null)