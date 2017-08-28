package com.fastaccess.ui.modules.repos.code.prettifier;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import com.fastaccess.R;
import com.fastaccess.data.dao.MarkdownModel;
import com.fastaccess.data.dao.model.ViewerFile;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import io.reactivex.Observable;

/**
 * Created by Kosh on 27 Nov 2016, 3:43 PM
 */

class ViewerPresenter extends BasePresenter<ViewerMvp.View> implements ViewerMvp.Presenter {
    private String downloadedStream;
    @com.evernote.android.state.State boolean isMarkdown;
    @com.evernote.android.state.State boolean isRepo;
    @com.evernote.android.state.State boolean isImage;
    @com.evernote.android.state.State String url;
    @com.evernote.android.state.State String htmlUrl;

    @Override public void onError(@NonNull Throwable throwable) {
        throwable.printStackTrace();
        int code = RestProvider.getErrorCode(throwable);
        if (code == 404) {
            if (!isRepo) {
                sendToView(view -> view.onShowError(R.string.no_file_found));
            }
            sendToView(BaseMvp.FAView::hideProgress);
        } else {
            if (code == 406) {
                sendToView(view -> {
                    view.hideProgress();
                    view.openUrl(url);
                });
                return;
            }
            onWorkOffline();
            super.onError(throwable);
        }
    }

    @Override public void onHandleIntent(@Nullable Bundle intent) {
        if (intent == null) return;
        isRepo = intent.getBoolean(BundleConstant.EXTRA);
        url = intent.getString(BundleConstant.ITEM);
        htmlUrl = intent.getString(BundleConstant.EXTRA_TWO);
        if (!InputHelper.isEmpty(url)) {
            if (MarkDownProvider.isArchive(url)) {
                sendToView(view -> view.onShowError(R.string.archive_file_detected_error));
                return;
            }
            if (isRepo) {
                url = url.endsWith("/") ? (url + "readme") : (url + "/readme");
            }
            onWorkOnline();
        }
    }

    @Override public void onLoadContentAsStream() {
        boolean isImage = MarkDownProvider.isImage(url) && !"svg".equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url));
        if (isImage || MarkDownProvider.isArchive(url)) {
            return;
        }
        makeRestCall(RestProvider.getRepoService(isEnterprise()).getFileAsStream(url),
                body -> {
                    downloadedStream = body;
                    sendToView(view -> view.onSetCode(body));
                });
    }

    @Override public String downloadedStream() {
        return downloadedStream;
    }

    @Override public boolean isMarkDown() {
        return isMarkdown;
    }

    @Override public void onWorkOffline() {
        if (downloadedStream == null) {
            manageDisposable(RxHelper.getObservable(ViewerFile.get(url))
                    .subscribe(fileModel -> {
                        if (fileModel != null) {
                            isImage = MarkDownProvider.isImage(fileModel.getFullUrl());
                            if (isImage) {
                                sendToView(view -> view.onSetImageUrl(fileModel.getFullUrl(), false));
                            } else {
                                downloadedStream = fileModel.getContent();
                                isRepo = fileModel.isRepo();
                                isMarkdown = fileModel.isMarkdown();
                                sendToView(view -> {
                                    if (isRepo || isMarkdown) {
                                        view.onSetMdText(downloadedStream, fileModel.getFullUrl(), false);
                                    } else {
                                        view.onSetCode(downloadedStream);
                                    }
                                });
                            }
                        }
                    }, throwable -> sendToView(view -> view.showErrorMessage(throwable.getMessage()))));
        }
    }

    @Override public void onWorkOnline() {
        isImage = MarkDownProvider.isImage(url);
        if (isImage) {
            if ("svg".equalsIgnoreCase(MimeTypeMap.getFileExtensionFromUrl(url))) {
                makeRestCall(RestProvider.getRepoService(isEnterprise()).getFileAsStream(url),
                        s -> sendToView(view -> view.onSetImageUrl(s, true)));
                return;
            }
            sendToView(view -> view.onSetImageUrl(url, false));
            return;
        }
        Observable<String> streamObservable = MarkDownProvider.isMarkdown(url)
                                              ? RestProvider.getRepoService(isEnterprise()).getFileAsHtmlStream(url)
                                              : RestProvider.getRepoService(isEnterprise()).getFileAsStream(url);
        Observable<String> observable = isRepo ? RestProvider.getRepoService(isEnterprise()).getReadmeHtml(url) : streamObservable;
        makeRestCall(observable, content -> {
            downloadedStream = content;
            ViewerFile fileModel = new ViewerFile();
            fileModel.setContent(downloadedStream);
            fileModel.setFullUrl(url);
            fileModel.setRepo(isRepo);
            if (isRepo) {
                fileModel.setMarkdown(true);
                isMarkdown = true;
                isRepo = true;
                sendToView(view -> view.onSetMdText(downloadedStream, htmlUrl == null ? url : htmlUrl, false));
            } else {
                isMarkdown = MarkDownProvider.isMarkdown(url);
                if (isMarkdown) {
                    MarkdownModel model = new MarkdownModel();
                    model.setText(downloadedStream);
                    Uri uri = Uri.parse(url);
                    StringBuilder baseUrl = new StringBuilder();
                    for (String s : uri.getPathSegments()) {
                        if (!s.equalsIgnoreCase(uri.getLastPathSegment())) {
                            baseUrl.append("/").append(s);
                        }
                    }
                    model.setContext(baseUrl.toString());
                    makeRestCall(RestProvider.getRepoService(isEnterprise()).convertReadmeToHtml(model), string -> {
                        isMarkdown = true;
                        downloadedStream = string;
                        fileModel.setMarkdown(true);
                        fileModel.setContent(downloadedStream);
                        manageObservable(fileModel.save(fileModel).toObservable());
                        sendToView(view -> view.onSetMdText(downloadedStream, htmlUrl == null ? url : htmlUrl, true));
                    });
                    return;
                }
                fileModel.setMarkdown(false);
                sendToView(view -> view.onSetCode(downloadedStream));
            }
            manageObservable(fileModel.save(fileModel).toObservable());
        });
    }

    @Override public boolean isRepo() {
        return isRepo;
    }

    @Override public boolean isImage() {
        return isImage;
    }

    @NonNull @Override public String url() {
        return url;
    }
}
