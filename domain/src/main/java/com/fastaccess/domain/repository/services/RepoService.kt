package com.fastaccess.domain.repository.services

import com.fastaccess.domain.response.IssueResponse
import com.fastaccess.domain.response.LabelResponse
import com.fastaccess.domain.response.body.AssigneesBodyModel
import com.fastaccess.domain.response.body.LabelsBodyModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Kosh on 07.03.19.
 */
interface RepoService {
    @POST("repos/{owner}/{repo}/labels")
    fun addLabel(@Path("owner") owner: String, @Path("repo") repo: String, @Body body: LabelResponse): Observable<LabelResponse>


    @POST("repos/{owner}/{repo}/issues/{number}/labels")
    fun addLabelsToIssue(@Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int,
                         @Body body: LabelsBodyModel): Observable<LabelResponse>

    @HTTP(method = "DELETE", path = "repos/{owner}/{repo}/issues/{number}/labels/{name}", hasBody = false)
    fun removeLabelsToIssue(@Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int,
                            @Path("name") name: String): Observable<LabelResponse>

    @POST("repos/{owner}/{repo}/issues/{number}/assignees")
    fun addAssignees(@Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int,
                     @Body body: AssigneesBodyModel): Observable<IssueResponse>

    @HTTP(method = "DELETE", path = "repos/{owner}/{repo}/issues/{number}/assignees", hasBody = true)
    fun removeAssignees(@Path("owner") owner: String, @Path("repo") repo: String, @Path("number") number: Int,
                        @Body body: AssigneesBodyModel): Observable<IssueResponse>
}