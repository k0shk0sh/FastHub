package com.fastaccess.data.dao;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.helper.InputHelper;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 17 Dec 2016, 12:17 AM
 */

@Getter @Setter
public class PullsIssuesParser implements Parcelable {

    private String login;
    private String repoId;
    private int number;

    public static PullsIssuesParser getForPullRequest(@NonNull String url) {
        Uri uri = Uri.parse(url);
        List<String> segments = uri.getPathSegments();
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

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.login);
        dest.writeString(this.repoId);
        dest.writeInt(this.number);
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
