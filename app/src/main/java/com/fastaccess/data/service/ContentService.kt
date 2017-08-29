package com.fastaccess.data.service

import com.fastaccess.data.dao.GitCommitModel
import com.fastaccess.data.dao.MergeRequestModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by kosh on 29/08/2017.
 */
interface ContentService {

    @PUT("repos/{owner}/{repoId}/contents/{path}")
    fun createUpdateFile(@Path("owner") owner: String,
                         @Path("repoId") repoId: String,
                         @Path("path") path: String,
                         @Body body: MergeRequestModel): Observable<GitCommitModel>
}