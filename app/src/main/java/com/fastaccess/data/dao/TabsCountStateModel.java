package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 27 Apr 2017, 6:10 PM
 */

@Getter @Setter public class TabsCountStateModel implements Parcelable, Serializable {
    private int count;
    private int tabIndex;

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TabsCountStateModel that = (TabsCountStateModel) o;
        return count == that.count && tabIndex == that.tabIndex;
    }

    @Override public int hashCode() {
        int result = count;
        result = 31 * result + tabIndex;
        return result;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeInt(this.tabIndex);
    }

    public TabsCountStateModel() {}

    protected TabsCountStateModel(Parcel in) {
        this.count = in.readInt();
        this.tabIndex = in.readInt();
    }

    public static final Parcelable.Creator<TabsCountStateModel> CREATOR = new Parcelable.Creator<TabsCountStateModel>() {
        @Override public TabsCountStateModel createFromParcel(Parcel source) {return new TabsCountStateModel(source);}

        @Override public TabsCountStateModel[] newArray(int size) {return new TabsCountStateModel[size];}
    };
}
