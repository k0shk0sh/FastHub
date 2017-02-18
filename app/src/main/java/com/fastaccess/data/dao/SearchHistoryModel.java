package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.siimkinks.sqlitemagic.SearchHistoryModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Table;
import com.siimkinks.sqlitemagic.annotation.Unique;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 01 Jan 2017, 11:20 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class SearchHistoryModel implements Parcelable {

    @Column @Unique String text;

    public SearchHistoryModel(String text) {
        this.text = text;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.text);}

    protected SearchHistoryModel(Parcel in) {this.text = in.readString();}

    public static final Creator<SearchHistoryModel> CREATOR = new Creator<SearchHistoryModel>() {
        @Override public SearchHistoryModel createFromParcel(Parcel source) {return new SearchHistoryModel(source);}

        @Override public SearchHistoryModel[] newArray(int size) {return new SearchHistoryModel[size];}
    };

    public Completable save() {
        return persist().observe()
                .onErrorReturn(throwable -> {
                    throwable.printStackTrace();
                    return null;
                })
                .toCompletable();
    }

    public static Observable<List<SearchHistoryModel>> getHistory() {
        return Select.from(SearchHistoryModelTable.SEARCH_HISTORY_MODEL)
                .orderBy(SearchHistoryModelTable.SEARCH_HISTORY_MODEL.TEXT.asc())
                .limit(10)
                .observe()
                .runQuery();
    }

    @Override public String toString() {
        return text;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchHistoryModel that = (SearchHistoryModel) o;

        return text != null ? text.equals(that.text) : that.text == null;

    }

    @Override public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}
