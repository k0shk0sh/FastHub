package com.fastaccess.ui.modules.repos.code.prettifier;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.ViewerFile;
import com.fastaccess.data.dao.MarkdownModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 27 Nov 2016, 3:43 PM
 */

class ViewerPresenter extends BasePresenter<ViewerMvp.View> implements ViewerMvp.Presenter {
    private String downloadedStream;
    private boolean isMarkdown;
    private boolean isRepo;
    private boolean isImage;
    private String url;

    @Override public void onError(@NonNull Throwable throwable) {
        throwable.printStackTrace();
        int code = RestProvider.getErrorCode(throwable);
        if (code == 404) {
            sendToView(view -> view.onShowError(isRepo ? R.string.no_readme_found : R.string.no_file_found));
        } else {
            if (code == 406) {
                sendToView(view -> view.openUrl(url));
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

    @Override public String downloadedStream() {
        return downloadedStream;
    }

    @Override public boolean isMarkDown() {
        return isMarkdown;
    }

    @Override public void onWorkOffline() {
        if (downloadedStream == null) {
            manageSubscription(RxHelper.getObserver(ViewerFile.get(url))
                    .subscribe(fileModel -> {
                        if (fileModel != null) {
                            isImage = MarkDownProvider.isImage(fileModel.getFullUrl());
                            if (isImage) {
                                sendToView(view -> view.onSetImageUrl(fileModel.getFullUrl()));
                            } else {
                                downloadedStream = fileModel.getContent();
                                isRepo = fileModel.isRepo();
                                isMarkdown = fileModel.isMarkdown();
                                sendToView(view -> {
                                    if (isRepo) {
                                        view.onSetMdText(downloadedStream, fileModel.getFullUrl());
                                    } else if (isMarkdown) {
                                        view.onSetMdText(downloadedStream, null);
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
            sendToView(view -> view.onSetImageUrl(url));
            return;
        }
        makeRestCall(isRepo ? RestProvider.getRepoService().getReadmeHtml(url) : RestProvider.getRepoService().getFileAsStream(url),
                content -> {
                    downloadedStream = content;
                    ViewerFile fileModel = new ViewerFile();
                    fileModel.setContent(downloadedStream);
                    fileModel.setFullUrl(url);
                    fileModel.setRepo(isRepo);
                    if (isRepo) {
                        fileModel.setMarkdown(true);
                        isMarkdown = true;
                        isRepo = true;
                        sendToView(view -> view.onSetMdText(downloadedStream, url));
                    } else {
                        isMarkdown = MarkDownProvider.isMarkdown(url);
                        if (isMarkdown) {
                            MarkdownModel model = new MarkdownModel();
                            model.setText(downloadedStream);
                            makeRestCall(RestProvider.getRepoService().convertReadmeToHtml(model),
                                    s -> {
                                        isMarkdown = true;
                                        downloadedStream = s;
                                        fileModel.setMarkdown(true);
                                        fileModel.setContent(downloadedStream);
                                        manageSubscription(fileModel.save(fileModel).subscribe());
                                        sendToView(view -> view.onSetMdText(downloadedStream, url));
                                    });
                            return;
                        }
                        fileModel.setMarkdown(false);
                        if (isMarkdown) {
                            sendToView(view -> view.onSetMdText(downloadedStream, null));
                        } else {
                            sendToView(view -> view.onSetCode(downloadedStream));
                        }
                    }
                    manageSubscription(fileModel.save(fileModel).subscribe());
                });
    }

    @Override public boolean isRepo() {
        return isRepo;
    }

    @Override public boolean isImage() {
        return isImage;
    }
}
