package com.fastaccess.ui.modules.profile.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.fastaccess.data.dao.CreateGistModel;
import com.fastaccess.data.dao.FilesListModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.data.dao.model.User;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.ImgurProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.contributions.ContributionsDay;
import com.fastaccess.ui.widgets.contributions.ContributionsProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import io.reactivex.Observable;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

class ProfileOverviewPresenter extends BasePresenter<ProfileOverviewMvp.View> implements ProfileOverviewMvp.Presenter {
    @com.evernote.android.state.State boolean isSuccessResponse;
    @com.evernote.android.state.State boolean isFollowing;
    @com.evernote.android.state.State String login;
    @com.evernote.android.state.State ArrayList<User> userOrgs = new ArrayList<>();
    private ArrayList<ContributionsDay> contributions = new ArrayList<>();
    private static final String URL = "https://github.com/users/%s/contributions";

    @Override public void onCheckFollowStatus(@NonNull String login) {
        if (!TextUtils.equals(login, Login.getUser().getLogin()))
            makeRestCall(RestProvider.getUserService().getFollowStatus(login),
                    booleanResponse -> {
                        isSuccessResponse = true;
                        isFollowing = booleanResponse.code() == 204;
                        sendToView(ProfileOverviewMvp.View::invalidateFollowBtn);
                    });
    }

    @Override public boolean isSuccessResponse() {
        return isSuccessResponse;
    }

    @Override public boolean isFollowing() {
        return isFollowing;
    }

    @Override public void onFollowButtonClicked(@NonNull String login) {
        manageDisposable(RxHelper.getObserver(!isFollowing ? RestProvider.getUserService().followUser(login)
                                                           : RestProvider.getUserService().unfollowUser(login))
                .subscribe(booleanResponse -> {
                    if (booleanResponse.code() == 204) {
                        isFollowing = !isFollowing;
                        sendToView(ProfileOverviewMvp.View::invalidateFollowBtn);
                    }
                }, this::onError));
    }

    @Override public void onError(@NonNull Throwable throwable) {
        int statusCode = RestProvider.getErrorCode(throwable);
        if (statusCode == 404) {
            sendToView(ProfileOverviewMvp.View::onUserNotFound);
            return;
        }
        if (!InputHelper.isEmpty(login)) {
            onWorkOffline(login);
        }
        sendToView(ProfileOverviewMvp.View::invalidateFollowBtn);
        super.onError(throwable);
    }

    @Override public void onFragmentCreated(@Nullable Bundle bundle) {
        if (bundle == null || bundle.getString(BundleConstant.EXTRA) == null) {
            throw new NullPointerException("Either bundle or User is null");
        }
        login = bundle.getString(BundleConstant.EXTRA);
        if (login != null) {
            loadOrgs();
            loadContributions();
//            loadUrlBackgroundImage();
            makeRestCall(RestProvider.getUserService().getUser(login), userModel -> {
                onSendUserToView(userModel);
                if (userModel != null) {
                    userModel.save(userModel);
                    if (userModel.getType() != null && userModel.getType().equalsIgnoreCase("user")) {
                        onCheckFollowStatus(login);
                    }
                }
            });
        }
    }

    @Override public void onWorkOffline(@NonNull String login) {
        User userModel = User.getUser(login);
        if (userModel == null) {
            return;
        }
        onSendUserToView(userModel);
    }

    @Override public void onSendUserToView(@Nullable User userModel) {
        sendToView(view -> view.onInitViews(userModel));
    }

    @NonNull @Override public ArrayList<User> getOrgs() {
        return userOrgs;
    }

    @NonNull @Override public ArrayList<ContributionsDay> getContributions() {
        return contributions;
    }

    @NonNull @Override public String getLogin() {
        return login;
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
                gist -> sendToView(view -> view.onImagePosted(PrefGetter.getProfileBackgroundUrl())));
    }

    @NonNull private Observable<String> getHeaderGist() {
        return RxHelper.getObserver(RestProvider.getGistService(true).getGistFile(ProfileOverviewMvp.HEADER_FST_URL));
    }

    private void loadContributions() {
        String url = String.format(URL, login);
        manageDisposable(RxHelper.getObserver(RestProvider.getContribution().getContributions(url))
                .flatMap(s -> Observable.just(new ContributionsProvider().getContributions(s)))
                .subscribe(lists -> {
                    contributions.clear();
                    contributions.addAll(lists);
                    sendToView(view -> view.onInitContributions(contributions));
                }, Throwable::printStackTrace));
    }

    private void loadOrgs() {
        boolean isMe = login.equalsIgnoreCase(Login.getUser() != null ? Login.getUser().getLogin() : "");
        manageDisposable(RxHelper.getObserver(isMe ? RestProvider.getOrgService().getMyOrganizations()
                                                   : RestProvider.getOrgService().getMyOrganizations(login))
                .subscribe(response -> {
                    if (response != null && response.getItems() != null) {
                        userOrgs.addAll(response.getItems());
                    }
                    sendToView(view -> view.onInitOrgs(userOrgs));
                }, Throwable::printStackTrace));
    }

    private void loadUrlBackgroundImage() {
        if (Login.getUser().getLogin().equalsIgnoreCase(login)) {
            if (PrefGetter.getProfileBackgroundUrl() == null) {
                manageDisposable(getHeaderGist()
                        .flatMap(s -> RxHelper.getObserver(Observable.fromArray(s.split("\n"))))
                        .flatMap(s -> RxHelper.getObserver(Observable.just(s.split("->"))))
                        .filter(strings -> strings != null && strings[0].equalsIgnoreCase(login))
                        .map(strings -> strings[1])
                        .subscribe(s -> sendToView(view -> view.onImagePosted(s)), Throwable::printStackTrace));
            } else {
                sendToView(view -> view.onImagePosted(PrefGetter.getProfileBackgroundUrl()));
            }
        } else {
            manageDisposable(getHeaderGist()
                    .flatMap(s -> RxHelper.getObserver(Observable.fromArray(s.split("\n"))))
                    .flatMap(s -> RxHelper.getObserver(Observable.just(s.split("->"))))
                    .filter(strings -> strings != null && strings[0].equalsIgnoreCase(login))
                    .map(strings -> strings[1])
                    .subscribe(s -> sendToView(view -> view.onImagePosted(s)), Throwable::printStackTrace));
        }
    }
}