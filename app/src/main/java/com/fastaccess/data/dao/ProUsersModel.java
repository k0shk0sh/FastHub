package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Hashemsergani on 03.10.17.
 */

public class ProUsersModel implements Parcelable {
    private int count;
    private boolean allowed;
    private int type;
    private boolean isBlocked;

    public int getCount() { return count;}

    public void setCount(int count) { this.count = count;}

    public boolean isAllowed() { return allowed;}

    public void setAllowed(boolean allowed) { this.allowed = allowed;}

    public int getType() { return type;}

    public void setType(int type) { this.type = type;}

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override public String toString() {
        return "ProUsersModel{" +
                ", count=" + count +
                ", allowed=" + allowed +
                ", type=" + type +
                '}';
    }

    public ProUsersModel() {}

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeByte(this.allowed ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
        dest.writeByte(this.isBlocked ? (byte) 1 : (byte) 0);
    }

    protected ProUsersModel(Parcel in) {
        this.count = in.readInt();
        this.allowed = in.readByte() != 0;
        this.type = in.readInt();
        this.isBlocked = in.readByte() != 0;
    }

    public static final Creator<ProUsersModel> CREATOR = new Creator<ProUsersModel>() {
        @Override public ProUsersModel createFromParcel(Parcel source) {return new ProUsersModel(source);}

        @Override public ProUsersModel[] newArray(int size) {return new ProUsersModel[size];}
    };
}
