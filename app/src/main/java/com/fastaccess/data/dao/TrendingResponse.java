package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.florent37.retrojsoup.annotations.JsoupText;

import lombok.ToString;

/**
 * Created by Kosh on 02 Jun 2017, 1:06 PM
 */

@ToString public class TrendingResponse implements Parcelable { // retroSoup doesn't like it to be kolin class.

    @JsoupText(".repo-list > li > .d-inline-block > h3 > a") public String title;
    @JsoupText(".repo-list > li > .py-1 > p") public String description;
    @JsoupText(".repo-list > li > .f6 > a[href*=/stargazers]") public String stars;
    @JsoupText(".repo-list > li > .f6 > a[href*=/network]") public String forks;
    @JsoupText(".repo-list > li > .f6 > span.float-right") public String todayStars;

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.stars);
        dest.writeString(this.forks);
        dest.writeString(this.todayStars);
    }

    public TrendingResponse() {}

    protected TrendingResponse(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.stars = in.readString();
        this.forks = in.readString();
        this.todayStars = in.readString();
    }

    public static final Parcelable.Creator<TrendingResponse> CREATOR = new Parcelable.Creator<TrendingResponse>() {
        @Override public TrendingResponse createFromParcel(Parcel source) {return new TrendingResponse(source);}

        @Override public TrendingResponse[] newArray(int size) {return new TrendingResponse[size];}
    };
}
