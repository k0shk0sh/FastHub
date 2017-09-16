package com.fastaccess.data.service

import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.data.dao.ProjectsModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

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

    @GET("projects/{projectId}/columns?per_page=100")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun getProjectColumns(@Path("projectId") projectId: Long): Observable<Pageable<ProjectColumnModel>>

    @GET("projects/columns/{columnId}/cards")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun getProjectCards(@Path("columnId") columnId: Long, @Query("page") page: Int): Observable<Pageable<ProjectCardModel>>

    @POST("projects/columns/{projectId}")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun createColumn(@Path("projectId") projectId: Long, @Body card: ProjectColumnModel): Observable<ProjectColumnModel>

    @PATCH("projects/columns/{projectId}")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun updateColumn(@Path("projectId") projectId: Long, @Body card: ProjectColumnModel): Observable<ProjectColumnModel>

    @DELETE("projects/columns/{projectId}")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun deleteColumn(@Path("projectId") projectId: Long): Observable<Response<Boolean>>

    @POST("/projects/columns/{columnId}/cards")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun createCard(@Path("columnId") columnId: Long, @Body card: ProjectCardModel): Observable<ProjectCardModel>

    @PATCH("projects/columns/cards/{cardId}")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun updateCard(@Path("cardId") cardId: Long, @Body card: ProjectCardModel): Observable<ProjectCardModel>

    @DELETE("projects/columns/cards/{cardId}")
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    fun deleteCard(@Path("cardId") cardId: Long): Observable<Response<Boolean>>
}