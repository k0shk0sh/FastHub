package com.fastaccess.ui.modules.editor.popup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.data.dao.ImgurReponseModel;
import com.fastaccess.provider.rest.ImgurProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by Kosh on 15 Apr 2017, 9:08 PM
 */

public class EditorLinkImagePresenter extends BasePresenter<EditorLinkImageMvp.View> implements EditorLinkImageMvp.Presenter {
    @Override public void onSubmit(@Nullable String title, @NonNull File file) {
        if (file.exists()) {
            RequestBody image = RequestBody.create(MediaType.parse("image/*"), file);
            makeRestCall(ImgurProvider.getImgurService().postImage(title, image),
                    imgurReponseModel -> {
                        if (imgurReponseModel.getData() != null) {
                            ImgurReponseModel.ImgurImage imageResponse = imgurReponseModel.getData();
                            sendToView(view -> view.onUploaded(title == null ? imageResponse.getTitle() : title, imageResponse.getLink()));
                            return;
                        }
                        sendToView(view -> view.onUploaded(null, null));
                    }, false);
        } else {
            if (getView() != null) getView().onUploaded(null, null);
        }
    }
}
