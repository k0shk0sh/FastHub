package com.fastaccess.ui.adapter.viewholder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.PayloadModel;
import com.fastaccess.data.dao.model.Event;
import com.fastaccess.data.dao.types.EventsType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class FeedsViewHolder extends BaseViewHolder<Event> {

    @BindView(R.id.avatarLayout) AvatarLayout avatar;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    @BindString(R.string.to) String to;
    @BindString(R.string.in_value) String in;

    public FeedsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static View getView(@NonNull ViewGroup viewGroup) {
        return getView(viewGroup, R.layout.feeds_row_item);
    }

    @Override public void bind(@NonNull Event eventsModel) {
        if (eventsModel.getActor() != null) {
            avatar.setUrl(eventsModel.getActor().getAvatarUrl(), eventsModel.getActor().getLogin(), eventsModel.getActor().isOrganizationType());
        } else {
            avatar.setUrl(null, null);
        }
        SpannableBuilder spannableBuilder = SpannableBuilder.builder();
        spannableBuilder.append(eventsModel.getActor() != null ? eventsModel.getActor().getLogin() : "N/A").append(" ");
        String number = "";
        if (eventsModel.getType() != null) {
            EventsType type = eventsModel.getType();
            date.setGravity(Gravity.CENTER);
            date.setEventsIcon(type.getDrawableRes());
            String action;
            if (type == EventsType.WatchEvent) {
                action = itemView.getResources().getString(type.getType()).toLowerCase();
            } else if (type == EventsType.PullRequestEvent) {
                if (eventsModel.getPayload().getPullRequest() != null) {
                    if (eventsModel.getPayload().getPullRequest().isMerged()) {
                        action = itemView.getResources().getString(R.string.merged);
                    } else {
                        action = eventsModel.getPayload() != null ? eventsModel.getPayload().getAction() : "";
                    }
                } else {
                    action = eventsModel.getPayload() != null ? eventsModel.getPayload().getAction() : "";
                }
            } else {
                action = eventsModel.getPayload() != null ? eventsModel.getPayload().getAction() : "";
            }
            spannableBuilder.bold(action != null ? action.toLowerCase() : "")
                    .append(eventsModel.getPayload() != null && eventsModel.getPayload().getAction() != null ? " " : "");
            if (type != EventsType.WatchEvent) {
                if (type == EventsType.CreateEvent && !InputHelper.isEmpty(eventsModel.getPayload().getRefType())) {
                    spannableBuilder
                            .bold(itemView.getResources().getString(type.getType()).toLowerCase())
                            .append(" ")
                            .bold(eventsModel.getPayload().getRefType())
                            .append(" ")
                            .append(in)
                            .append(" ");
                } else if ((type == EventsType.PushEvent || type == EventsType.DeleteEvent) && eventsModel.getPayload() != null) {
                    spannableBuilder
                            .bold(itemView.getResources().getString(type.getType()).toLowerCase())
                            .append(" ")
                            .bold(Uri.parse(eventsModel.getPayload().getRef()).getLastPathSegment())
                            .append(" ")
                            .append(in)
                            .append(" ");
                } else {
                    if (eventsModel.getPayload() != null) {
                        PayloadModel payloadModel = eventsModel.getPayload();
                        if (payloadModel.getTarget() != null) {
                            spannableBuilder
                                    .bold(payloadModel.getTarget().getLogin())
                                    .append(" ")
                                    .append(in)
                                    .append(" ");
                        } else if (payloadModel.getTeam() != null) {
                            spannableBuilder
                                    .bold(payloadModel.getTeam().getName())
                                    .append(" ")
                                    .append(in)
                                    .append(" ");
                        } else if (payloadModel.getMember() != null) {
                            spannableBuilder
                                    .bold(payloadModel.getMember().getLogin())
                                    .append(" ")
                                    .append(in)
                                    .append(" ");
                        } else {
                            spannableBuilder.bold(itemView.getResources().getString(type.getType()).toLowerCase()).append(" ");
                        }
                    } else {
                        spannableBuilder.bold(itemView.getResources().getString(type.getType()).toLowerCase()).append(" ");
                    }
                }
            }
        }
        if (eventsModel.getPayload() != null) {
            if (eventsModel.getPayload().getComment() != null) {
                MarkDownProvider.stripMdText(description, eventsModel.getPayload().getComment().getBody());
                description.setVisibility(View.VISIBLE);
                if (eventsModel.getPayload().getIssue() != null) {
                    number = "#" + eventsModel.getPayload().getIssue().getNumber();
                } else if (eventsModel.getPayload().getPullRequest() != null) {
                    number = "#" + eventsModel.getPayload().getPullRequest().getNumber();
                }
            } else if (eventsModel.getPayload().getIssue() != null) {
                number = "#" + eventsModel.getPayload().getIssue().getNumber();
                description.setText(eventsModel.getPayload().getIssue().getTitle());
                description.setVisibility(View.VISIBLE);
            } else if (eventsModel.getPayload().getPullRequest() != null) {
                number = "#" + eventsModel.getPayload().getPullRequest().getNumber();
                description.setText(eventsModel.getPayload().getPullRequest().getTitle());
                description.setVisibility(View.VISIBLE);
            } else {
                description.setText("");
                description.setVisibility(View.GONE);
            }
        } else {
            description.setText("");
            description.setVisibility(View.GONE);
        }
        spannableBuilder.append(eventsModel.getRepo() != null ? eventsModel.getRepo().getName() : "")
                .append(number);
        title.setText(spannableBuilder);
        date.setText(ParseDateFormat.getTimeAgo(eventsModel.getCreatedAt()));
    }
}
