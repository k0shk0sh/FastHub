package com.fastaccess.data.service;


import com.fastaccess.data.dao.TrendingResponse;
import com.github.florent37.retrojsoup.annotations.Select;

import io.reactivex.Observable;

/**
 * Created by Kosh on 02 Jun 2017, 12:58 PM
 */

public interface TrendingService {

    @Select(".repo-list") Observable<TrendingResponse> getTrending();
}
