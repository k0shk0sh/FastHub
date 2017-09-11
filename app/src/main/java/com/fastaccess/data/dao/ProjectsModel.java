package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;

import java.util.Date;

/**
 * Created by kosh on 09/09/2017.
 */

public class ProjectsModel implements Parcelable {
    private String ownerUrl;
    private String url;
    private String htmlUrl;
    private String columnsUrl;
    private long id;
    private String name;
    private String body;
    private int number;
    private String state;
    private User creator;
    private Date createdAt;
    private Date updatedAt;

    public String getOwnerUrl() {
        return ownerUrl;
    }

    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getColumnsUrl() {
        return columnsUrl;
    }

    public void setColumnsUrl(String columnsUrl) {
        this.columnsUrl = columnsUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
        dest.writeString(this.ownerUrl);
        dest.writeString(this.url);
        dest.writeString(this.htmlUrl);
        dest.writeString(this.columnsUrl);
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.body);
        dest.writeInt(this.number);
        dest.writeString(this.state);
        dest.writeParcelable(this.creator, flags);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    public ProjectsModel() {}

    protected ProjectsModel(Parcel in) {
        this.ownerUrl = in.readString();
        this.url = in.readString();
        this.htmlUrl = in.readString();
        this.columnsUrl = in.readString();
        this.id = in.readLong();
        this.name = in.readString();
        this.body = in.readString();
        this.number = in.readInt();
        this.state = in.readString();
        this.creator = in.readParcelable(User.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<ProjectsModel> CREATOR = new Creator<ProjectsModel>() {
        @Override public ProjectsModel createFromParcel(Parcel source) {return new ProjectsModel(source);}

        @Override public ProjectsModel[] newArray(int size) {return new ProjectsModel[size];}
    };
}
