package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.PostReactionModel;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Kosh on 29 Mar 2017, 9:48 PM
 */
public interface ReactionsService {

    @POST("/repos/{owner}/{repo}/issues/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<Boolean> postIssueReaction(@NonNull @Body PostReactionModel body,
                                           @NonNull @Path("owner") String owner,
                                           @Path("repo") @NonNull String repo,
                                           @Path("id") long id);

    @POST("/repos/{owner}/{repo}/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    Observable<Boolean> postCommitReaction(@NonNull @Body PostReactionModel body,
                                            @NonNull @Path("owner") String owner,
                                            @Path("repo") @NonNull String repo,
                                            @Path("id") long id);
}

