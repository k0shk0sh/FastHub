package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Hashemsergani on 11.09.17.
 */

public class ProjectColumnModel implements Parcelable {

    private int id;
    private String name;
    private String url;
    private String projectUrl;
    private String cardsUrl;
    private Date createdAt;
    private Date updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getCardsUrl() {
        return cardsUrl;
    }

    public void setCardsUrl(String cardsUrl) {
        this.cardsUrl = cardsUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.url);
        dest.writeString(this.projectUrl);
        dest.writeString(this.cardsUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    public ProjectColumnModel() {}

    protected ProjectColumnModel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.url = in.readString();
        this.projectUrl = in.readString();
        this.cardsUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Parcelable.Creator<ProjectColumnModel> CREATOR = new Parcelable.Creator<ProjectColumnModel>() {
        @Override public ProjectColumnModel createFromParcel(Parcel source) {return new ProjectColumnModel(source);}

        @Override public ProjectColumnModel[] newArray(int size) {return new ProjectColumnModel[size];}
    };
}
