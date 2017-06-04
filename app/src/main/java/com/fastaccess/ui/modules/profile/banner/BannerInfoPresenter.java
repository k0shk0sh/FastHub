package com.fastaccess.ui.modules.profile.banner;

import android.support.annotation.NonNull;

import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.ImgurProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.profile.overview.ProfileOverviewMvp;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import io.reactivex.Observable;

/**
 * Created by JediB on 5/25/2017.
 */

public class BannerInfoPresenter extends BasePresenter<BannerInfoMvp.View> implements BannerInfoMvp.Presenter {
    @NonNull private Observable<String> getHeaderGist() {
        return RxHelper.getObserver(RestProvider.getGistService(true).getGistFile(ProfileOverviewMvp.HEADER_FST_URL));
    }

    @Override public void onPostImage(@NonNull String path) {
        Login login = Login.getUser();
        RequestBody image = RequestBody.create(MediaType.parse("image/*"), new File(path));
        ImgurProvider.getImgurService().postImage("", image);
        makeRestCall(RxHelper.getObserver(ImgurProvider.getImgurService().postImage("", image))
                        .filter(imgurReponseModel -> imgurReponseModel != null && imgurReponseModel.getData() != null)
                        .map(imgurReponseModel -> imgurReponseModel.getData().getLink())
                        .flatMap(link -> getHeaderGist(), (imageLink, gistContent) -> {
                            boolean isReplace = false;
                            if (gistContent.contains(login.getLogin() + "->")) {
                                String[] splitByNewLine = gistContent.split("\n");
                                for (String s : splitByNewLine) {
                                    String[] splitByUser = s.split("->");
                                    if (login.getLogin().equalsIgnoreCase(splitByUser[0])) {
                                        gistContent = gistContent.replaceFirst(splitByUser[0] + "->" +
                                                splitByUser[1], login.getLogin() + "->" + imageLink);
                                        isReplace = true;
                                        break;
                                    }
                                }
                            }
                            PrefGetter.setProfileBackgroundUrl(imageLink);
                            if (!isReplace) {
                                gistContent += "\n" + login.getLogin() + "->" + imageLink;
                            }
                            return gistContent;
                        })
                        .map(s -> {
                            CreateGistModel createGistModel = new CreateGistModel();
                            createGistModel.setPublicGist(true);
                            HashMap<String, FilesListModel> modelHashMap = new HashMap<>();
                            FilesListModel file = new FilesListModel();
                            file.setFilename("header.fst");
                            file.setContent(s);
                            modelHashMap.put("header.fst", file);
                            createGistModel.setFiles(modelHashMap);
                            return createGistModel;
                        })
                        .flatMap(body -> RxHelper.getObserver(RestProvider.getGistService().editGist(body, ProfileOverviewMvp.HEADER_GIST_ID))),
                gist -> sendToView(BannerInfoMvp.View::onFinishedUploading));
    }
}