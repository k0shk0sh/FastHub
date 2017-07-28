package com.fastaccess.provider.timeline

import com.fastaccess.data.dao.ReviewCommentModel
import com.fastaccess.data.dao.ReviewModel
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.timeline.GenericEvent
import com.fastaccess.data.dao.types.IssueEventType
import com.fastaccess.helper.InputHelper
import com.fastaccess.provider.rest.RestProvider
import com.google.gson.Gson
import com.google.gson.JsonObject

import io.reactivex.Observable

/**
 * Created by kosh on 26/07/2017.
 */

object TimelineConverter {

    fun convert(jsonObjects: List<JsonObject>?): Observable<TimelineModel> {
        if (jsonObjects == null) return Observable.empty<TimelineModel>()
        val gson = RestProvider.gson
        return Observable.fromIterable(jsonObjects)
                .map { jsonObject ->
                    val event = jsonObject.get("event").asString
                    val timeline = TimelineModel()
                    if (!InputHelper.isEmpty(event)) {
                        val type = IssueEventType.getType(event)
                        timeline.event = type
                        if (type != null) {
                            if (type == IssueEventType.commented) {
                                timeline.comment = getComment(jsonObject, gson)
                            } else if (type == IssueEventType.line_commented) {
                                timeline.reviewComment = getReviewComment(jsonObject, gson)
                            } else if (type == IssueEventType.reviewed) {
                                timeline.review = getReview(jsonObject, gson)
                            } else {
                                timeline.genericEvent = getGenericEvent(jsonObject, gson)
                            }
                        }
                    } else {
                        timeline.genericEvent = getGenericEvent(jsonObject, gson)
                    }
                    timeline
                }
                .filter { filterEvents(it.event) }
    }

    private fun getReview(jsonObject: JsonObject, gson: Gson): ReviewModel {
        return gson.fromJson(jsonObject, ReviewModel::class.java)
    }

    private fun getGenericEvent(jsonObject: JsonObject, gson: Gson): GenericEvent {
        return gson.fromJson(jsonObject, GenericEvent::class.java)
    }

    private fun getReviewComment(jsonObject: JsonObject, gson: Gson): ReviewCommentModel {
        return gson.fromJson(jsonObject, ReviewCommentModel::class.java)
    }

    private fun getComment(jsonObject: JsonObject, gson: Gson): Comment {
        return gson.fromJson(jsonObject, Comment::class.java)
    }

    private fun filterEvents(type: IssueEventType?): Boolean {
        return type != IssueEventType.subscribed && type != IssueEventType.unsubscribed && type != IssueEventType.mentioned
    }
}
