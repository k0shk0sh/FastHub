package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.Repo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 19 Feb 2017, 1:17 AM
 */

@Getter @Setter
public class SearchCodeModel implements Parcelable {
    private String name;
    private String path;
    private String sha;
    private String url;
    private String gitUrl;
    private Repo repository;
    private double score;
    private String htmlUrl;


    public SearchCodeModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.sha);
        dest.writeString(this.url);
        dest.writeString(this.gitUrl);
        dest.writeParcelable(this.repository, flags);
        dest.writeDouble(this.score);
    }

    protected SearchCodeModel(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.sha = in.readString();
        this.url = in.readString();
        this.gitUrl = in.readString();
        this.repository = in.readParcelable(Repo.class.getClassLoader());
        this.score = in.readDouble();
    }

    public static final Creator<SearchCodeModel> CREATOR = new Creator<SearchCodeModel>() {
        @Override public SearchCodeModel createFromParcel(Parcel source) {return new SearchCodeModel(source);}

        @Override public SearchCodeModel[] newArray(int size) {return new SearchCodeModel[size];}
    };
}
