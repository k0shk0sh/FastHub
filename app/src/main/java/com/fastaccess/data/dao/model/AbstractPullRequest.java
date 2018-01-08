package com.fastaccess.data.dao.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.PullsIssuesParser;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.converters.CommitConverter;
import com.fastaccess.data.dao.converters.LabelsListConverter;
import com.fastaccess.data.dao.converters.MilestoneConverter;
import com.fastaccess.data.dao.converters.PullRequestConverter;
import com.fastaccess.data.dao.converters.ReactionsConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.data.dao.converters.UsersConverter;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.ui.widgets.SpannableBuilder;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.PullRequest.ID;
import static com.fastaccess.data.dao.model.PullRequest.LOGIN;
import static com.fastaccess.data.dao.model.PullRequest.NUMBER;
import static com.fastaccess.data.dao.model.PullRequest.REPO_ID;
import static com.fastaccess.data.dao.model.PullRequest.STATE;
import static com.fastaccess.data.dao.model.PullRequest.UPDATED_AT;

/**
 * Created by Kosh on 16 Mar 2017, 7:39 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractPullRequest implements Parcelable {
    @Key long id;
    String url;
    String body;
    String title;
    int comments;
    int number;
    boolean locked;
    boolean mergable;
    boolean merged;
    boolean mergeable;
    int commits;
    int additions;
    int deletions;
    IssueState state;
    String bodyHtml;
    String htmlUrl;
    Date closedAt;
    Date createdAt;
    Date updatedAt;
    int changedFiles;
    String diffUrl;
    String patchUrl;
    String mergeCommitSha;
    Date mergedAt;
    String mergeState;
    int reviewComments;
    String repoId;
    String login;
    String mergeableState;
    @Convert(UsersConverter.class) UsersListModel assignees;
    @Convert(UserConverter.class) User mergedBy;
    @Convert(UserConverter.class) User closedBy;
    @Column(name = "user_column") @Convert(UserConverter.class) User user;
    @Convert(UserConverter.class) User assignee;
    @Convert(LabelsListConverter.class) LabelListModel labels;
    @Convert(MilestoneConverter.class) MilestoneModel milestone;
    @Convert(CommitConverter.class) Commit base;
    @Convert(CommitConverter.class) Commit head;
    @Convert(PullRequestConverter.class) PullRequest pullRequest;
    @Convert(ReactionsConverter.class) ReactionsModel reactions;

    public Single<PullRequest> save(PullRequest entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore()
                .delete(PullRequest.class)
                .where(PullRequest.ID.eq(entity.getId()))
                .get()
                .single()
                .flatMap(observer -> App.getInstance().getDataStore().insert(entity)));
    }

    public static Disposable save(@NonNull List<PullRequest> models, @NonNull String repoId, @NonNull String login) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(PullRequest.class)
                        .where(REPO_ID.equal(repoId)
                                .and(LOGIN.equal(login)))
                        .get()
                        .value();
                if (!models.isEmpty()) {
                    for (PullRequest pullRequest : models) {
                        dataSource.delete(PullRequest.class).where(PullRequest.ID.eq(pullRequest.getId())).get().value();
                        pullRequest.setRepoId(repoId);
                        pullRequest.setLogin(login);
                        dataSource.insert(pullRequest);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Single<List<PullRequest>> getPullRequests(@NonNull String repoId, @NonNull String login,
                                                            @NonNull IssueState issueState) {
        return App.getInstance().getDataStore()
                .select(PullRequest.class)
                .where(REPO_ID.equal(repoId)
                        .and(LOGIN.equal(login))
                        .and(STATE.equal(issueState)))
                .orderBy(UPDATED_AT.desc())
                .get()
                .observable()
                .toList();
    }

    public static Observable<PullRequest> getPullRequestById(long id) {
        return App.getInstance().getDataStore()
                .select(PullRequest.class)
                .where(ID.eq(id))
                .get()
                .observable();
    }

    public static Observable<PullRequest> getPullRequestByNumber(int number, @NonNull String repoId, @NonNull String login) {
        return App.getInstance().getDataStore()
                .select(PullRequest.class)
                .where(REPO_ID.equal(repoId)
                        .and(LOGIN.equal(login))
                        .and(NUMBER.equal(number)))
                .get()
                .observable();
    }

    @NonNull public static SpannableBuilder getMergeBy(@NonNull PullRequest pullRequest, @NonNull Context context, boolean showRepoName) {
        boolean isMerge = pullRequest.isMerged() || !InputHelper.isEmpty(pullRequest.mergedAt);
        if (isMerge) {
            User merger = pullRequest.getMergedBy();
            SpannableBuilder builder = SpannableBuilder.builder();
            if (showRepoName) {
                PullsIssuesParser parser = PullsIssuesParser.getForPullRequest(pullRequest.getHtmlUrl());
                if (parser != null)
                    builder.bold(parser.getLogin())
                            .append("/")
                            .bold(parser.getRepoId())
                            .append(" ")
                            .bold("#").bold(String.valueOf(pullRequest.getNumber()))
                            .append(" ");
            } else {
                builder.bold("#" + pullRequest.getNumber())
                        .append(" ")
                        .append(merger != null ? merger.getLogin() + " " : "");
            }
            builder.append(context.getString(R.string.merged).toLowerCase())
                    .append(" ");
            if (pullRequest.getHead() != null) {
                builder.bold(pullRequest.getHead().getRef())
                        .append(" ")
                        .append(context.getString(R.string.to))
                        .append(" ")
                        .bold(pullRequest.getBase().getRef())
                        .append(" ");
            }
            builder.append(ParseDateFormat.getTimeAgo(pullRequest.getMergedAt()));
            return builder;
        } else {
            User user = pullRequest.getUser();
            String status = context.getString(pullRequest.getState().getStatus());
            SpannableBuilder builder = SpannableBuilder.builder();
            if (showRepoName) {
                PullsIssuesParser parser = PullsIssuesParser.getForPullRequest(pullRequest.getHtmlUrl());
                if (parser != null) {
                    builder.bold(parser.getLogin())
                            .append("/")
                            .bold(parser.getRepoId())
                            .append(" ")
                            .bold("#").bold(String.valueOf(pullRequest.getNumber()))
                            .append(" ");
                }
            } else {
                builder.bold("#" + pullRequest.getNumber())
                        .append(" ")
                        .append(user.getLogin())
                        .append(" ");
            }
            if (pullRequest.getState() == IssueState.open && pullRequest.getHead() != null && pullRequest.getBase() != null) {
                return builder
                        .append(context.getString(R.string.want_to_merge))
                        .append(" ")
                        .bold(pullRequest.getHead().getRef())
                        .append(" ")
                        .append(context.getString(R.string.to))
                        .append(" ")
                        .bold(pullRequest.getBase().getRef())
                        .append(" ")
                        .append(ParseDateFormat.getTimeAgo(pullRequest.getState() == IssueState.closed
                                                           ? pullRequest.getClosedAt() : pullRequest.getCreatedAt()));
            } else {
                return builder
                        .bold(status.toLowerCase())
                        .append(" ")
                        .bold(pullRequest.getHead() != null ? pullRequest.getHead().getRef() : "")
                        .append(" ")
                        .append(ParseDateFormat.getTimeAgo(pullRequest.getState() == IssueState.closed
                                                           ? pullRequest.getClosedAt() : pullRequest.getCreatedAt()));
            }
        }
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeString(this.body);
        dest.writeString(this.title);
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
        dest.writeString(this.bodyHtml);
        dest.writeString(this.htmlUrl);
        dest.writeLong(this.closedAt != null ? this.closedAt.getTime() : -1);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeInt(this.changedFiles);
        dest.writeString(this.diffUrl);
        dest.writeString(this.patchUrl);
        dest.writeString(this.mergeCommitSha);
        dest.writeLong(this.mergedAt != null ? this.mergedAt.getTime() : -1);
        dest.writeString(this.mergeState);
        dest.writeInt(this.reviewComments);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
        dest.writeString(this.mergeableState);
        dest.writeList(this.assignees);
        dest.writeParcelable(this.mergedBy, flags);
        dest.writeParcelable(this.closedBy, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeList(this.labels);
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.base, flags);
        dest.writeParcelable(this.head, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeParcelable(this.reactions, flags);
    }

    protected AbstractPullRequest(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.body = in.readString();
        this.title = in.readString();
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
        this.bodyHtml = in.readString();
        this.htmlUrl = in.readString();
        long tmpClosedAt = in.readLong();
        this.closedAt = tmpClosedAt == -1 ? null : new Date(tmpClosedAt);
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.changedFiles = in.readInt();
        this.diffUrl = in.readString();
        this.patchUrl = in.readString();
        this.mergeCommitSha = in.readString();
        long tmpMergedAt = in.readLong();
        this.mergedAt = tmpMergedAt == -1 ? null : new Date(tmpMergedAt);
        this.mergeState = in.readString();
        this.reviewComments = in.readInt();
        this.repoId = in.readString();
        this.login = in.readString();
        this.mergeableState = in.readString();
        this.assignees = new UsersListModel();
        in.readList(this.assignees, this.assignees.getClass().getClassLoader());
        this.mergedBy = in.readParcelable(User.class.getClassLoader());
        this.closedBy = in.readParcelable(User.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
        this.assignee = in.readParcelable(User.class.getClassLoader());
        this.labels = new LabelListModel();
        in.readList(this.labels, this.labels.getClass().getClassLoader());
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.base = in.readParcelable(Commit.class.getClassLoader());
        this.head = in.readParcelable(Commit.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
    }

    public static final Creator<PullRequest> CREATOR = new Creator<PullRequest>() {
        @Override public PullRequest createFromParcel(Parcel source) {return new PullRequest(source);}

        @Override public PullRequest[] newArray(int size) {return new PullRequest[size];}
    };
}
