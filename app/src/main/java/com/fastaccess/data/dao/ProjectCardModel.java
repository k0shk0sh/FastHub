package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.model.User;

import java.util.Date;

/**
 * Created by Hashemsergani on 11.09.17.
 */

public class ProjectCardModel implements Parcelable {
    private String url;
    private String columnUrl;
    private String contentUrl;
    private Integer id;
    private String note;
    private User creator;
    private Date createdAt;
    private Date updatedAt;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getColumnUrl() {
        return columnUrl;
    }

    public void setColumnUrl(String columnUrl) {
        this.columnUrl = columnUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public ProjectCardModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.columnUrl);
        dest.writeString(this.contentUrl);
        dest.writeValue(this.id);
        dest.writeString(this.note);
        dest.writeParcelable(this.creator, flags);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected ProjectCardModel(Parcel in) {
        this.url = in.readString();
        this.columnUrl = in.readString();
        this.contentUrl = in.readString();
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.note = in.readString();
        this.creator = in.readParcelable(User.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<ProjectCardModel> CREATOR = new Creator<ProjectCardModel>() {
        @Override public ProjectCardModel createFromParcel(Parcel source) {return new ProjectCardModel(source);}

        @Override public ProjectCardModel[] newArray(int size) {return new ProjectCardModel[size];}
    };
}
