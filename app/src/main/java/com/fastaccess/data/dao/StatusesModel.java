package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.data.dao.types.StatusStateType;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 10 Apr 2017, 3:16 AM
 */

@Getter @Setter public class StatusesModel implements Parcelable {
    private String url;
    private StatusStateType state;
    private String description;
    private String targetUrl;
    private String context;
    private Date createdAt;
    private Date updatedAt;

    public StatusesModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeString(this.description);
        dest.writeString(this.targetUrl);
        dest.writeString(this.context);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    private StatusesModel(Parcel in) {
        this.url = in.readString();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : StatusStateType.values()[tmpState];
        this.description = in.readString();
        this.targetUrl = in.readString();
        this.context = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<StatusesModel> CREATOR = new Creator<StatusesModel>() {
        @Override public StatusesModel createFromParcel(Parcel source) {return new StatusesModel(source);}

        @Override public StatusesModel[] newArray(int size) {return new StatusesModel[size];}
    };
}
