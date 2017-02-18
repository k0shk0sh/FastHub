package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Kosh on 08 Dec 2016, 8:47 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class MilestoneModel implements Parcelable {

    @Column String url;
    @Column String title;
    @Column String state;
    @Column String description;
    @Column @Id(autoIncrement = false) long id;
    @Column int number;
    @Column UserModel creator;
    @Column String htmlUr;
    @Column int openIssues;
    @Column int closedIssues;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column Date closedAt;
    @Column Date dueOn;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeString(this.state);
        dest.writeString(this.description);
        dest.writeLong(this.id);
        dest.writeInt(this.number);
        dest.writeParcelable(this.creator, flags);
        dest.writeString(this.htmlUr);
        dest.writeInt(this.openIssues);
        dest.writeInt(this.closedIssues);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeLong(this.closedAt != null ? this.closedAt.getTime() : -1);
        dest.writeLong(this.dueOn != null ? this.dueOn.getTime() : -1);
    }

    protected MilestoneModel(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.state = in.readString();
        this.description = in.readString();
        this.id = in.readLong();
        this.number = in.readInt();
        this.creator = in.readParcelable(UserModel.class.getClassLoader());
        this.htmlUr = in.readString();
        this.openIssues = in.readInt();
        this.closedIssues = in.readInt();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        long tmpClosedAt = in.readLong();
        this.closedAt = tmpClosedAt == -1 ? null : new Date(tmpClosedAt);
        long tmpDueOn = in.readLong();
        this.dueOn = tmpDueOn == -1 ? null : new Date(tmpDueOn);
    }

    public static final Creator<MilestoneModel> CREATOR = new Creator<MilestoneModel>() {
        @Override public MilestoneModel createFromParcel(Parcel source) {return new MilestoneModel(source);}

        @Override public MilestoneModel[] newArray(int size) {return new MilestoneModel[size];}
    };
}
