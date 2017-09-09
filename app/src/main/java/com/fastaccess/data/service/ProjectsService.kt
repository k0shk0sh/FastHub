package com.fastaccess.data.service

import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.ProjectsModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by kosh on 09/09/2017.
 */

interface ProjectsService {

    @GET("repos/{owner}/{repo}/projects")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun getRepoProjects(@Path("owner") owner: String, @Path("repo") repo: String,
                        @Query("state") state: String?, @Query("page") page: Int): Observable<Pageable<ProjectsModel>>

    @GET("orgs/{org}/projects")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun getOrgsProjects(@Path("org") org: String,
                        @Query("page") page: Int): Observable<Pageable<ProjectsModel>>

}