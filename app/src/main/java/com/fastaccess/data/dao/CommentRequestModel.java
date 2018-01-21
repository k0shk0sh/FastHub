package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 20 Nov 2016, 10:40 AM
 */

@Getter @Setter public class CommentRequestModel implements Parcelable {
    public String body;
    @SerializedName("in_reply_to") public Long inReplyTo;
    public String path;
    public Integer position;
    public Integer line;

    public CommentRequestModel() {}

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentRequestModel that = (CommentRequestModel) o;
        return (path != null ? path.equals(that.path) : that.path == null) &&
                (position != null ? position.equals(that.position) : that.position == null);
    }

    @Override public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeValue(this.inReplyTo);
        dest.writeString(this.path);
        dest.writeValue(this.position);
        dest.writeValue(this.line);
    }

    @Override public String toString() {
        return "CommentRequestModel{" +
                "body='" + body + '\'' +
                ", inReplyTo=" + inReplyTo +
                ", path='" + path + '\'' +
                ", position=" + position +
                ", line=" + line +
                '}';
    }

    private CommentRequestModel(Parcel in) {
        this.body = in.readString();
        this.inReplyTo = (Long) in.readValue(Long.class.getClassLoader());
        this.path = in.readString();
        this.position = (Integer) in.readValue(Integer.class.getClassLoader());
        this.line = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<CommentRequestModel> CREATOR = new Creator<CommentRequestModel>() {
        @Override public CommentRequestModel createFromParcel(Parcel source) {return new CommentRequestModel(source);}

        @Override public CommentRequestModel[] newArray(int size) {return new CommentRequestModel[size];}
    };
}
