package com.fastaccess.ui.modules.profile.banner;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by JediB on 5/25/2017.
 */

public interface BannerInfoMvp {

    interface View extends BaseMvp.FAView {
        void onFinishedUploading();
    }

    interface Presenter {
        void onPostImage(@NonNull String path);
    }

}
