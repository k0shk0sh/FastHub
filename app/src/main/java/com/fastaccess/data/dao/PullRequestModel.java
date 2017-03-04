package com.fastaccess.data.dao;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.siimkinks.sqlitemagic.Delete;
import com.siimkinks.sqlitemagic.PullRequestModelTable;
import com.siimkinks.sqlitemagic.Select;
import com.siimkinks.sqlitemagic.annotation.Column;
import com.siimkinks.sqlitemagic.annotation.Id;
import com.siimkinks.sqlitemagic.annotation.Table;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rx.Completable;
import rx.Observable;

/**
 * Created by Kosh on 08 Dec 2016, 8:51 PM
 */

@Getter @Setter @NoArgsConstructor @Table(persistAll = true)
public class PullRequestModel implements Parcelable {
    @Column String url;
    @Column String body;
    @Column String title;
    @Column @Id(autoIncrement = false) long id;
    @Column int comments;
    @Column int number;
    @Column boolean locked;
    @Column boolean mergable;
    @Column boolean merged;
    @Column boolean mergeable;
    @Column int commits;
    @Column int additions;
    @Column int deletions;
    @Column IssueState state;
    @Column UserModel user;
    @Column UserModel assignee;
    @Column LabelListModel labels;
    @Column MilestoneModel milestone;
    @Column(handleRecursively = false) CommitModel base;
    @Column(handleRecursively = false) CommitModel head;
    @Column String bodyHtml;
    @Column String htmlUrl;
    @Column(handleRecursively = false) PullRequestModel pullRequest;
    @Column Date closedAt;
    @Column Date createdAt;
    @Column Date updatedAt;
    @Column UserModel closedBy;
    @Column int changedFiles;
    @Column String diffUrl;
    @Column String patchUrl;
    @Column String mergeCommitSha;
    @Column Date mergedAt;
    @Column UserModel mergedBy;
    @Column String mergeState;
    @Column int reviewComments;
    @Column String repoId;
    @Column String login;

    public Completable save() {
        return persist().observe().toCompletable();
    }

    public static Completable save(@NonNull List<PullRequestModel> models, @NonNull String repoId, @NonNull String login) {
        return Delete.from(PullRequestModelTable.PULL_REQUEST_MODEL)
                .where(PullRequestModelTable.PULL_REQUEST_MODEL.REPO_ID.is(repoId))
                .observe()
                .toCompletable()
                .andThen(Observable.from(models)
                        .map(pull -> {
                            pull.setRepoId(repoId);
                            pull.setLogin(login);
                            return pull.save();
                        }))
                .toCompletable();
    }

    public static Observable<List<PullRequestModel>> getPullRequests(@NonNull String repoId, @NonNull String login, @NonNull IssueState issueState) {
        return Select.from(PullRequestModelTable.PULL_REQUEST_MODEL)
                .where(PullRequestModelTable.PULL_REQUEST_MODEL.REPO_ID.is(repoId)
                        .and(PullRequestModelTable.PULL_REQUEST_MODEL.LOGIN.is(login))
                        .and(PullRequestModelTable.PULL_REQUEST_MODEL.STATE.is(issueState)))
                .orderBy(PullRequestModelTable.PULL_REQUEST_MODEL.UPDATED_AT.desc())
                .queryDeep()
                .observe()
                .runQuery();
    }

    public static Observable<PullRequestModel> getPullRequest(long id) {
        return Select.from(PullRequestModelTable.PULL_REQUEST_MODEL)
                .where(PullRequestModelTable.PULL_REQUEST_MODEL.ID.is(id))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    public static Observable<PullRequestModel> getPullRequest(int number, @NonNull String repoId, @NonNull String login) {
        return Select.from(PullRequestModelTable.PULL_REQUEST_MODEL)
                .where(PullRequestModelTable.PULL_REQUEST_MODEL.NUMBER.is(number)
                        .and(PullRequestModelTable.PULL_REQUEST_MODEL.LOGIN.is(login))
                        .and(PullRequestModelTable.PULL_REQUEST_MODEL.REPO_ID.is(repoId)))
                .queryDeep()
                .takeFirst()
                .observe()
                .runQuery();
    }

    @NonNull public static SpannableBuilder getMergeBy(@NonNull PullRequestModel pullRequest, @NonNull Context context) {
        boolean isMerge = pullRequest.isMerged();
        UserModel merger = (isMerge && pullRequest.getMergedBy() != null) ? pullRequest.getMergedBy() : pullRequest.getUser();
        String status = !isMerge ? context.getString(pullRequest.getState().getStatus()) : context.getString(R.string.merged);
        SpannableBuilder builder = SpannableBuilder.builder();
        builder.append(merger.getLogin())
                .append(" ")
                .append(status)
                .append(" ");
        if (isMerge) {
            builder.append(ParseDateFormat.getTimeAgo(pullRequest.getMergedAt()));
        } else {
            builder.append(ParseDateFormat.getTimeAgo(
                    pullRequest.getState() == IssueState.closed
                    ? pullRequest.getClosedAt() : pullRequest.getCreatedAt()));
        }
        return builder;
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.body);
        dest.writeString(this.title);
        dest.writeLong(this.id);
        dest.writeInt(this.comments);
        dest.writeInt(this.number);
        dest.writeByte(this.locked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mergable ? (byte) 1 : (byte) 0);
        dest.writeByte(this.merged ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mergeable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.commits);
        dest.writeInt(this.additions);
        dest.writeInt(this.deletions);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeList(this.labels);
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.base, flags);
        dest.writeParcelable(this.head, flags);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.htmlUrl);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeLong(this.closedAt != null ? this.closedAt.getTime() : -1);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeParcelable(this.closedBy, flags);
        dest.writeInt(this.changedFiles);
        dest.writeString(this.diffUrl);
        dest.writeString(this.patchUrl);
        dest.writeString(this.mergeCommitSha);
        dest.writeLong(this.mergedAt != null ? this.mergedAt.getTime() : -1);
        dest.writeParcelable(this.mergedBy, flags);
        dest.writeString(this.mergeState);
        dest.writeInt(this.reviewComments);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
    }

    protected PullRequestModel(Parcel in) {
        this.url = in.readString();
        this.body = in.readString();
        this.title = in.readString();
        this.id = in.readLong();
        this.comments = in.readInt();
        this.number = in.readInt();
        this.locked = in.readByte() != 0;
        this.mergable = in.readByte() != 0;
        this.merged = in.readByte() != 0;
        this.mergeable = in.readByte() != 0;
        this.commits = in.readInt();
        this.additions = in.readInt();
        this.deletions = in.readInt();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
        this.user = in.readParcelable(UserModel.class.getClassLoader());
        this.assignee = in.readParcelable(UserModel.class.getClassLoader());
        this.labels = new LabelListModel();
        in.readList(this.labels, this.labels.getClass().getClassLoader());
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.base = in.readParcelable(CommitModel.class.getClassLoader());
        this.head = in.readParcelable(CommitModel.class.getClassLoader());
        this.bodyHtml = in.readString();
        this.htmlUrl = in.readString();
        this.pullRequest = in.readParcelable(PullRequestModel.class.getClassLoader());
        long tmpClosedAt = in.readLong();
        this.closedAt = tmpClosedAt == -1 ? null : new Date(tmpClosedAt);
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.closedBy = in.readParcelable(UserModel.class.getClassLoader());
        this.changedFiles = in.readInt();
        this.diffUrl = in.readString();
        this.patchUrl = in.readString();
        this.mergeCommitSha = in.readString();
        long tmpMergedAt = in.readLong();
        this.mergedAt = tmpMergedAt == -1 ? null : new Date(tmpMergedAt);
        this.mergedBy = in.readParcelable(UserModel.class.getClassLoader());
        this.mergeState = in.readString();
        this.reviewComments = in.readInt();
        this.repoId = in.readString();
        this.login = in.readString();
    }

    public static final Creator<PullRequestModel> CREATOR = new Creator<PullRequestModel>() {
        @Override public PullRequestModel createFromParcel(Parcel source) {return new PullRequestModel(source);}

        @Override public PullRequestModel[] newArray(int size) {return new PullRequestModel[size];}
    };
}
