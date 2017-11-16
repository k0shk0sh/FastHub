package com.fastaccess.ui.adapter.viewholder;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.GitCommitModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.TeamsModel;
import com.fastaccess.data.dao.WikiModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Event;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.PullRequest;
import com.fastaccess.data.dao.model.Release;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class FeedsViewHolder extends BaseViewHolder<Event> {

    @Nullable @BindView(R.id.avatarLayout) AvatarLayout avatar;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    private Resources resources;

    public FeedsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        this.resources = itemView.getResources();
    }

    public static View getView(@NonNull ViewGroup viewGroup, boolean noImage) {
        if (noImage) {
            return getView(viewGroup, R.layout.feeds_row_no_image_item);
        } else {
            return getView(viewGroup, R.layout.feeds_row_item);
        }
    }

    @Override public void bind(@NonNull Event eventsModel) {
        appendAvatar(eventsModel);
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        appendActor(eventsModel, spannableBuilder);
        description.setMaxLines(2);
        description.setText("");
        description.setVisibility(View.GONE);
        if (eventsModel.getType() != null) {
            EventsType type = eventsModel.getType();
            if (type == EventsType.WatchEvent) {
                appendWatch(spannableBuilder, type, eventsModel);
            } else if (type == EventsType.CreateEvent) {
                appendCreateEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.CommitCommentEvent) {
                appendCommitComment(spannableBuilder, eventsModel);
            } else if (type == EventsType.DownloadEvent) {
                appendDownloadEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.FollowEvent) {
                appendFollowEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.ForkEvent) {
                appendForkEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.GistEvent) {
                appendGistEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.GollumEvent) {
                appendGollumEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.IssueCommentEvent) {
                appendIssueCommentEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.IssuesEvent) {
                appendIssueEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.MemberEvent) {
                appendMemberEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.PublicEvent) {
                appendPublicEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.PullRequestEvent) {
                appendPullRequestEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.PullRequestReviewCommentEvent) {
                appendPullRequestReviewCommentEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.PullRequestReviewEvent) {
                appendPullRequestReviewCommentEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.RepositoryEvent) {
                appendPublicEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.PushEvent) {
                appendPushEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.TeamAddEvent) {
                appendTeamEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.DeleteEvent) {
                appendDeleteEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.ReleaseEvent) {
                appendReleaseEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.ForkApplyEvent) {
                appendForkApplyEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.OrgBlockEvent) {
                appendOrgBlockEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.ProjectCardEvent) {
                appendProjectCardEvent(spannableBuilder, eventsModel, false);
            } else if (type == EventsType.ProjectColumnEvent) {
                appendProjectCardEvent(spannableBuilder, eventsModel, true);
            } else if (type == EventsType.OrganizationEvent) {
                appendOrganizationEvent(spannableBuilder, eventsModel);
            } else if (type == EventsType.ProjectEvent) {
                appendProjectCardEvent(spannableBuilder, eventsModel, false);
            }
            date.setGravity(Gravity.CENTER);
            date.setEventsIcon(type.getDrawableRes());
        }
        title.setText(spannableBuilder);
        date.setText(ParseDateFormat.getTimeAgo(eventsModel.getCreatedAt()));
    }

    private void appendOrganizationEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold(eventsModel.getPayload().getAction().replaceAll("_", ""))
                .append(" ")
                .append(eventsModel.getPayload().getInvitation() != null ? eventsModel.getPayload().getInvitation().getLogin() + " " : "")
                .append(eventsModel.getPayload().getOrganization().getLogin());
    }

    private void appendProjectCardEvent(SpannableBuilder spannableBuilder, Event eventsModel, boolean isColumn) {
        spannableBuilder.bold(eventsModel.getPayload().getAction())
                .append(" ")
                .append(!isColumn ? "project" : "column")
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendOrgBlockEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold(eventsModel.getPayload().getAction())
                .append(" ")
                .append(eventsModel.getPayload().getBlockedUser().getLogin())
                .append(" ")
                .append(eventsModel.getPayload().getOrganization().getLogin());
    }

    private void appendForkApplyEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold(eventsModel.getPayload().getHead())
                .append(" ")
                .append(eventsModel.getPayload().getBefore())
                .append(" ")
                .append(eventsModel.getRepo() != null ? "in " + eventsModel.getRepo().getName() : "");
    }

    private void appendReleaseEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        Release release = eventsModel.getPayload().getRelease();
        spannableBuilder.bold("released")
                .append(" ")
                .append(release.getName())
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendDeleteEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold("deleted")
                .append(" ")
                .append(eventsModel.getPayload().getRefType())
                .append(" ")
                .append(eventsModel.getPayload().getRef())
                .append(" ")
                .bold("at")
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendTeamEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        TeamsModel teamsModel = eventsModel.getPayload().getTeam();
        User user = eventsModel.getPayload().getUser();
        spannableBuilder.bold("added")
                .append(" ")
                .append(user != null ? user.getLogin() : eventsModel.getRepo().getName())
                .append(" ")
                .bold("in")
                .append(" ")
                .append(teamsModel.getName() != null ? teamsModel.getName() : teamsModel.getSlug());
    }

    private void appendPushEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        String ref = eventsModel.getPayload().getRef();
        if (ref.startsWith("refs/heads/")) {
            ref = ref.substring(11);
        }
        spannableBuilder.bold("pushed to")
                .append(" ")
                .append(ref)
                .append(" ")
                .bold("at")
                .append(" ")
                .append(eventsModel.getRepo().getName());
        final List<GitCommitModel> commits = eventsModel.getPayload().getCommits();
        int size = commits != null ? commits.size() : -1;
        SpannableBuilder spanCommits = SpannableBuilder.builder();
        if (size > 0) {
            if (size != 1) spanCommits.append(String.valueOf(eventsModel.getPayload().getSize())).append(" new commits").append("\n");
            else spanCommits.append("1 new commit").append("\n");
            int max = 5;
            int appended = 0;
            for (GitCommitModel commit : commits) {
                if (commit == null) continue;
                String sha = commit.getSha();
                if (TextUtils.isEmpty(sha)) continue;
                sha = sha.length() > 7 ? sha.substring(0, 7) : sha;
                spanCommits.url(sha).append(" ")
                        .append(commit.getMessage() != null ? commit.getMessage().replaceAll("\\r?\\n|\\r", " ") : "")
                        .append("\n");
                appended++;
                if (appended == max) break;
            }
        }
        if (spanCommits.length() > 0) {
            int last = spanCommits.length();
            description.setMaxLines(5);
            description.setText(spanCommits.delete(last - 1, last));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText("");
            description.setMaxLines(2);
            description.setVisibility(View.GONE);
        }
    }

    private void appendPullRequestReviewCommentEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        PullRequest pullRequest = eventsModel.getPayload().getPullRequest();
        Comment comment = eventsModel.getPayload().getComment();
        spannableBuilder.bold("reviewed")
                .append(" ")
                .bold("pull request")
                .append(" ")
                .bold("in")
                .append(" ")
                .append(eventsModel.getRepo().getName())
                .bold("#")
                .bold(String.valueOf(pullRequest.getNumber()));
        if (comment.getBody() != null) {
            MarkDownProvider.stripMdText(description, comment.getBody().replaceAll("\\r?\\n|\\r", " "));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
        }
    }

    private void appendPullRequestEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        PullRequest issue = eventsModel.getPayload().getPullRequest();
        String action = eventsModel.getPayload().getAction();
        if ("synchronize".equals(action)) {
            action = "updated";
        }
        if (eventsModel.getPayload().getPullRequest().isMerged()) {
            action = "merged";
        }
        spannableBuilder.bold(action)
                .append(" ")
                .bold("pull request")
                .append(" ")
                .append(eventsModel.getRepo().getName())
                .bold("#")
                .bold(String.valueOf(issue.getNumber()));
        if ("opened".equals(action) || "closed".equals(action)) {
            if (issue.getTitle() != null) {
                MarkDownProvider.stripMdText(description, issue.getTitle().replaceAll("\\r?\\n|\\r", " "));
                description.setVisibility(View.VISIBLE);
            } else {
                description.setText("");
                description.setVisibility(View.GONE);
            }
        }
    }

    private void appendPublicEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        String action = "public";
        if (eventsModel.getPayload() != null && "privatized".equalsIgnoreCase(eventsModel.getPayload().getAction())) {
            action = "private";
        }
        spannableBuilder.append("made")
                .append(" ")
                .append(eventsModel.getRepo().getName())
                .append(" ")
                .append(action);
    }

    private void appendMemberEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        User user = eventsModel.getPayload().getMember();
        spannableBuilder.bold("added")
                .append(" ")
                .append(user != null ? user.getLogin() + " " : "")
                .append("as a collaborator")
                .append(" ")
                .append("to")
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendIssueEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        Issue issue = eventsModel.getPayload().getIssue();
        boolean isLabel = "label".equals(eventsModel.getPayload().getAction());
        LabelModel label = isLabel ? issue.getLabels() != null && !issue.getLabels().isEmpty()
                                     ? issue.getLabels().get(issue.getLabels().size() - 1) : null : null;
        spannableBuilder.bold(isLabel && label != null ? ("Labeled " + label.getName()) : eventsModel.getPayload().getAction())
                .append(" ")
                .bold("issue")
                .append(" ")
                .append(eventsModel.getRepo().getName())
                .bold("#")
                .bold(String.valueOf(issue.getNumber()));
        if (issue.getTitle() != null) {
            MarkDownProvider.stripMdText(description, issue.getTitle().replaceAll("\\r?\\n|\\r", " "));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
        }
    }

    private void appendIssueCommentEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        Comment comment = eventsModel.getPayload().getComment();
        Issue issue = eventsModel.getPayload().getIssue();
        spannableBuilder.bold("commented")
                .append(" ")
                .bold("on")
                .append(" ")
                .bold(issue.getPullRequest() != null ? "pull request" : "issue")
                .append(" ")
                .append(eventsModel.getRepo().getName())
                .bold("#")
                .bold(String.valueOf(issue.getNumber()));
        if (comment.getBody() != null) {
            MarkDownProvider.stripMdText(description, comment.getBody().replaceAll("\\r?\\n|\\r", " "));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
        }
    }

    private void appendGollumEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        List<WikiModel> wiki = eventsModel.getPayload().getPages();
        if (wiki != null && !wiki.isEmpty()) {
            for (WikiModel wikiModel : wiki) {
                spannableBuilder.bold(wikiModel.getAction())
                        .append(" ")
                        .append(wikiModel.getPageName())
                        .append(" ");
            }
        } else {
            spannableBuilder.bold(resources.getString(R.string.gollum))
                    .append(" ");
        }
        spannableBuilder
                .append(eventsModel.getRepo().getName());

    }

    private void appendGistEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        String action = eventsModel.getPayload().getAction();
        action = "create".equals(action) ? "created" : "update".equals(action) ? "updated" : action;
        spannableBuilder.bold(action)
                .append(" ")
                .append(itemView.getResources().getString(R.string.gist))
                .append(" ")
                .append(eventsModel.getPayload().getGist().getGistId());
    }

    private void appendForkEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold("forked")
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendFollowEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold("started following")
                .append(" ")
                .bold(eventsModel.getPayload().getTarget().getLogin());
    }

    private void appendDownloadEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        spannableBuilder.bold("uploaded a file")
                .append(" ")
                .append(eventsModel.getPayload().getDownload() != null ? eventsModel.getPayload().getDownload().getName() : "")
                .append(" ")
                .append("to")
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendCreateEvent(SpannableBuilder spannableBuilder, Event eventsModel) {
        PayloadModel payloadModel = eventsModel.getPayload();
        String refType = payloadModel.getRefType();
        spannableBuilder
                .bold("created")
                .append(" ")
                .append(refType)
                .append(" ")
                .append(!"repository".equalsIgnoreCase(refType) ? payloadModel.getRef() + " " : "")
                .bold("at")
                .append(" ")
                .append(eventsModel.getRepo().getName());
        if (payloadModel.getDescription() != null) {
            MarkDownProvider.stripMdText(description, payloadModel.getDescription().replaceAll("\\r?\\n|\\r", " "));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
        }
    }

    private void appendWatch(SpannableBuilder spannableBuilder, EventsType type, Event eventsModel) {
        spannableBuilder.bold(resources.getString(type.getType()).toLowerCase())
                .append(" ")
                .append(eventsModel.getRepo().getName());
    }

    private void appendCommitComment(SpannableBuilder spannableBuilder, Event eventsModel) {
        Comment comment = eventsModel.getPayload().getCommitComment() == null ? eventsModel.getPayload().getComment() : eventsModel.getPayload()
                .getCommitComment();
        String commitId = comment != null && comment.getCommitId() != null && comment.getCommitId().length() > 10 ?
                          comment.getCommitId().substring(0, 10) : null;
        spannableBuilder.bold("commented")
                .append(" ")
                .bold("on")
                .append(" ")
                .bold("commit")
                .append(" ")
                .append(eventsModel.getRepo().getName())
                .url(commitId != null ? "@" + commitId : "");
        if (comment != null && comment.getBody() != null) {
            MarkDownProvider.stripMdText(description, comment.getBody().replaceAll("\\r?\\n|\\r", " "));
            description.setVisibility(View.VISIBLE);
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
        }
    }

    private void appendActor(@NonNull Event eventsModel, SpannableBuilder spannableBuilder) {
        if (eventsModel.getActor() != null) {
            spannableBuilder.append(eventsModel.getActor().getLogin()).append(" ");
        }
    }

    private void appendAvatar(@NonNull Event eventsModel) {
        if (avatar != null) {
            if (eventsModel.getActor() != null) {
                avatar.setUrl(eventsModel.getActor().getAvatarUrl(), eventsModel.getActor().getLogin(),
                        eventsModel.getActor().isOrganizationType(),
                        LinkParserHelper.isEnterprise(eventsModel.getActor().getHtmlUrl()));
            } else {
                avatar.setUrl(null, null, false, false);
            }
        }
    }
}