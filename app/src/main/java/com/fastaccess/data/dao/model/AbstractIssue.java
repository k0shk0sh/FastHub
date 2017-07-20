package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.LabelListModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.ReactionsModel;
import com.fastaccess.data.dao.UsersListModel;
import com.fastaccess.data.dao.converters.LabelsListConverter;
import com.fastaccess.data.dao.converters.MilestoneConverter;
import com.fastaccess.data.dao.converters.PullRequestConverter;
import com.fastaccess.data.dao.converters.ReactionsConverter;
import com.fastaccess.data.dao.converters.RepoConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.data.dao.converters.UsersConverter;
import com.fastaccess.data.dao.types.IssueState;
import com.fastaccess.helper.RxHelper;

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

import static com.fastaccess.data.dao.model.Issue.ID;
import static com.fastaccess.data.dao.model.Issue.LOGIN;
import static com.fastaccess.data.dao.model.Issue.NUMBER;
import static com.fastaccess.data.dao.model.Issue.REPO_ID;
import static com.fastaccess.data.dao.model.Issue.STATE;
import static com.fastaccess.data.dao.model.Issue.UPDATED_AT;

/**
 * Created by Kosh on 16 Mar 2017, 7:34 PM
 */
@Entity @NoArgsConstructor public abstract class AbstractIssue implements Parcelable {
    @Key long id;
    String url;
    String body;
    String title;
    int comments;
    int number;
    boolean locked;
    IssueState state;
    String repoUrl;
    String bodyHtml;
    String htmlUrl;
    Date closedAt;
    Date createdAt;
    Date updatedAt;
    String repoId;
    String login;
    @Column(name = "user_column") @Convert(UserConverter.class) User user;
    @Convert(UserConverter.class) User assignee;
    @Convert(UsersConverter.class) UsersListModel assignees;
    @Convert(LabelsListConverter.class) LabelListModel labels;
    @Convert(MilestoneConverter.class) MilestoneModel milestone;
    @Convert(RepoConverter.class) Repo repository;
    @Convert(PullRequestConverter.class) PullRequest pullRequest;
    @Convert(UserConverter.class) User closedBy;
    @Convert(ReactionsConverter.class) ReactionsModel reactions;

    public Single<Issue> save(Issue entity) {
        return RxHelper.getSingle(App.getInstance().getDataStore()
                .delete(Issue.class)
                .where(Issue.ID.eq(entity.getId()))
                .get()
                .single()
                .flatMap(observer -> App.getInstance().getDataStore().insert(entity)));
    }

    public static Disposable save(@NonNull List<Issue> models, @NonNull String repoId, @NonNull String login) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(Issue.class)
                        .where(REPO_ID.equal(repoId).and(LOGIN.equal(login)))
                        .get()
                        .value();
                if (!models.isEmpty()) {
                    for (Issue issueModel : models) {
                        dataSource.delete(Issue.class).where(Issue.ID.eq(issueModel.getId())).get().value();
                        issueModel.setRepoId(repoId);
                        issueModel.setLogin(login);
                        dataSource.insert(issueModel);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Single<List<Issue>> getIssues(@NonNull String repoId, @NonNull String login, @NonNull IssueState issueState) {
        return App.getInstance().getDataStore().select(Issue.class)
                .where(REPO_ID.equal(repoId)
                        .and(LOGIN.equal(login))
                        .and(STATE.equal(issueState)))
                .orderBy(UPDATED_AT.desc())
                .get()
                .observable()
                .toList();
    }

    public static Observable<Issue> getIssue(long id) {
        return App.getInstance().getDataStore()
                .select(Issue.class)
                .where(ID.equal(id))
                .get()
                .observable();
    }

    public static Observable<Issue> getIssueByNumber(int number, String repoId, String login) {
        return App.getInstance().getDataStore()
                .select(Issue.class)
                .where(NUMBER.equal(number)
                        .and(REPO_ID.eq(repoId))
                        .and(LOGIN.eq(login)))
                .get()
                .observable();
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
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeString(this.repoUrl);
        dest.writeString(this.bodyHtml);
        dest.writeString(this.htmlUrl);
        dest.writeLong(this.closedAt != null ? this.closedAt.getTime() : -1);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeList(this.assignees);
        dest.writeList(this.labels);
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.repository, flags);
        dest.writeParcelable(this.pullRequest, flags);
        dest.writeParcelable(this.closedBy, flags);
        dest.writeParcelable(this.reactions, flags);
    }

    protected AbstractIssue(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        this.body = in.readString();
        this.title = in.readString();
        this.comments = in.readInt();
        this.number = in.readInt();
        this.locked = in.readByte() != 0;
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
        this.repoUrl = in.readString();
        this.bodyHtml = in.readString();
        this.htmlUrl = in.readString();
        long tmpClosedAt = in.readLong();
        this.closedAt = tmpClosedAt == -1 ? null : new Date(tmpClosedAt);
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
        this.repoId = in.readString();
        this.login = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.assignee = in.readParcelable(User.class.getClassLoader());
        this.assignees = new UsersListModel();
        in.readList(this.assignees, this.assignees.getClass().getClassLoader());
        this.labels = new LabelListModel();
        in.readList(this.labels, this.labels.getClass().getClassLoader());
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.repository = in.readParcelable(Repo.class.getClassLoader());
        this.pullRequest = in.readParcelable(PullRequest.class.getClassLoader());
        this.closedBy = in.readParcelable(User.class.getClassLoader());
        this.reactions = in.readParcelable(ReactionsModel.class.getClassLoader());
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override public Issue createFromParcel(Parcel source) {return new Issue(source);}

        @Override public Issue[] newArray(int size) {return new Issue[size];}
    };

}
