package com.fastaccess.data.service;

import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.data.dao.SearchCodeModel;
import com.fastaccess.data.dao.UserModel;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Kosh on 08 Dec 2016, 9:07 PM
 */

public interface SearchService {

    @GET("search/repositories")
    Observable<Pageable<RepoModel>> searchRepositories(@Query("q") String query, @Query("page") long page);

    @GET("search/code")
    Observable<Pageable<SearchCodeModel>> searchCode(@Query("q") String query, @Query("page") long page);

    @GET("search/issues")
    Observable<Pageable<IssueModel>> searchIssues(@Query("q") String query, @Query("page") long page);

    @GET("search/users")
    Observable<Pageable<UserModel>> searchUsers(@Query("q") String query, @Query("page") long page);
}
