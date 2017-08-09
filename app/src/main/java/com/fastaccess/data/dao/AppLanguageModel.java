package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kosh on 20/07/2017.
 */

@Getter @Setter @AllArgsConstructor public class AppLanguageModel implements Parcelable {
    private String value;
    private String label;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.label);
    }

    private AppLanguageModel(Parcel in) {
        this.value = in.readString();
        this.label = in.readString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppLanguageModel that = (AppLanguageModel) o;

        return label != null ? label.equals(that.label) : that.label == null;
    }

    @Override public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }

    public static final Parcelable.Creator<AppLanguageModel> CREATOR = new Parcelable.Creator<AppLanguageModel>() {
        @Override public AppLanguageModel createFromParcel(Parcel source) {return new AppLanguageModel(source);}

        @Override public AppLanguageModel[] newArray(int size) {return new AppLanguageModel[size];}
    };
}
