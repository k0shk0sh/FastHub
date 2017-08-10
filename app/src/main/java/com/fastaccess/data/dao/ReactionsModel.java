package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import pr.PullRequestTimelineQuery;

/**
 * Created by Kosh on 28 Mar 2017, 9:15 PM
 */

@Getter @Setter public class ReactionsModel implements Parcelable {

    private long id;
    private String url;
    private int total_count;
    @SerializedName("+1") private int plusOne;
    @SerializedName("-1") private int minusOne;
    private int laugh;
    private int hooray;
    private int confused;
    private int heart;
    private String content;
    private User user;
    private boolean viewerHasReacted;
    private boolean isCallingApi;

    public ReactionsModel() {}

    @Override public String toString() {
        return "ReactionsModel{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", total_count=" + total_count +
                ", plusOne=" + plusOne +
                ", minusOne=" + minusOne +
                ", laugh=" + laugh +
                ", hooray=" + hooray +
                ", confused=" + confused +
                ", heart=" + heart +
                '}';
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeInt(this.total_count);
        dest.writeInt(this.plusOne);
        dest.writeInt(this.minusOne);
        dest.writeInt(this.laugh);
        dest.writeInt(this.hooray);
        dest.writeInt(this.confused);
        dest.writeInt(this.heart);
        dest.writeString(this.content);
        dest.writeParcelable(this.user, flags);
        dest.writeByte(this.isCallingApi ? (byte) 1 : (byte) 0);
    }

    protected ReactionsModel(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.total_count = in.readInt();
        this.plusOne = in.readInt();
        this.minusOne = in.readInt();
        this.laugh = in.readInt();
        this.hooray = in.readInt();
        this.confused = in.readInt();
        this.heart = in.readInt();
        this.content = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.isCallingApi = in.readByte() != 0;
    }

    public static final Creator<ReactionsModel> CREATOR = new Creator<ReactionsModel>() {
        @Override public ReactionsModel createFromParcel(Parcel source) {return new ReactionsModel(source);}

        @Override public ReactionsModel[] newArray(int size) {return new ReactionsModel[size];}
    };

    @NonNull public static List<ReactionsModel> getReaction(@Nullable List<PullRequestTimelineQuery.ReactionGroup1> reactions) {
        List<ReactionsModel> models = new ArrayList<>();
        if (reactions != null && !reactions.isEmpty()) {
            for (PullRequestTimelineQuery.ReactionGroup1 reaction : reactions) {
                ReactionsModel model = new ReactionsModel();
                model.setContent(reaction.content().name());
                model.setViewerHasReacted(reaction.viewerHasReacted());
                model.setTotal_count(reaction.users().totalCount());
                models.add(model);
            }
        }
        return models;
    }
    @NonNull public static List<ReactionsModel> getReaction2(@Nullable List<PullRequestTimelineQuery.ReactionGroup2> reactions) {
        List<ReactionsModel> models = new ArrayList<>();
        if (reactions != null && !reactions.isEmpty()) {
            for (PullRequestTimelineQuery.ReactionGroup2 reaction : reactions) {
                ReactionsModel model = new ReactionsModel();
                model.setContent(reaction.content().name());
                model.setViewerHasReacted(reaction.viewerHasReacted());
                model.setTotal_count(reaction.users().totalCount());
                models.add(model);
            }
        }
        return models;
    }
}
