package com.fastaccess.data.dao;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 17 Dec 2016, 12:17 AM
 */

@Getter @Setter
public class PullsIssuesParser {

    private String login;
    private String repoId;
    private int number;

    public static PullsIssuesParser getForPullRequest(@NonNull String url) {
        Uri uri = Uri.parse(url);
        List<String> segments = uri.getPathSegments();
        Logger.e(url, uri, segments);
        if (segments == null || segments.size() < 4) return null;
        if (!"pull".equals(segments.get(2))) return null;
        String owner = segments.get(0);
        String repo = segments.get(1);
        String number = segments.get(3);
        if (InputHelper.isEmpty(number)) return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        PullsIssuesParser model = new PullsIssuesParser();
        model.setLogin(owner);
        model.setRepoId(repo);
        model.setNumber(issueNumber);
        return model;
    }

    public static PullsIssuesParser getForIssue(@NonNull String url) {
        Uri uri = Uri.parse(url);
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        if (!"issues".equals(segments.get(2))) return null;
        String owner = segments.get(0);
        String repo = segments.get(1);
        String number = segments.get(3);
        if (InputHelper.isEmpty(number)) return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        PullsIssuesParser model = new PullsIssuesParser();
        model.setLogin(owner);
        model.setRepoId(repo);
        model.setNumber(issueNumber);
        return model;
    }

    @Override public String toString() {
        return "PullsIssuesParser{" +
                "login='" + login + '\'' +
                ", repoId='" + repoId + '\'' +
                ", number=" + number +
                '}';
    }
}
