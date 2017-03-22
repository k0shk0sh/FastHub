package com.fastaccess.data.service;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.Pageable;
import com.fastaccess.data.dao.model.User;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Kosh on 22 Mar 2017, 6:44 PM
 */

public interface OrganizationService {

    @GET("/orgs/{org}") Observable<User> getOrganization(@NonNull @Path("org") String org);

    @GET("/user/orgs") Observable<Pageable<User>> getMyOrganizations();

    @GET("/users/{user}/orgs") Observable<Pageable<User>> getMyOrganizations(@NonNull @Path("user") String user);

    @GET("/orgs/{org}/members") Observable<Pageable<User>> getOrgMembers(@NonNull @Path("user") String user);
}
