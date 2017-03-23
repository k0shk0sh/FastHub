package com.fastaccess.provider.rest;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.types.IssueState;

/**
 * Created by Kosh on 23 Mar 2017, 7:26 PM
 */

public class RepoQueryProvider {
    @NonNull public static String getIssuesPullRequerQuery(@NonNull String owner, @NonNull String repo,
                                                           @NonNull IssueState issueState, boolean isPr) {
        return new StringBuilder()
                .append("+")
                .append("type:")
                .append(isPr ? "pr" : "issue")
                .append("+")
                .append("repo:")
                .append(owner)
                .append("/")
                .append(repo)
                .append("+")
                .append("is:")
                .append(issueState.name())
                .toString();
    }
}
