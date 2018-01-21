package com.fastaccess.data.dao.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fastaccess.App;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.RenameModel;
import com.fastaccess.data.dao.TeamsModel;
import com.fastaccess.data.dao.converters.IssueConverter;
import com.fastaccess.data.dao.converters.LabelConverter;
import com.fastaccess.data.dao.converters.MilestoneConverter;
import com.fastaccess.data.dao.converters.RenameConverter;
import com.fastaccess.data.dao.converters.TeamConverter;
import com.fastaccess.data.dao.converters.UserConverter;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.RxHelper;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.requery.BlockingEntityStore;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.Transient;
import lombok.NoArgsConstructor;

import static com.fastaccess.data.dao.model.IssueEvent.CREATED_AT;
import static com.fastaccess.data.dao.model.IssueEvent.ISSUE_ID;
import static com.fastaccess.data.dao.model.IssueEvent.LOGIN;
import static com.fastaccess.data.dao.model.IssueEvent.REPO_ID;

/**
 * Created by Kosh on 16 Mar 2017, 7:33 PM
 */

@Entity @NoArgsConstructor public abstract class AbstractIssueEvent implements Parcelable {

    @Key long id;
    String url;
    IssueEventType event;
    @Convert(UserConverter.class) User actor;
    @Convert(UserConverter.class) User assigner;
    @Convert(UserConverter.class) User assignee;
    @Convert(UserConverter.class) User requestedReviewer;
    @Convert(TeamConverter.class) TeamsModel requestedTeam;
    @Convert(MilestoneConverter.class) MilestoneModel milestone;
    @Convert(RenameConverter.class) RenameModel rename;
    @Convert(IssueConverter.class) Issue source;
    @Convert(LabelConverter.class) LabelModel label;
    String commitId;
    String commitUrl;
    Date createdAt;
    String issueId;
    String repoId;
    String login;
    @Transient List<LabelModel> labels;
    @Transient Issue issue;

    public static Disposable save(@NonNull List<IssueEvent> models, @NonNull String repoId,
                                  @NonNull String login, @NonNull String issueId) {
        return RxHelper.getSingle(Single.fromPublisher(s -> {
            try {
                BlockingEntityStore<Persistable> dataSource = App.getInstance().getDataStore().toBlocking();
                dataSource.delete(IssueEvent.class)
                        .where(LOGIN.equal(login)
                                .and(REPO_ID.equal(repoId))
                                .and(ISSUE_ID.equal(issueId)))
                        .get()
                        .value();
                if (!models.isEmpty()) {
                    for (IssueEvent issueEventModel : models) {
                        dataSource.delete(IssueEvent.class).where(IssueEvent.ID.eq(issueEventModel.getId())).get().value();
                        issueEventModel.setIssueId(issueId);
                        issueEventModel.setLogin(login);
                        issueEventModel.setRepoId(repoId);
                        dataSource.insert(issueEventModel);
                    }
                }
                s.onNext("");
            } catch (Exception e) {
                s.onError(e);
            }
            s.onComplete();
        })).subscribe(o -> {/*donothing*/}, Throwable::printStackTrace);
    }

    public static Single<List<IssueEvent>> get(@NonNull String repoId, @NonNull String login,
                                               @NonNull String issueId) {
        return App.getInstance().getDataStore()
                .select(IssueEvent.class)
                .where(LOGIN.equal(login)
                        .and(REPO_ID.equal(repoId))
                        .and(ISSUE_ID.equal(issueId)))
                .orderBy(CREATED_AT.desc())
                .get()
                .observable()
                .toList();
    }

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.url);
        dest.writeInt(this.event == null ? -1 : this.event.ordinal());
        dest.writeParcelable(this.actor, flags);
        dest.writeParcelable(this.assigner, flags);
        dest.writeParcelable(this.assignee, flags);
        dest.writeParcelable(this.requestedReviewer, flags);
        dest.writeParcelable(this.requestedTeam, flags);
        dest.writeParcelable(this.milestone, flags);
        dest.writeParcelable(this.rename, flags);
        dest.writeParcelable(this.source, flags);
        dest.writeParcelable(this.label, flags);
        dest.writeString(this.commitId);
        dest.writeString(this.commitUrl);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeString(this.issueId);
        dest.writeString(this.repoId);
        dest.writeString(this.login);
        dest.writeTypedList(this.labels);
        dest.writeParcelable(this.issue, flags);
    }

    protected AbstractIssueEvent(Parcel in) {
        this.id = in.readLong();
        this.url = in.readString();
        int tmpEvent = in.readInt();
        this.event = tmpEvent == -1 ? null : IssueEventType.values()[tmpEvent];
        this.actor = in.readParcelable(User.class.getClassLoader());
        this.assigner = in.readParcelable(User.class.getClassLoader());
        this.assignee = in.readParcelable(User.class.getClassLoader());
        this.requestedReviewer = in.readParcelable(User.class.getClassLoader());
        this.requestedTeam = in.readParcelable(TeamsModel.class.getClassLoader());
        this.milestone = in.readParcelable(MilestoneModel.class.getClassLoader());
        this.rename = in.readParcelable(RenameModel.class.getClassLoader());
        this.source = in.readParcelable(Issue.class.getClassLoader());
        this.label = in.readParcelable(LabelModel.class.getClassLoader());
        this.commitId = in.readString();
        this.commitUrl = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        this.issueId = in.readString();
        this.repoId = in.readString();
        this.login = in.readString();
        this.labels = in.createTypedArrayList(LabelModel.CREATOR);
        this.issue = in.readParcelable(Issue.class.getClassLoader());
    }

    public static final Creator<IssueEvent> CREATOR = new Creator<IssueEvent>() {
        @Override public IssueEvent createFromParcel(Parcel source) {return new IssueEvent(source);}

        @Override public IssueEvent[] newArray(int size) {return new IssueEvent[size];}
    };
}
