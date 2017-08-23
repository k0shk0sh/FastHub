package com.fastaccess.provider.timeline

import com.fastaccess.data.dao.*
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

    fun convert(jsonObjects: List<JsonObject>?, comments: Pageable<ReviewCommentModel>?): List<TimelineModel> {
        val list = arrayListOf<TimelineModel>()
        if (jsonObjects == null) return list
        val gson = RestProvider.gson

        jsonObjects.onEach { jsonObject ->
            val event = jsonObject.get("event").asString
            val timeline = TimelineModel()
            if (!InputHelper.isEmpty(event)) {
                val type = IssueEventType.getType(event)
                timeline.event = type
                if (type != null) {
                    if (type == IssueEventType.commented) {
                        timeline.comment = getComment(jsonObject, gson)
                        list.add(timeline)
                    } else if (type == IssueEventType.reviewed) {
                        val review = getReview(jsonObject, gson)
                        if (review != null) {
                            timeline.review = review
                            list.add(timeline)
                            val reviewComments = arrayListOf<ReviewCommentModel>()
                            val firstReview = comments?.items?.firstOrNull { it.pullRequestReviewId == review.id }
                            if (firstReview != null) {
                                firstReview.let {
                                    val grouped = GroupedReviewModel()
                                    grouped.diffText = it.diffHunk
                                    grouped.path = it.path
                                    grouped.position = it.position
                                    grouped.date = it.createdAt
                                    reviewComments.add(it)
                                    comments.items?.onEach {
                                        if (firstReview.id != it.id) {
                                            if (firstReview.position == it.position && firstReview.path == it.path) {
                                                reviewComments.add(it)
                                            }
                                        }
                                    }
                                    grouped.comments = reviewComments
                                    val groupTimeline = TimelineModel()
                                    groupTimeline.event = IssueEventType.GROUPED
                                    groupTimeline.groupedReviewModel = grouped
                                    list.add(groupTimeline)
                                }
                            }
                        }
                    } else {
                        timeline.genericEvent = getGenericEvent(jsonObject, gson)
                        list.add(timeline)
                    }
                }
            } else {
                timeline.genericEvent = getGenericEvent(jsonObject, gson)
                list.add(timeline)
            }
        }
        return list.filter { filterEvents(it.event) }
    }

    private fun getGenericEvent(jsonObject: JsonObject, gson: Gson): GenericEvent {
        return gson.fromJson(jsonObject, GenericEvent::class.java)
    }

    private fun getComment(jsonObject: JsonObject, gson: Gson): Comment {
        return gson.fromJson(jsonObject, Comment::class.java)
    }

    private fun filterEvents(type: IssueEventType?): Boolean {
        return type != null && type != IssueEventType.subscribed && type != IssueEventType.unsubscribed && type != IssueEventType.mentioned
    }

    private fun getReview(jsonObject: JsonObject, gson: Gson): ReviewModel? {
        return gson.fromJson(jsonObject, ReviewModel::class.java)
    }
}
