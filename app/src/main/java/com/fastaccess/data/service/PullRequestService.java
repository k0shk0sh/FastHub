package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.AssigneesRequestModel;
import com.fastaccess.data.dao.CommitFileModel;
import com.fastaccess.data.dao.IssueRequestModel;
import com.fastaccess.data.dao.MergeRequestModel;
import com.fastaccess.data.dao.MergeResponseModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.PullRequestStatusModel;
import com.fastaccess.data.dao.model.Commit;
import com.fastaccess.data.dao.model.PullRequest;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import io.reactivex.Observable;

/**
 * Created by Kosh on 15 Dec 2016, 10:21 PM
 */

public interface PullRequestService {

    @GET("repos/{owner}/{repo}/pulls")
    Observable<Pageable<PullRequest>> getPullRequests(@Path("owner") String owner, @Path("repo") String repo,
                                                      @Query("state") String state, @Query("page") int page);

    @GET("search/issues")
    Observable<Pageable<PullRequest>> getPullsWithCount(@NonNull @Query(value = "q", encoded = true) String query,
                                                        @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<PullRequest> getPullRequest(@Path("owner") String owner, @Path("repo") String repo, @Path("number") long number);

    @PUT("repos/{owner}/{repo}/pulls/{number}/merge")
    Observable<MergeResponseModel> mergePullRequest(@Path("owner") String owner, @Path("repo") String repo,
                                                    @Path("number") long number, @Body MergeRequestModel body);


    @GET("repos/{owner}/{repo}/pulls/{number}/commits")
    Observable<Pageable<Commit>> getPullRequestCommits(@Path("owner") String owner, @Path("repo") String repo,
                                                       @Path("number") long number,
                                                       @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/{number}/files")
    Observable<Pageable<CommitFileModel>> getPullRequestFiles(@Path("owner") String owner, @Path("repo") String repo,
                                                              @Path("number") long number,
                                                              @Query("page") int page);

    @GET("repos/{owner}/{repo}/pulls/{number}/merge")
    Observable<Response<Boolean>> hasPullRequestBeenMerged(@Path("owner") String owner, @Path("repo") String repo,
                                                           @Path("number") long number);

    @PATCH("repos/{owner}/{repo}/pulls/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<PullRequest> editPullRequest(@Path("owner") String owner, @Path("repo") String repo,
                                            @Path("number") int number,
                                            @Body IssueRequestModel issue);

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<PullRequest> editIssue(@Path("owner") String owner, @Path("repo") String repo,
                                      @Path("number") int number,
                                      @Body IssueRequestModel issue);

    @POST("repos/{owner}/{repo}/issues/{number}/assignees")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    Observable<PullRequest> putAssignees(@Path("owner") String owner, @Path("repo") String repo,
                                         @Path("number") int number, @Body AssigneesRequestModel body);

    @GET("repos/{owner}/{repo}/commits/{ref}/status")
    Observable<PullRequestStatusModel> getPullStatus(@Path("owner") String owner, @Path("repo") String repo, @Path("ref") String ref);

    @POST("repos/{owner}/{repo}/pulls/{number}/requested_reviewers")
    @Headers("Accept: application/vnd.github.black-cat-preview+json")
    Observable<PullRequest> putReviewers(@Path("owner") String owner, @Path("repo") String repo,
                                         @Path("number") int number, @Body AssigneesRequestModel body);
}
