package com.fastaccess.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.ui.modules.gists.gist.comments.GistCommentsView;
import com.fastaccess.ui.modules.gists.gist.files.GistFilesListView;
import com.fastaccess.ui.modules.main.issues.MyIssuesView;
import com.fastaccess.ui.modules.main.pullrequests.MyPullRequestView;
import com.fastaccess.ui.modules.profile.followers.ProfileFollowersView;
import com.fastaccess.ui.modules.profile.following.ProfileFollowingView;
import com.fastaccess.ui.modules.profile.gists.ProfileGistsView;
import com.fastaccess.ui.modules.profile.org.OrgProfileOverviewView;
import com.fastaccess.ui.modules.profile.org.feeds.OrgFeedsView;
import com.fastaccess.ui.modules.profile.org.members.OrgMembersView;
import com.fastaccess.ui.modules.profile.org.teams.OrgTeamView;
import com.fastaccess.ui.modules.profile.org.teams.details.members.TeamMembersView;
import com.fastaccess.ui.modules.profile.org.teams.details.repos.TeamReposView;
import com.fastaccess.ui.modules.profile.overview.ProfileOverviewView;
import com.fastaccess.ui.modules.profile.repos.ProfileReposView;
import com.fastaccess.ui.modules.profile.starred.ProfileStarredView;
import com.fastaccess.ui.modules.repos.code.commit.RepoCommitsView;
import com.fastaccess.ui.modules.repos.code.commit.details.comments.CommitCommentsView;
import com.fastaccess.ui.modules.repos.code.commit.details.files.CommitFilesView;
import com.fastaccess.ui.modules.repos.code.contributors.RepoContributorsView;
import com.fastaccess.ui.modules.repos.code.files.paths.RepoFilePathView;
import com.fastaccess.ui.modules.repos.code.prettifier.ViewerView;
import com.fastaccess.ui.modules.repos.code.releases.RepoReleasesView;
import com.fastaccess.ui.modules.repos.issues.issue.RepoClosedIssuesView;
import com.fastaccess.ui.modules.repos.issues.issue.RepoOpenedIssuesView;
import com.fastaccess.ui.modules.repos.issues.issue.details.timeline.IssueTimelineView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.RepoPullRequestView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits.PullRequestCommitsView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineView;
import com.fastaccess.ui.modules.search.code.SearchCodeView;
import com.fastaccess.ui.modules.search.issues.SearchIssuesView;
import com.fastaccess.ui.modules.search.repos.SearchReposView;
import com.fastaccess.ui.modules.search.users.SearchUsersView;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 03 Dec 2016, 9:26 AM
 */

@Getter @Setter public class FragmentPagerAdapterModel {

    private String title;
    private Fragment fragment;

    private FragmentPagerAdapterModel(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForProfile(@NonNull Context context, @NonNull String login) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.overview), ProfileOverviewView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.repos), ProfileReposView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.starred), ProfileStarredView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.gists), ProfileGistsView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.followers), ProfileFollowersView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.following), ProfileFollowingView.newInstance(login)))
                .collect(Collectors.toList());
    }

    public static List<FragmentPagerAdapterModel> buildForRepoCode(@NonNull Context context, @NonNull String repoId,
                                                                   @NonNull String login, @NonNull String url,
                                                                   @NonNull String defaultBranch) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.readme), ViewerView.newInstance(url, true)),
                new FragmentPagerAdapterModel(context.getString(R.string.files), RepoFilePathView.newInstance(login, repoId, null, defaultBranch)),
                new FragmentPagerAdapterModel(context.getString(R.string.commits), RepoCommitsView.newInstance(repoId, login, defaultBranch)),
                new FragmentPagerAdapterModel(context.getString(R.string.releases), RepoReleasesView.newInstance(repoId, login)),
                new FragmentPagerAdapterModel(context.getString(R.string.contributors), RepoContributorsView.newInstance(repoId, login)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForSearch(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.repos), SearchReposView.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.users), SearchUsersView.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.issues), SearchIssuesView.newInstance()),
                new FragmentPagerAdapterModel(context.getString(R.string.code), SearchCodeView.newInstance()))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForIssues(@NonNull Context context, @NonNull Issue issueModel) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.details), IssueTimelineView.newInstance(issueModel)))
                .collect(Collectors.toList());
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForPullRequest(@NonNull Context context, @NonNull PullRequest pullRequest) {
        String login = pullRequest.getLogin();
        String repoId = pullRequest.getRepoId();
        int number = pullRequest.getNumber();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.details), PullRequestTimelineView.newInstance(pullRequest)),
                new FragmentPagerAdapterModel(context.getString(R.string.commits), PullRequestCommitsView.newInstance(repoId, login, number)))
                .collect(Collectors.toList());
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoIssue(@NonNull Context context, @NonNull String login,
                                                                             @NonNull String repoId) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.opened),
                        RepoOpenedIssuesView.newInstance(repoId, login)),
                new FragmentPagerAdapterModel(context.getString(R.string.closed),
                        RepoClosedIssuesView.newInstance(repoId, login)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoPullRequest(@NonNull Context context, @NonNull String login,
                                                                                   @NonNull String repoId) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.opened),
                        RepoPullRequestView.newInstance(repoId, login, IssueState.open)),
                new FragmentPagerAdapterModel(context.getString(R.string.closed),
                        RepoPullRequestView.newInstance(repoId, login, IssueState.closed)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForCommit(@NonNull Context context, @NonNull Commit commitModel) {
        String login = commitModel.getLogin();
        String repoId = commitModel.getRepoId();
        String sha = commitModel.getSha();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.commits), CommitFilesView.newInstance(commitModel.getSha(),
                commitModel.getFiles()))
                , new FragmentPagerAdapterModel(context.getString(R.string.comments), CommitCommentsView.newInstance(login, repoId, sha)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForGist(@NonNull Context context, @NonNull Gist gistsModel) {

        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.files), GistFilesListView.newInstance(gistsModel.getFiles())),
                new FragmentPagerAdapterModel(context.getString(R.string.comments), GistCommentsView.newInstance(gistsModel.getGistId())))
                .collect(Collectors.toList());
    }

    public static List<FragmentPagerAdapterModel> buildForMyIssues(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.open), MyIssuesView.newInstance(IssueState.open))
                , new FragmentPagerAdapterModel(context.getString(R.string.closed), MyIssuesView.newInstance(IssueState.closed)))
                .collect(Collectors.toList());
    }

    public static List<FragmentPagerAdapterModel> buildForMyPulls(@NonNull Context context) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.open), MyPullRequestView.newInstance(IssueState.open))
                , new FragmentPagerAdapterModel(context.getString(R.string.closed), MyPullRequestView.newInstance(IssueState.closed)))
                .collect(Collectors.toList());
    }

    public static List<FragmentPagerAdapterModel> buildForOrg(@NonNull Context context, @NonNull String login, boolean isMember) {
        return Stream.of(
                new FragmentPagerAdapterModel(context.getString(R.string.feeds), isMember ? OrgFeedsView.newInstance(login) : null),
                new FragmentPagerAdapterModel(context.getString(R.string.overview), OrgProfileOverviewView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.repos), ProfileReposView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.people), OrgMembersView.newInstance(login)),
                new FragmentPagerAdapterModel(context.getString(R.string.teams), isMember ? OrgTeamView.newInstance(login) : null))
                .filter(fragmentPagerAdapterModel -> fragmentPagerAdapterModel.getFragment() != null)
                .collect(Collectors.toList());
    }

    public static List<FragmentPagerAdapterModel> buildForTeam(@NonNull Context context, long id) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.members), TeamMembersView.newInstance(id)),
                new FragmentPagerAdapterModel(context.getString(R.string.repos), TeamReposView.newInstance(id)))
                .collect(Collectors.toList());
    }
}
