package com.fastaccess.data.service;


import android.support.annotation.NonNull;

import com.fastaccess.data.dao.AssigneesRequestModel;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CreateIssueModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.IssueEvent;

import java.util.List;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface IssueService {

    @GET("repos/{owner}/{repo}/issues")
    Observable<Pageable<Issue>> getRepositoryIssues(@Path("owner") String owner, @Path("repo") String repo,
                                                    @Query("state") String state, @Query("sort") String sortBy,
                                                    @Query("page") int page);

    @GET("search/issues") Observable<Pageable<Issue>> getIssuesWithCount(@NonNull @Query(value = "q", encoded = true) String query,
                                                                         @Query("page") int page);

    @GET("user/issues")
    Observable<Pageable<Issue>> getUserIssues(@Query("page") int page, @Query("state") @NonNull String state);

    @GET("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Issue> getIssue(@Path("owner") String owner, @Path("repo") String repo,
                               @Path("number") int number);

    @GET("repos/{owner}/{repo}/issues/{issue_number}/events?per_page=200")
    Observable<Pageable<IssueEvent>> getTimeline(@Path("owner") String owner, @Path("repo") String repo,
                                                 @Path("issue_number") int issue_number);

    @POST("repos/{owner}/{repo}/issues")
    Observable<Issue> createIssue(@Path("owner") String owner, @Path("repo") String repo,
                                  @Body IssueRequestModel issue);

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Issue> editIssue(@Path("owner") String owner, @Path("repo") String repo,
                                @Path("number") int number,
                                @Body IssueRequestModel issue);

    @Headers("Content-Length: 0")
    @PUT("repos/{owner}/{repo}/issues/{number}/lock")
    Observable<Response<Boolean>> lockIssue(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number);

    @DELETE("repos/{owner}/{repo}/issues/{number}/lock")
    Observable<Response<Boolean>> unlockIssue(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number);


    @GET("repos/{owner}/{repo}/issues/{number}/comments?per_page=200")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Pageable<Comment>> getIssueComments(@Path("owner") String owner,
                                                   @Path("repo") String repo,
                                                   @Path("number") int number);

    @GET("repos/{owner}/{repo}/issues/{number}/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Comment> getIssueComment(@Path("owner") String owner, @Path("repo") String repo,
                                        @Path("number") int number, @Path("id") long id);

    @POST("repos/{owner}/{repo}/issues/{number}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Comment> createIssueComment(@Path("owner") String owner, @Path("repo") String repo,
                                           @Path("number") int number, @Body CommentRequestModel body);

    @PATCH("repos/{owner}/{repo}/issues/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<Comment> editIssueComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id,
                                         @Body CommentRequestModel body);

    @DELETE("repos/{owner}/{repo}/issues/comments/{id}")
    Observable<Response<Boolean>> deleteIssueComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id);

    @POST("repos/{owner}/{repo}/issues")
    Observable<Issue> createIssue(@Path("owner") String owner, @Path("repo") String repo, @NonNull @Body CreateIssueModel body);

    @PUT("repos/{owner}/{repo}/issues/{number}/labels")
    Observable<Pageable<LabelModel>> putLabels(@Path("owner") String owner, @Path("repo") String repo,
                                               @Path("number") int number, @Body @NonNull List<String> labels);


    @POST("repos/{owner}/{repo}/issues/{number}/assignees")
    Observable<Issue> putAssignees(@Path("owner") String owner, @Path("repo") String repo,
                                   @Path("number") int number, @Body AssigneesRequestModel body);


}