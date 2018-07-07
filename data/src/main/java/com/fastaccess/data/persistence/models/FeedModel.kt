package com.fastaccess.data.persistence.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastaccess.domain.response.enums.EventsType
import com.google.gson.annotations.SerializedName
import java.util.*


/**
 * Created by Kosh on 23.06.18.
 */
@Entity(tableName = FeedModel.TABLE_NAME)
data class FeedModel(@SerializedName("id") @PrimaryKey var id: Long = 0L,
                     @SerializedName("type") var type: EventsType? = null,
                     @SerializedName("created_at") var createdAt: Date? = null,
                     @SerializedName("public") var isPublic: Boolean? = null,
                     @SerializedName("actor") @Embedded(prefix = "actor_") var actor: UserModel? = null,
                     @SerializedName("repo") var repo: RepositoryModel? = null,
                     @SerializedName("payload") var payload: PayloadModel? = null,
                     @SerializedName("login") var login: String? = null,
                     @SerializedName("saved_date") var savedDate: Date = Date()) {

    companion object {
        const val TABLE_NAME = "feed_table"
    }
}

data class PayloadModel(@SerializedName("action") var action: String? = null,
                        @SerializedName("forkee") var forkee: RepositoryModel? = null,
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
                        @SerializedName("issue") var issue: IssueModel? = null,
                        @SerializedName("pull_request") var pullRequest: IssueModel? = null,
                        @SerializedName("`release`") var release: ReleaseModel? = null,
                        @SerializedName("`download`") var download: ReleaseModel? = null,
                        @SerializedName("commits") var commits: List<CommitModel>? = null,
                        @SerializedName("commit_comment") var commitComment: CommentModel? = null,
                        @SerializedName("comment") var comment: CommentModel? = null,
                        @SerializedName("gist") var gist: GistModel? = null,
                        @SerializedName("pages") var pages: List<WikiModel>? = null,
                        @SerializedName("team") var team: TeamModel? = null)

data class IssueModel(@SerializedName("id") var id: Long,
                      @SerializedName("number") var number: Long? = null,
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
                      @SerializedName("repository") var repo: RepositoryModel? = null,
                      @SerializedName("merged") var isMerged: Boolean? = null,
                      @SerializedName("labels") var labels: List<LabelModel>? = null)

data class WikiModel(@SerializedName("title") var title: String? = null,
                     @SerializedName("summery") var summery: String? = null,
                     @SerializedName("action") var action: String? = null,
                     @SerializedName("sha") var sha: String? = null,
                     @SerializedName("html_url") var htmlUrl: String? = null,
                     @SerializedName("package_name") var packageName: String? = null)

data class TeamModel(@SerializedName("id") var id: Long? = null,
                     @SerializedName("url") var url: String? = null,
                     @SerializedName("name") var name: String? = null,
                     @SerializedName("description") var description: String? = null,
                     @SerializedName("slug") var slug: String? = null,
                     @SerializedName("location") var location: String? = null,
                     @SerializedName("privacy") var privacy: String? = null,
                     @SerializedName("permission") var permission: String? = null,
                     @SerializedName("members_url") var membersUrl: String? = null,
                     @SerializedName("repositories_url") var repositoriesUrl: Int? = null)

data class ReleaseModel(@SerializedName("id") var id: Long? = null,
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

data class CommitModel(@SerializedName("sha") var sha: String? = null,
                       @SerializedName("url") var url: String? = null,
                       @SerializedName("message") var message: String? = null,
                       @SerializedName("author") var author: UserModel? = null,
                       @SerializedName("committer") var committer: UserModel? = null,
                       @SerializedName("tree") var tree: UserModel? = null,
                       @SerializedName("distinct") var distincted: Boolean = false,
                       @SerializedName("parents") var parents: List<CommitModel>? = null,
                       @SerializedName("comment_count") var commentCount: Int? = null)

data class RepositoryModel(@SerializedName("id") var id: Int? = null,
                           @SerializedName("node_id") var nodeId: String? = null,
                           @SerializedName("owner") var owner: UserModel? = null,
                           @SerializedName("name") var name: String? = null,
                           @SerializedName("full_name") var fullName: String? = null,
                           @SerializedName("description") var description: String? = null,
                           @SerializedName("private") var private: Boolean? = null,
                           @SerializedName("fork") var fork: Boolean? = null,
                           @SerializedName("url") var url: String? = null,
                           @SerializedName("html_url") var htmlUrl: String? = null)

data class CommentModel(@SerializedName("id") var id: Int? = null,
                        @SerializedName("body") var body: String? = null,
                        @SerializedName("commit_id") var commitId: String? = null,
                        @SerializedName("path") var path: String? = null,
                        @SerializedName("line") var line: Long? = null,
                        @SerializedName("position") var position: Long? = null,
                        @SerializedName("created_at") var createdAt: Date? = null,
                        @SerializedName("updated_at") var UpdatedAt: Date? = null)

data class LabelModel(@SerializedName("url") var url: String? = null,
                      @SerializedName("color") var color: String? = null,
                      @SerializedName("name") var name: String? = null)

data class GistModel(@SerializedName("id") var id: String? = null,
                     @SerializedName("node_id") var nodeId: String? = null,
                     @SerializedName("url") var url: String? = null,
                     @SerializedName("html_url") var htmlUrl: String? = null,
                     @SerializedName("public_x") var publicX: String? = null,
                     @SerializedName("description") var description: String? = null,
                     @SerializedName("truncated") var truncated: Boolean? = null)