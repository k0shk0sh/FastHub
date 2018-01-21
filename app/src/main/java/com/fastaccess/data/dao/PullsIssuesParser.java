package com.fastaccess.data.dao;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.helper.InputHelper;

import java.util.List;

/**
 * Created by Kosh on 17 Dec 2016, 12:17 AM
 */

public class PullsIssuesParser implements Parcelable {

    private String login;
    private String repoId;
    private int number;

    public static PullsIssuesParser getForPullRequest(@NonNull String url) {
        Uri uri = Uri.parse(url);
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = null;
        String repo = null;
        String number = null;
        if (segments.size() > 3) {
            if (("pull".equals(segments.get(2)) || "pulls".equals(segments.get(2)))) {
                owner = segments.get(0);
                repo = segments.get(1);
                number = segments.get(3);
            } else if (("pull".equals(segments.get(3)) || "pulls".equals(segments.get(3))) && segments.size() > 4) {
                owner = segments.get(1);
                repo = segments.get(2);
                number = segments.get(4);
            } else {
                return null;
            }
        }
        if (InputHelper.isEmpty(number)) return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        PullsIssuesParser model = new PullsIssuesParser();
        model.setLogin(owner);
        model.setRepoId(repo);
        model.setNumber(issueNumber);
        return model;
    }

    public static PullsIssuesParser getForIssue(@NonNull String url) {
        Uri uri = Uri.parse(url);
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = null;
        String repo = null;
        String number = null;
        if (segments.size() > 3) {
            if (segments.get(2).equalsIgnoreCase("issues")) {
                owner = segments.get(0);
                repo = segments.get(1);
                number = segments.get(3);
            } else if (segments.get(3).equalsIgnoreCase("issues") && segments.size() > 4) {
                owner = segments.get(1);
                repo = segments.get(2);
                number = segments.get(4);
            } else {
                return null;
            }
        }
        if (InputHelper.isEmpty(number))
            return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
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

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.login);
        dest.writeString(this.repoId);
        dest.writeInt(this.number);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public PullsIssuesParser() {}

    protected PullsIssuesParser(Parcel in) {
        this.login = in.readString();
        this.repoId = in.readString();
        this.number = in.readInt();
    }

    public static final Parcelable.Creator<PullsIssuesParser> CREATOR = new Parcelable.Creator<PullsIssuesParser>() {
        @Override public PullsIssuesParser createFromParcel(Parcel source) {return new PullsIssuesParser(source);}

        @Override public PullsIssuesParser[] newArray(int size) {return new PullsIssuesParser[size];}
    };
}
