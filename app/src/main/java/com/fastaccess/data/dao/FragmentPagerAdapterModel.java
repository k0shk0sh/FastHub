package com.fastaccess.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fastaccess.R;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.ui.modules.gists.gist.comments.GistCommentsView;
import com.fastaccess.ui.modules.gists.gist.files.GistFilesListView;
import com.fastaccess.ui.modules.profile.followers.ProfileFollowersView;
import com.fastaccess.ui.modules.profile.following.ProfileFollowingView;
import com.fastaccess.ui.modules.profile.gists.ProfileGistsView;
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
import com.fastaccess.ui.modules.repos.issues.issue.RepoIssuesView;
import com.fastaccess.ui.modules.repos.issues.issue.details.comments.IssueCommentsView;
import com.fastaccess.ui.modules.repos.issues.issue.details.events.IssueDetailsView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.RepoPullRequestView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.commits.PullRequestCommitsView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.events.PullRequestDetailsView;
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

@Getter @Setter
public class FragmentPagerAdapterModel {

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
                new FragmentPagerAdapterModel(context.getString(R.string.commits), RepoCommitsView.newInstance(repoId, login)),
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

    @NonNull public static List<FragmentPagerAdapterModel> buildForIssues(@NonNull Context context, @NonNull IssueModel issueModel) {
        String login = issueModel.getLogin();
        String repoId = issueModel.getRepoId();
        int number = issueModel.getNumber();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.details), IssueDetailsView.newInstance(issueModel)),
                new FragmentPagerAdapterModel(context.getString(R.string.comments), IssueCommentsView.newInstance(login, repoId, number)))
                .collect(Collectors.toList());
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForPullRequest(@NonNull Context context, @NonNull PullRequestModel pullRequest) {
        String login = pullRequest.getLogin();
        String repoId = pullRequest.getRepoId();
        int number = pullRequest.getNumber();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.details), PullRequestDetailsView.newInstance(pullRequest)),
                new FragmentPagerAdapterModel(context.getString(R.string.commits), PullRequestCommitsView.newInstance(repoId, login, number)),
                new FragmentPagerAdapterModel(context.getString(R.string.comments), IssueCommentsView.newInstance(login, repoId, number)))
                .collect(Collectors.toList());
    }


    @NonNull public static List<FragmentPagerAdapterModel> buildForRepoIssue(@NonNull Context context, @NonNull String login,
                                                                             @NonNull String repoId) {
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.opened),
                        RepoIssuesView.newInstance(repoId, login, IssueState.open)),
                new FragmentPagerAdapterModel(context.getString(R.string.closed),
                        RepoIssuesView.newInstance(repoId, login, IssueState.closed)))
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

    @NonNull public static List<FragmentPagerAdapterModel> buildForCommit(@NonNull Context context, @NonNull CommitModel commitModel) {
        String login = commitModel.getLogin();
        String repoId = commitModel.getRepoId();
        String sha = commitModel.getSha();
        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.commits), CommitFilesView.newInstance(commitModel.getFiles()))
                , new FragmentPagerAdapterModel(context.getString(R.string.comments), CommitCommentsView.newInstance(login, repoId, sha)))
                .collect(Collectors.toList());
    }

    @NonNull public static List<FragmentPagerAdapterModel> buildForGist(@NonNull Context context, @NonNull GistsModel gistsModel) {

        return Stream.of(new FragmentPagerAdapterModel(context.getString(R.string.files), GistFilesListView.newInstance(gistsModel.getFiles())),
                new FragmentPagerAdapterModel(context.getString(R.string.comments), GistCommentsView.newInstance(gistsModel.getGistId())))
                .collect(Collectors.toList());
    }

}
