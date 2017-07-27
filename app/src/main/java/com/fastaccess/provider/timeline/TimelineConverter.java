package com.fastaccess.provider.timeline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.ReviewCommentModel;
import com.fastaccess.data.dao.ReviewModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.timeline.GenericEvent;
import com.fastaccess.data.dao.types.IssueEventType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kosh on 26/07/2017.
 */

public class TimelineConverter {

    @NonNull public static Observable<TimelineModel> convert(@Nullable List<JsonObject> jsonObjects) {
        if (jsonObjects == null) return Observable.empty();
        Gson gson = RestProvider.gson;
        return Observable.fromIterable(jsonObjects)
                .map(jsonObject -> {
                    String event = jsonObject.get("event").getAsString();
                    TimelineModel timeline = new TimelineModel();
                    if (!InputHelper.isEmpty(event)) {
                        IssueEventType type = IssueEventType.getType(event);
                        timeline.setEvent(type);
                        if (type != null) {
                            if (type == IssueEventType.commented) {
                                timeline.setComment(getComment(jsonObject, gson));
                            } else if (type == IssueEventType.line_commented) {
                                timeline.setReviewComment(getReviewComment(jsonObject, gson));
                            } else if (type == IssueEventType.reviewed) {
                                timeline.setReview(getReview(jsonObject, gson));
                            } else {
                                timeline.setGenericEvent(getGenericEvent(jsonObject, gson));
                            }
                        }
                    } else {
                        timeline.setGenericEvent(getGenericEvent(jsonObject, gson));
                    }
                    return timeline;
                })
                .filter(timeline -> timeline != null && filterEvents(timeline.getEvent()));
    }

    private static ReviewModel getReview(@NonNull JsonObject object, @NonNull Gson gson) {
        return gson.fromJson(object, ReviewModel.class);
    }

    private static GenericEvent getGenericEvent(@NonNull JsonObject object, @NonNull Gson gson) {
        return gson.fromJson(object, GenericEvent.class);
    }

    private static ReviewCommentModel getReviewComment(@NonNull JsonObject object, @NonNull Gson gson) {
        return gson.fromJson(object, ReviewCommentModel.class);
    }

    private static Comment getComment(@NonNull JsonObject object, @NonNull Gson gson) {
        return gson.fromJson(object, Comment.class);
    }

    private static boolean filterEvents(@Nullable IssueEventType type) {
        return type != IssueEventType.subscribed && type != IssueEventType.unsubscribed && type != IssueEventType.mentioned;
    }
}
