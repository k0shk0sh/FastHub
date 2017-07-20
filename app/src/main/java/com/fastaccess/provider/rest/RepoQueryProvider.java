package com.fastaccess.provider.rest;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.IssueState;

/**
 * Created by Kosh on 23 Mar 2017, 7:26 PM
 */

public class RepoQueryProvider {
    @NonNull public static String getIssuesPullRequestQuery(@NonNull String owner, @NonNull String repo,
                                                            @NonNull IssueState issueState, boolean isPr) {
        return "+" + "type:" + (isPr ? "pr" : "issue") +
                "+" + "repo:" + owner + "/" +
                repo + "+" + "is:" + issueState.name();
    }

    @NonNull public static String getMyIssuesPullRequestQuery(@NonNull String username, @NonNull IssueState issueState, boolean isPr) {
        return "type:" + (isPr ? "pr" : "issue") +
                "+" + "author:" + username +
                "+is:" + issueState.name();
    }

    @NonNull public static String getAssigned(@NonNull String username, @NonNull IssueState issueState, boolean isPr) {
        return "type:" + (isPr ? "pr" : "issue") +
                "+" + "assignee:" + username +
                "+is:" + issueState.name();
    }

    @NonNull public static String getMentioned(@NonNull String username, @NonNull IssueState issueState, boolean isPr) {
        return "type:" + (isPr ? "pr" : "issue") +
                "+" + "mentions:" + username +
                "+is:" + issueState.name();
    }

    @NonNull public static String getReviewRequests(@NonNull String username, @NonNull IssueState issueState) {
        return "type:pr" +
                "+" + "review-requested:" + username +
                "+is:" + issueState.name();
    }

    public static String getParticipated(@NonNull String username, @NonNull IssueState issueState, boolean isPr) {
        return "type:" + (isPr ? "pr" : "issue") +
                "+" + "involves:" + username +
                "+is:" + issueState.name();
    }
}
