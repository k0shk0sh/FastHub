package com.fastaccess.data.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.LongStream;
import com.annimon.stream.Stream;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.google.gson.annotations.SerializedName;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.GistsModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 09 Feb 2017, 2:45 AM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class GistsModel implements Parcelable {

    @SerializedName("nooope") @Id @Column long id;
    @Column String url;
    @Column String forksUrl;
    @Column String commitsUrl;
    @SerializedName("id") @Column String gistId;
    @Column String gitPullUrl;
    @Column String gitPushUrl;
    @Column String htmlUrl;
    @Column boolean publicX;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column String description;
    @Column int comments;
    @Column String commentsUrl;
    @Column boolean truncated;
    @Column String ownerName;
    @Column(onDeleteCascade = true, handleRecursively = false) GithubFileModel files;
    @Column(onDeleteCascade = true, handleRecursively = false) UserModel user;
    @Column(onDeleteCascade = true, handleRecursively = false) UserModel owner;

    public static Completable save(@NonNull List<GistsModel> gists) {
        return Delete.from(GistsModelTable.GISTS_MODEL)
                .where(GistsModelTable.GISTS_MODEL.OWNER_NAME.isNull())
                .observe()
                .toCompletable()
                .andThen(persist(gists).observe());

    }

    public static Completable save(@NonNull List<GistsModel> gists, @NonNull String ownerName) {
        return Delete.from(GistsModelTable.GISTS_MODEL)
                .where(GistsModelTable.GISTS_MODEL.OWNER_NAME.is(ownerName))
                .observe()
                .toCompletable()
                .andThen(Observable.from(gists)
                        .map(gistsModel -> {
                            gistsModel.setOwnerName(ownerName);
                            return gistsModel.persist().observe();
                        }))
                .toCompletable();
    }

    @NonNull public static Observable<List<GistsModel>> getMyGists(@NonNull String ownerName) {
        return Select.from(GistsModelTable.GISTS_MODEL)
                .where(GistsModelTable.GISTS_MODEL.OWNER_NAME.is(ownerName))
                .queryDeep()
                .observe()
                .runQuery();
    }

    @NonNull public static Observable<List<GistsModel>> getGists() {
        return Select.from(GistsModelTable.GISTS_MODEL)
                .where(GistsModelTable.GISTS_MODEL.OWNER_NAME.isNull())
                .queryDeep()
                .observe()
                .runQuery();
    }

    @Nullable public static Observable<GistsModel> getGist(@NonNull String gistId) {
        return Select.from(GistsModelTable.GISTS_MODEL)
                .where(GistsModelTable.GISTS_MODEL.GIST_ID.is(gistId))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GistsModel that = (GistsModel) o;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @NonNull public List<FilesListModel> getFilesAsList() {
        List<FilesListModel> models = new ArrayList<>();
        if (files != null) {
            models.addAll(files.values());
        }
        return models;
    }

    @NonNull public SpannableBuilder getDisplayTitle(boolean isFromProfile) {
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        if (!isFromProfile) {
            if (getOwner() != null) {
                spannableBuilder.bold(getOwner().getLogin());
            } else if (getUser() != null) {
                spannableBuilder.bold(getUser().getLogin());
            } else {
                spannableBuilder.bold("Anonymous");
            }
        }
        if (!InputHelper.isEmpty(getDescription())) {
            if (!InputHelper.isEmpty(spannableBuilder.toString())) {
                spannableBuilder.append("/");
            }
            spannableBuilder.append(getDescription());
        }
        if (InputHelper.isEmpty(spannableBuilder.toString())) {
            if (isFromProfile) spannableBuilder.bold("N/A");
        }
        return spannableBuilder;
    }

    public long getSize() {
        List<FilesListModel> models = getFilesAsList();
        if (!models.isEmpty()) {
            return Stream.of(models).flatMapToLong(filesListModel -> LongStream.of(filesListModel.getSize())).sum();
        }
        return 0;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeString(this.forksUrl);
        dest.writeString(this.commitsUrl);
        dest.writeString(this.gistId);
        dest.writeString(this.gitPullUrl);
        dest.writeString(this.gitPushUrl);
        dest.writeString(this.htmlUrl);
        dest.writeByte(this.publicX ? (byte) 1 : (byte) 0);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeString(this.description);
        dest.writeInt(this.comments);
        dest.writeString(this.commentsUrl);
        dest.writeByte(this.truncated ? (byte) 1 : (byte) 0);
        dest.writeString(this.ownerName);
        dest.writeSerializable(this.files);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.owner, flags);
    }

    protected GistsModel(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.forksUrl = in.readString();
        this.commitsUrl = in.readString();
        this.gistId = in.readString();
        this.gitPullUrl = in.readString();
        this.gitPushUrl = in.readString();
        this.htmlUrl = in.readString();
        this.publicX = in.readByte() != 0;
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.description = in.readString();
        this.comments = in.readInt();
        this.commentsUrl = in.readString();
        this.truncated = in.readByte() != 0;
        this.ownerName = in.readString();
        this.files = (GithubFileModel) in.readSerializable();
        this.user = in.readParcelable(UserModel.class.getClassLoader());
        this.owner = in.readParcelable(UserModel.class.getClassLoader());
    }

    public static final Creator<GistsModel> CREATOR = new Creator<GistsModel>() {
        @Override public GistsModel createFromParcel(Parcel source) {return new GistsModel(source);}

        @Override public GistsModel[] newArray(int size) {return new GistsModel[size];}
    };
}
