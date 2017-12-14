package com.fastaccess.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.data.dao.types.MyIssuesType;
import com.fastaccess.ui.modules.feeds.FeedsFragment;
import com.fastaccess.ui.modules.gists.GistsFragment;
import com.fastaccess.ui.modules.gists.gist.comments.GistCommentsFragment;
import com.fastaccess.ui.modules.gists.gist.files.GistFilesListFragment;
import com.fastaccess.ui.modules.gists.starred.StarredGistsFragment;
import com.fastaccess.ui.modules.main.issues.MyIssuesFragment;
import com.fastaccess.ui.modules.main.pullrequests.MyPullRequestFragment;
import com.fastaccess.ui.modules.notification.all.AllNotificationsFragment;
import com.fastaccess.ui.modules.notification.fasthub.FastHubNotificationsFragment;
import com.fastaccess.ui.modules.notification.unread.UnreadNotificationsFragment;
import com.fastaccess.ui.modules.pinned.gist.PinnedGistFragment;
import com.fastaccess.ui.modules.pinned.issue.PinnedIssueFragment;
import com.fastaccess.ui.modules.pinned.pullrequest.PinnedPullRequestFragment;
import com.fastaccess.ui.modules.pinned.repo.PinnedReposFragment;
import com.fastaccess.ui.modules.profile.followers.ProfileFollowersFragment;
import com.fastaccess.ui.modules.profile.following.ProfileFollowingFragment;
import com.fastaccess.ui.modules.profile.gists.ProfileGistsFragment;
import com.fastaccess.ui.modules.profile.org.OrgProfileOverviewFragment;
import com.fastaccess.ui.modules.profile.org.members.OrgMembersFragment;
import com.fastaccess.ui.modules.profile.org.repos.OrgReposFragment;
import com.fastaccess.ui.modules.profile.org.teams.OrgTeamFragment;
import com.fastaccess.ui.modules.profile.org.teams.details.members.TeamMembersFragment;
import com.fastaccess.ui.modules.profile.org.teams.details.repos.TeamReposFragment;
import com.fastaccess.ui.modules.profile.overview.ProfileOverviewFragment;
import com.fastaccess.ui.modules.profile.repos.ProfileReposFragment;
import com.fastaccess.ui.modules.profile.starred.ProfileStarredFragment;
import com.fastaccess.ui.modules.repos.code.commit.RepoCommitsFragment;
import com.fastaccess.ui.modules.repos.code.commit.details.comments.CommitCommentsFragment;
import com.fastaccess.ui.modules.repos.code.commit.details.files.CommitFilesFragment;
import com.fastaccess.ui.modules.repos.code.contributors.RepoContributorsFragment;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathFragment;
import com.fastaccess.ui.modules.repos.code.prettifier.ViewerFragment;
import com.fastaccess.ui.modules.repos.code.releases.RepoReleasesFragment;
import com.fastaccess.ui.modules.repos.extras.branches.BranchesFragment;
import com.fastaccess.ui.modules.repos.issues.issue.RepoClosedIssuesFragment;
import com.fastaccess.ui.modules.repos.issues.issue.RepoOpenedIssuesFragment;
import com.fastaccess.ui.modules.repos.issues.issue.details.timeline.IssueTimelineFragment;
import com.fastaccess.ui.modules.repos.projects.columns.ProjectColumnFragment;
import com.fastaccess.ui.modules.repos.projects.list.RepoProjectFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.RepoPullRequestFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits.PullRequestCommitsFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.PullRequestFilesFragment;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineFragment;
import com.fastaccess.ui.modules.search.code.SearchCodeFragment;
import com.fastaccess.ui.modules.search.issues.SearchIssuesFragment;
import com.fastaccess.ui.modules.search.repos.SearchReposFragment;
import com.fastaccess.ui.modules.search.users.SearchUsersFragment;
import com.fastaccess.ui.modules.theme.fragment.ThemeFragment;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 03 Dec 2016, 9:26 AM
 */

@Getter @Setter public class FragmentPagerAdapterModel {

    String title;
    Fragment fragment;
    String key;

    private FragmentPagerAdapterModel(String title, Fragment fragment) {
        this(title, fragment, null);
    }

    public FragmentPagerAdapterModel(String title, Fragment fragment, String key) {
        this.title = title;
        this.fragment = fragment;
        this.key = key;
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForProfile(@NonNull Context context, @NonNull String login) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.overview), ProfileOverviewFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.feed), FeedsFragment.newInstance(login, false)),
                new FragmentPagerAdapterModel(context.getString(R.string.repos), ProfileReposFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.starred), ProfileStarredFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.gists), ProfileGistsFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.followers), ProfileFollowersFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.following), ProfileFollowingFragment.newInstance(login)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoCode(@NonNull Context context, @NonNull String repoId,
                                                                            @NonNull String login, @NonNull String url,
                                                                            @NonNull String defaultBranch,
                                                                            @NonNull String htmlUrl) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.readme), ViewerFragment.newInstance(url, htmlUrl, true)),
                new FragmentPagerAdapterModel(context.getString(R.string.files), RepoFilePathFragment.newInstance(login, repoId, null,
                        defaultBranch)),
                new FragmentPagerAdapterModel(context.getString(R.string.commits), RepoCommitsFragment.newInstance(repoId, login, defaultBranch)),
                new FragmentPagerAdapterModel(context.getString(R.string.releases), RepoReleasesFragment.newInstance(repoId, login)),
                new FragmentPagerAdapterModel(context.getString(R.string.contributors), RepoContributorsFragment.newInstance(repoId, login)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForSearch(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.repos), SearchReposFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.users), SearchUsersFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.issues), SearchIssuesFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.code), SearchCodeFragment.newInstance()))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForIssues(@NonNull Context context, long commentId) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.details), IssueTimelineFragment.newInstance(commentId)))
                .collect(Collectors.toList());
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForPullRequest(@NonNull Context context, @NonNull PullRequest pullRequest) {
        String login = pullRequest.getLogin();
        String repoId = pullRequest.getRepoId();
        int number = pullRequest.getNumber();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.details), PullRequestTimelineFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.commits), PullRequestCommitsFragment.newInstance(repoId, login, number)),
                new FragmentPagerAdapterModel(context.getString(R.string.files), PullRequestFilesFragment.newInstance(repoId, login, number)))
                .collect(Collectors.toList());
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoIssue(@NonNull Context context, @NonNull String login,
                                                                             @NonNull String repoId) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.opened),
                        RepoOpenedIssuesFragment.newInstance(repoId, login)),
                new FragmentPagerAdapterModel(context.getString(R.string.closed),
                        RepoClosedIssuesFragment.newInstance(repoId, login)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoPullRequest(@NonNull Context context, @NonNull String login,
                                                                                   @NonNull String repoId) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.opened),
                        RepoPullRequestFragment.newInstance(repoId, login, IssueState.open)),
                new FragmentPagerAdapterModel(context.getString(R.string.closed),
                        RepoPullRequestFragment.newInstance(repoId, login, IssueState.closed)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForCommit(@NonNull Context context, @NonNull Commit commitModel) {
        String login = commitModel.getLogin();
        String repoId = commitModel.getRepoId();
        String sha = commitModel.getSha();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.files),
                        CommitFilesFragment.newInstance(commitModel.getSha(), commitModel.getFiles())),
                new FragmentPagerAdapterModel(context.getString(R.string.comments),
                        CommitCommentsFragment.newInstance(login, repoId, sha)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForGist(@NonNull Context context, @NonNull Gist gistsModel) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.files), GistFilesListFragment.newInstance(gistsModel
                        .getFilesAsList(), false)),
                new FragmentPagerAdapterModel(context.getString(R.string.comments), GistCommentsFragment.newInstance(gistsModel.getGistId())))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForNotifications(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.unread), new UnreadNotificationsFragment()),
                new FragmentPagerAdapterModel(context.getString(R.string.all), AllNotificationsFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.app_name), new FastHubNotificationsFragment()))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForGists(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.my_gists), ProfileGistsFragment
                        .newInstance(Login.getUser().getLogin())),
                new FragmentPagerAdapterModel(context.getString(R.string.starred), StarredGistsFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.public_gists), GistsFragment.newInstance()))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForMyIssues(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.created),
                        MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.CREATED)),
                new FragmentPagerAdapterModel(context.getString(R.string.assigned),
                        MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.ASSIGNED)),
                new FragmentPagerAdapterModel(context.getString(R.string.mentioned),
                        MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.MENTIONED)),
                new FragmentPagerAdapterModel(context.getString(R.string.participated),
                        MyIssuesFragment.newInstance(IssueState.open, MyIssuesType.PARTICIPATED)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForMyPulls(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.created),
                        MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.CREATED)),
                new FragmentPagerAdapterModel(context.getString(R.string.assigned),
                        MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.ASSIGNED)),
                new FragmentPagerAdapterModel(context.getString(R.string.mentioned),
                        MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.MENTIONED)),
                new FragmentPagerAdapterModel(context.getString(R.string.review_requests),
                        MyPullRequestFragment.newInstance(IssueState.open, MyIssuesType.REVIEW)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForOrg(@NonNull Context context, @NonNull String login, boolean isMember) {
        return Stream.of(
                new FragmentPagerAdapterModel(context.getString(R.string.feeds),
                        isMember ? FeedsFragment.newInstance(login, true) : null),
                new FragmentPagerAdapterModel(context.getString(R.string.overview), OrgProfileOverviewFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.repos), OrgReposFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.people), OrgMembersFragment.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.teams), isMember ? OrgTeamFragment.newInstance(login) : null))
                .filter(fragmentPagerAdapterModel -> fragmentPagerAdapterModel.getFragment() != null)
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForTeam(@NonNull Context context, long id) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.members), TeamMembersFragment.newInstance(id)),
                new FragmentPagerAdapterModel(context.getString(R.string.repos), TeamReposFragment.newInstance(id)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForTheme() {
        return Stream.of(new FragmentPagerAdapterModel("", ThemeFragment.Companion.newInstance(R.style.ThemeLight)),
                new FragmentPagerAdapterModel("", ThemeFragment.Companion.newInstance(R.style.ThemeDark)),
                new FragmentPagerAdapterModel("", ThemeFragment.Companion.newInstance(R.style.ThemeAmlod)),
                new FragmentPagerAdapterModel("", ThemeFragment.Companion.newInstance(R.style.ThemeBluish)))
//                new FragmentPagerAdapterModel("", ThemeFragment.Companion.newInstance(R.style.ThemeMidnight)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForBranches(@NonNull Context context, @NonNull String repoId, @NonNull String login) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.branches),
                        BranchesFragment.Companion.newInstance(login, repoId, true)),
                new FragmentPagerAdapterModel(context.getString(R.string.tags),
                        BranchesFragment.Companion.newInstance(login, repoId, false)))
                .toList();
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoProjects(@NonNull Context context, @Nullable String repoId,
                                                                                @NonNull String login) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.open),
                        RepoProjectFragment.Companion.newInstance(login, repoId, IssueState.open)),
                new FragmentPagerAdapterModel(context.getString(R.string.closed),
                        RepoProjectFragment.Companion.newInstance(login, repoId, IssueState.closed)))
                .toList();
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForProjectColumns(@NonNull List<ProjectColumnModel> models, boolean isCollaborator) {
        return Stream.of(models)
                .map(projectColumnModel -> new FragmentPagerAdapterModel("", ProjectColumnFragment.Companion
                        .newInstance(projectColumnModel, isCollaborator), String.valueOf(projectColumnModel.getId())))
                .toList();
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForPinned(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.repos), PinnedReposFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.issues), PinnedIssueFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.pull_requests), PinnedPullRequestFragment.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.gists), PinnedGistFragment.newInstance()))
                .collect(Collectors.toList());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FragmentPagerAdapterModel that = (FragmentPagerAdapterModel) o;

        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
