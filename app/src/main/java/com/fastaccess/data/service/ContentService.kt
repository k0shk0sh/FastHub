package com.fastaccess.data.service

import com.fastaccess.data.dao.CommitRequestModel
import com.fastaccess.data.dao.GitCommitModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by kosh on 29/08/2017.
 */
interface ContentService {

    @PUT("repos/{owner}/{repoId}/contents/{path}")
    fun createUpdateFile(@Path("owner") owner: String,
                         @Path("repoId") repoId: String,
                         @Path("path") path: String,
                         @Query("branch") branch: String,
                         @Body body: CommitRequestModel): Observable<GitCommitModel>
}