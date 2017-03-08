package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.BranchesModel;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.CommentsModel;
import com.fastaccess.data.dao.CommitModel;
import com.fastaccess.data.dao.CreateMilestoneModel;
import com.fastaccess.data.dao.LabelModel;
import com.fastaccess.data.dao.MarkdownModel;
import com.fastaccess.data.dao.MilestoneModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.ReleasesModel;
import com.fastaccess.data.dao.RepoFilesModel;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.data.dao.UserModel;

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
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Kosh on 10 Dec 2016, 3:16 PM
 */
public interface RepoService {


    @GET @Headers("Accept: application/vnd.github.VERSION.raw")
    Observable<String> getFileAsStream(@Url String url);

    @POST("markdown")
    Observable<String> convertReadmeToHtml(@Body MarkdownModel model);

    @GET("repos/{login}/{repoId}") @Headers({"Accept: application/vnd.github.drax-preview+json"})
    Observable<RepoModel> getRepo(@Path("login") String login, @Path("repoId") String repoId);

    @DELETE("repos/{login}/{repoId}")
    Observable<Response<Boolean>> deleteRepo(@Path("login") String login, @Path("repoId") String repoId);

    @GET @Headers("Accept: application/vnd.github.html")
    Observable<String> getReadmeHtml(@NonNull @Url String url);

    @GET("user/starred/{owner}/{repo}")
    Observable<Response<Boolean>> checkStarring(@NonNull @Path("owner") String login, @NonNull @Path("repo") String repoId);

    @PUT("user/starred/{owner}/{repo}")
    Observable<Response<Boolean>> starRepo(@NonNull @Path("owner") String login, @NonNull @Path("repo") String repoId);

    @DELETE("user/starred/{owner}/{repo}")
    Observable<Response<Boolean>> unstarRepo(@NonNull @Path("owner") String login, @NonNull @Path("repo") String repoId);

    @POST("/repos/{owner}/{repo}/forks")
    Observable<RepoModel> forkRepo(@NonNull @Path("owner") String login, @NonNull @Path("repo") String repoId);

    @GET("user/subscriptions/{owner}/{repo}")
    Observable<Response<Boolean>> isWatchingRepo(@Path("owner") String owner, @Path("repo") String repo);

    @PUT("user/subscriptions/{owner}/{repo}")
    Observable<Response<Boolean>> watchRepo(@Path("owner") String owner, @Path("repo") String repo);

    @DELETE("user/subscriptions/{owner}/{repo}")
    Observable<Response<Boolean>> unwatchRepo(@Path("owner") String owner, @Path("repo") String repo);

    @GET("repos/{owner}/{repo}/commits")
    Observable<Pageable<CommitModel>> getCommits(@Path("owner") String owner, @Path("repo") String repo, @Query("page") int page);

    @GET("repos/{owner}/{repo}/releases")
    @Headers("Accept: application/vnd.github.VERSION.html")
    Observable<Pageable<ReleasesModel>> getReleases(@Path("owner") String owner, @Path("repo") String repo, @Query("page") int page);

    @GET("repos/{owner}/{repo}/contributors")
    Observable<Pageable<UserModel>> getContributors(@Path("owner") String owner, @Path("repo") String repo, @Query("page") int page);

    @GET("repos/{owner}/{repo}/commits/{sha}")
    Observable<CommitModel> getCommit(@Path("owner") String owner, @Path("repo") String repo, @Path("sha") String sha);

    @GET("repos/{owner}/{repo}/commits/{sha}/comments")
    Observable<Pageable<CommentsModel>> getCommitComments(@NonNull @Path("owner") String owner, @NonNull @Path("repo") String repo,
                                                          @NonNull @Path("sha") String ref, @Query("page") int page);

    @POST("repos/{owner}/{repo}/commits/{sha}/comments")
    Observable<CommentsModel> postCommitComment(@NonNull @Path("owner") String owner, @NonNull @Path("repo") String repo,
                                                @NonNull @Path("sha") String ref, @Body CommentRequestModel model);

    @PATCH("repos/{owner}/{repo}/comments/{id}")
    Observable<CommentsModel> editCommitComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id,
                                                @Body CommentRequestModel body);

    @DELETE("repos/{owner}/{repo}/comments/{id}")
    Observable<Response<Boolean>> deleteComment(@Path("owner") String owner, @Path("repo") String repo, @Path("id") long id);

    @GET("repos/{owner}/{repo}/contents/{path}")
    Observable<Pageable<RepoFilesModel>> getRepoFiles(@NonNull @Path("owner") String owner, @NonNull @Path("repo") String repo,
                                                      @NonNull @Path("path") String path, @NonNull @Query("ref") String ref);

    @GET("repos/{owner}/{repo}/labels")
    Observable<Pageable<LabelModel>> getLabels(@NonNull @Path("owner") String owner, @NonNull @Path("repo") String repo);

    @GET("repos/{owner}/{repo}/collaborators/{username}")
    Observable<Response<Boolean>> isCollaborator(@NonNull @Path("owner") String owner, @NonNull @Path("repo") String repo,
                                                 @NonNull @Path("username") String username);

    @GET("repos/{owner}/{repo}/branches")
    Observable<Pageable<BranchesModel>> getBranches(@NonNull @Path("owner") String owner, @NonNull @Path("repo") String repo);

    @GET("repos/{owner}/{repo}/milestones")
    Observable<Pageable<MilestoneModel>> getMilestones(@Path("owner") String owner, @Path("repo") String repo);

    @POST("repos/{owner}/{repo}/milestones")
    Observable<MilestoneModel> createMilestone(@Path("owner") String owner, @Path("repo") String repo,
                                               @Body CreateMilestoneModel create);

    @GET("repos/{owner}/{repo}/assignees")
    Observable<Pageable<UserModel>> getAssignees(@Path("owner") String owner, @Path("repo") String repo);
}
