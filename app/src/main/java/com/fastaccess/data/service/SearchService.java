package com.fastaccess.data.service;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.SearchCodeModel;
import com.fastaccess.data.dao.model.Issue;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.data.dao.model.User;

import retrofit2.http.GET;
import retrofit2.http.Query;
import io.reactivex.Observable;

/**
 * Created by Kosh on 08 Dec 2016, 9:07 PM
 */

public interface SearchService {

    @GET("search/repositories")
    Observable<Pageable<Repo>> searchRepositories(@Query(value = "q", encoded = true) String query, @Query("page") long page);

    @GET("search/code")
    Observable<Pageable<SearchCodeModel>> searchCode(@Query(value = "q", encoded = true) String query, @Query("page") long page);

    @GET("search/issues")
    Observable<Pageable<Issue>> searchIssues(@Query(value = "q", encoded = true) String query, @Query("page") long page);

    @GET("search/users")
    Observable<Pageable<User>> searchUsers(@Query(value = "q", encoded = true) String query, @Query("page") long page);
}
