package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fastaccess.App;

import java.util.List;

import io.requery.Column;
import io.requery.Entity;
import rx.Observable;
import rx.Single;

/**
 * Created by Kosh on 01 Jan 2017, 11:20 PM
 */


@Entity
public abstract class AbstractSearchHistory implements Parcelable {
    @Column(unique = true) String text;

    public Single save(SearchHistory entity) {
        return App.getInstance().getDataStore()
                .delete(SearchHistory.class)
                .where(SearchHistory.TEXT.eq(entity.getText()))
                .get()
                .toSingle()
                .flatMap(integer -> App.getInstance().getDataStore().insert(entity));
    }

    public static Observable<List<SearchHistory>> getHistory() {
        return App.getInstance().getDataStore()
                .select(SearchHistory.class)
                .groupBy(SearchHistory.TEXT.asc())
                .get()
                .toObservable()
                .toList();
    }

    @Override public String toString() {
        return text;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSearchHistory that = (AbstractSearchHistory) o;

        return text != null ? text.equals(that.text) : that.text == null;

    }

    @Override public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.text);}

    public AbstractSearchHistory() {}

    public AbstractSearchHistory(String text) {
        this.text = text;
    }

    protected AbstractSearchHistory(Parcel in) {this.text = in.readString();}

    public static final Creator<SearchHistory> CREATOR = new Creator<SearchHistory>() {
        @Override public SearchHistory createFromParcel(Parcel source) {return new SearchHistory(source);}

        @Override public SearchHistory[] newArray(int size) {return new SearchHistory[size];}
    };
}
