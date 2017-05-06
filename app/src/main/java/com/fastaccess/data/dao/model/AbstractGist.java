package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.annimon.stream.LongStream;
import com.annimon.stream.Stream;
import com.fastaccess.App;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.GithubFileModel;
import com.fastaccess.data.dao.converters.GitHubFilesConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.rx.SingleEntityStore;
import lombok.NoArgsConstructor;
import rx.Observable;
import rx.Single;

/**
 * Created by Kosh on 16 Mar 2017, 7:32 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractGist implements Parcelable {
    @SerializedName("nooope") @Key long id;
    String url;
    String forksUrl;
    String commitsUrl;
    String gitPullUrl;
    String gitPushUrl;
    String htmlUrl;
    boolean publicX;
    Date createdAt;
    Date updatedAt;
    String description;
    int comments;
    String commentsUrl;
    boolean truncated;
    String ownerName;
    @SerializedName("id") String gistId;
    @Convert(GitHubFilesConverter.class) GithubFileModel files;
    @Column(name = "user_column") @Convert(UserConverter.class) User user;
    @Convert(UserConverter.class) User owner;

    public Single save(Gist modelEntity) {
        return App.getInstance().getDataStore()
                .delete(Gist.class)
                .where(Gist.ID.eq(modelEntity.getId()))
                .get()
                .toSingle()
                .flatMap(integer -> App.getInstance().getDataStore().insert(modelEntity));
    }

    public static Observable save(@NonNull List<Gist> gists) {
        SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(Gist.class)
                .where(Gist.OWNER_NAME.isNull())
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(gists))
                .flatMap(gist -> singleEntityStore.insert(gist).toObservable()));
    }

    public static Observable save(@NonNull List<Gist> gists, @NonNull String ownerName) {
        SingleEntityStore<Persistable> singleEntityStore = App.getInstance().getDataStore();
        return RxHelper.safeObservable(singleEntityStore.delete(Gist.class)
                .where(Gist.OWNER_NAME.equal(ownerName))
                .get()
                .toSingle()
                .toObservable()
                .flatMap(integer -> Observable.from(gists))
                .flatMap(gist -> {
                    gist.setOwnerName(ownerName);
                    return gist.save(gist).toObservable();
                }));
    }

    @NonNull public static Observable<List<Gist>> getMyGists(@NonNull String ownerName) {
        return App.getInstance()
                .getDataStore()
                .select(Gist.class)
                .where(Gist.OWNER_NAME.equal(ownerName))
                .get()
                .toObservable()
                .toList();
    }

    @NonNull public static Observable<List<Gist>> getGists() {
        return App.getInstance()
                .getDataStore()
                .select(Gist.class)
                .where(Gist.OWNER_NAME.isNull())
                .get()
                .toObservable()
                .toList();
    }

    public static Observable<Gist> getGist(@NonNull String gistId) {
        return App.getInstance()
                .getDataStore()
                .select(Gist.class)
                .where(Gist.GIST_ID.eq(gistId))
                .get()
                .toObservable();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractGist that = (AbstractGist) o;
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
        return getDisplayTitle(isFromProfile, false);
    }

    @NonNull public SpannableBuilder getDisplayTitle(boolean isFromProfile, boolean gistView) {
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        boolean addDescription = true;
        if (!isFromProfile) {
            if (owner != null) {
                spannableBuilder.bold(owner.getLogin());
            } else if (user != null) {
                spannableBuilder.bold(user.getLogin());
            } else {
                spannableBuilder.bold("Anonymous");
            }
            if (!gistView) {
                List<FilesListModel> files = getFilesAsList();
                if (!files.isEmpty()) {
                    FilesListModel filesListModel = files.get(0);
                    if (!InputHelper.isEmpty(filesListModel.getFilename()) && filesListModel.getFilename().trim().length() > 2) {
                        spannableBuilder.append(" ").append("/").append(" ")
                                .append(filesListModel.getFilename());
                        addDescription = false;
                    }
                }
            }
        }
        if (!InputHelper.isEmpty(description) && addDescription) {
            if (!InputHelper.isEmpty(spannableBuilder.toString())) {
                spannableBuilder.append(" ").append("/").append(" ");
            }
            spannableBuilder.append(description);
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
        dest.writeString(this.gistId);
        dest.writeSerializable(this.files);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.owner, flags);
    }

    protected AbstractGist(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.forksUrl = in.readString();
        this.commitsUrl = in.readString();
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
        this.gistId = in.readString();
        this.files = (GithubFileModel) in.readSerializable();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.owner = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Gist> CREATOR = new Creator<Gist>() {
        @Override public Gist createFromParcel(Parcel source) {return new Gist(source);}

        @Override public Gist[] newArray(int size) {return new Gist[size];}
    };
}
