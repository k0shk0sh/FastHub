package com.fastaccess.data.service

import com.fastaccess.data.dao.CommitRequestModel
import com.fastaccess.data.dao.GitCommitModel
import com.fastaccess.data.dao.GitHubStatusModel
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by kosh on 29/08/2017.
 */
interface ContentService {

    @PUT("repos/{owner}/{repoId}/contents/{path}")
    fun updateCreateFile(@Path("owner") owner: String,
                         @Path("repoId") repoId: String,
                         @Path("path") path: String,
                         @Query("branch") branch: String,
                         @Body body: CommitRequestModel): Observable<GitCommitModel>

    @HTTP(method = "DELETE", path = "repos/{owner}/{repoId}/contents/{path}", hasBody = true)
    fun deleteFile(@Path("owner") owner: String,
                   @Path("repoId") repoId: String,
                   @Path("path") path: String,
                   @Query("branch") branch: String,
                   @Body body: CommitRequestModel): Observable<GitCommitModel>

    @GET("api/last-message.json")
    fun checkStatus(): Observable<GitHubStatusModel>
}