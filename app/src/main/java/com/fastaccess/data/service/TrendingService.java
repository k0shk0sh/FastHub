package com.fastaccess.data.service;


import android.support.annotation.Nullable;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kosh on 02 Jun 2017, 12:58 PM
 */

public interface TrendingService {

    @GET("{lan}") Observable<String> getTrending(@Path("lan") @Nullable String lan, @Nullable @Query("since") String since);
}
