package com.fastaccess.ui.modules.profile.banner;

import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.ImgurReponseModel;
import com.fastaccess.data.dao.model.Gist;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.ImgurProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;

/**
 * Created by JediB on 5/25/2017.
 */

public class BannerInfoPresenter extends BasePresenter<BannerInfoMvp.View> implements BannerInfoMvp.Presenter {
    @Override public void onPostImage(@NonNull String path) {
        RequestBody image = RequestBody.create(MediaType.parse("image/*"), new File(path));
        ImgurProvider.getImgurService().postImage("", image);
        makeRestCall(RxHelper.getObserver(ImgurProvider.getImgurService().postImage("", image))
                        .filter(imgurReponseModel -> imgurReponseModel != null && imgurReponseModel.getData() != null)
                        .flatMap(response -> RxHelper.getObserver(Gist.getMyGists(Login.getUser().getLogin()))
                                .flatMap(Observable::from)
                                .filter(gist -> "header.fst".equalsIgnoreCase(gist.getDescription()))
                                .flatMap(gist -> RxHelper.getObserver(RestProvider.getGistService().deleteGist(gist.getGistId())))
                                .flatMap(booleanResponse -> {
                                    CreateGistModel createGistModel = getCreateGistModel(response);
                                    return RxHelper.getObserver(RestProvider.getGistService().createGist(createGistModel));
                                }), (imgurReponseModel, gist) -> imgurReponseModel.getData()),
                response -> sendToView(view -> {
                    PrefGetter.setProfileBackgroundUrl(response != null ? response.getLink() : null);
                    if (response != null) {
                        view.onFinishedUploading();
                    } else {
                        view.showMessage(R.string.error, R.string.unexpected_error);
                    }
                }));
    }

    @NonNull private CreateGistModel getCreateGistModel(ImgurReponseModel response) {
        CreateGistModel createGistModel = new CreateGistModel();
        createGistModel.setDescription(InputHelper.toString("header.fst"));
        createGistModel.setPublicGist(true);
        HashMap<String, FilesListModel> modelHashMap = new HashMap<>();
        FilesListModel file = new FilesListModel();
        file.setFilename("header.fst");
        file.setContent(response.getData().getLink());
        modelHashMap.put("header.fst", file);
        createGistModel.setFiles(modelHashMap);
        return createGistModel;
    }
}