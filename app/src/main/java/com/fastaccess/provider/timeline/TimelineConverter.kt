package com.fastaccess.provider.timeline

import com.fastaccess.data.dao.*
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.timeline.GenericEvent
import com.fastaccess.data.dao.timeline.PullRequestCommitModel
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
                    } else if (type == IssueEventType.commit_commented) {
                        val commit = getCommit(jsonObject, gson)
                        if (commit != null) {
                            val comment = commit.comments?.firstOrNull()
                            comment?.let {
                                commit.path = it.path
                                commit.position = it.position
                                commit.line = it.line
                                commit.login = it.user?.login
                            }
                            timeline.commit = commit
                            list.add(timeline)
                        }
                    } else if (type == IssueEventType.reviewed || type == IssueEventType
                            .changes_requested) {
                        val review = getReview(jsonObject, gson)
                        if (review != null) {
                            timeline.review = review
                            list.add(timeline)
                            val reviewsList = arrayListOf<TimelineModel>()
                            comments?.items?.filter { it.pullRequestReviewId == review.id }
                                    ?.onEach {
                                        val grouped = GroupedReviewModel()
                                        grouped.diffText = it.diffHunk
                                        grouped.path = it.path
                                        grouped.position = it.position
                                        grouped.comments = arrayListOf(it)
                                        grouped.id = it.id
                                        val groupTimeline = TimelineModel()
                                        groupTimeline.event = IssueEventType.GROUPED
                                        groupTimeline.groupedReviewModel = grouped
                                        reviewsList.add(groupTimeline)
                                    }
                            comments?.items?.filter { it.pullRequestReviewId != review.id }?.onEach {
                                reviewsList.onEach { reviews ->
                                    if (it.path == reviews.groupedReviewModel.path && it.position == reviews.groupedReviewModel.position) {
                                        reviews.groupedReviewModel.comments.add(it)
                                    }
                                }
                            }
                            list.addAll(reviewsList)
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
        return list.filter({filterEvents(it.event)})
    }

    private fun getCommit(jsonObject: JsonObject, gson: Gson): PullRequestCommitModel? {
        return gson.fromJson(jsonObject, PullRequestCommitModel::class.java)
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
