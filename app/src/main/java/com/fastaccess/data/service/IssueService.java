package com.fastaccess.data.service;


import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.CreateIssueModel;
import com.fastaccess.data.dao.IssueEventModel;
import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.Pageable;

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
    Observable<Pageable<IssueModel>> getRepositoryIssues(@Path("owner") String owner, @Path("repo") String repo,
                                                         @Query("state") String state, @Query("page") int page);

    @GET("repos/{owner}/{repo}/issues/{number}")
//    @Headers("Accept: application/vnd.github.VERSION.html")
    Observable<IssueModel> getIssue(@Path("owner") String owner, @Path("repo") String repo,
                                    @Path("number") int number);

    @GET("repos/{owner}/{repo}/issues/{issue_number}/events")
    Observable<Pageable<IssueEventModel>> getTimeline(@Path("owner") String owner, @Path("repo") String repo,
                                                      @Path("issue_number") int issue_number,
                                                      @Query("page") int page);

    @POST("repos/{owner}/{repo}/issues")
    Observable<IssueModel> createIssue(@Path("owner") String owner, @Path("repo") String repo,
                                       @Body IssueRequestModel issue);

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    Observable<IssueModel> editIssue(@Path("owner") String owner, @Path("repo") String repo,
                                     @Path("number") int number,
                                     @Body IssueRequestModel issue);

    @Headers("Content-Length: 0")
    @PUT("repos/{owner}/{repo}/issues/{number}/lock")
    Observable<Response<Boolean>> lockIssue(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number);

    @DELETE("repos/{owner}/{repo}/issues/{number}/lock")
    Observable<Response<Boolean>> unlockIssue(@Path("owner") String owner, @Path("repo") String repo, @Path("number") int number);


    @GET("repos/{owner}/{repo}/issues/{number}/comments")
//    @Headers("Accept: application/vnd.github.VERSION.html")
    Observable<Pageable<CommentsModel>> getIssueComments(@Path("owner") String owner,
                                                         @Path("repo") String repo,
                                                         @Path("number") int number,
                                                         @Query("page") int page);

    @GET("repos/{owner}/{repo}/issues/{number}/comments/{id}")
    Observable<CommentsModel> getIssueComment(@Path("owner") String owner, @Path("repo") String repo,
                                              @Path("number") int number, @Path("id") long id);

    @POST("repos/{owner}/{repo}/issues/{number}/comments")
    Observable<CommentsModel> createIssueComment(@Path("owner") String owner, @Path("repo") String repo,
                                                 @Path("number") int number, @Body CommentRequestModel body);

    @PATCH("repos/{owner}/{repo}/issues/comments/{id}")
    Observable<CommentsModel> editIssueComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id,
                                               @Body CommentRequestModel body);

    @DELETE("repos/{owner}/{repo}/issues/comments/{id}")
    Observable<Response<Boolean>> deleteIssueComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id);

    @POST("repos/{owner}/{repo}/issues")
    Observable<IssueModel> createIssue(@Path("owner") String owner, @Path("repo") String repo, @NonNull @Body CreateIssueModel body);

    @POST("repos/{owner}/{repo}/issues/{number}/labels")
    Observable<Pageable<LabelModel>> putLabels(@Path("owner") String owner, @Path("repo") String repo,
                                               @Path("number") int number, @Body @NonNull List<String> labels);
}