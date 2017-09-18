package com.fastaccess.ui.modules.gists.gist.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupMenu;

import com.fastaccess.R;
import com.fastaccess.data.dao.CommentRequestModel;
import com.fastaccess.data.dao.model.Comment;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.RxHelper;
import com.fastaccess.provider.rest.RestProvider;
import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.ArrayList;

/**
 * Created by Kosh on 11 Nov 2016, 12:36 PM
 */

class GistCommentsPresenter extends BasePresenter<GistCommentsMvp.View> implements GistCommentsMvp.Presenter {
    private ArrayList<Comment> comments = new ArrayList<>();
    private int page;
    private int previousTotal;
    private int lastPage = Integer.MAX_VALUE;

    @Override public int getCurrentPage() {
        return page;
    }

    @Override public int getPreviousTotal() {
        return previousTotal;
    }

    @Override public void setCurrentPage(int page) {
        this.page = page;
    }

    @Override public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    @Override public void onError(@NonNull Throwable throwable) {
        //noinspection ConstantConditions
        sendToView(view -> onWorkOffline(view.getLoadMore().getParameter()));
        super.onError(throwable);
    }

    @Override public boolean onCallApi(int page, @Nullable String parameter) {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE;
            sendToView(view -> view.getLoadMore().reset());
        }
        if (page > lastPage || parameter == null || lastPage == 0) {
            sendToView(GistCommentsMvp.View::hideProgress);
            return false;
        }
        setCurrentPage(page);
        makeRestCall(RestProvider.getGistService(isEnterprise()).getGistComments(parameter, page),
                listResponse -> {
                    lastPage = listResponse.getLast();
                    if (getCurrentPage() == 1) {
                        manageDisposable(Comment.saveForGist(listResponse.getItems(), parameter));
                    }
                    sendToView(view -> view.onNotifyAdapter(listResponse.getItems(), page));
                });
        return true;
    }

    @NonNull @Override public ArrayList<Comment> getComments() {
        return comments;
    }

    @Override public void onHandleDeletion(@Nullable Bundle bundle) {
        if (bundle != null) {
            long commId = bundle.getLong(BundleConstant.EXTRA, 0);
            String gistId = bundle.getString(BundleConstant.ID);
            if (commId != 0 && gistId != null) {
                makeRestCall(RestProvider.getGistService(isEnterprise()).deleteGistComment(gistId, commId),
                        booleanResponse -> sendToView(view -> {
                            if (booleanResponse.code() == 204) {
                                Comment comment = new Comment();
                                comment.setId(commId);
                                view.onRemove(comment);
                            } else {
                                view.showMessage(R.string.error, R.string.error_deleting_comment);
                            }
                        }));
            }
        }
    }

    @Override public void onWorkOffline(@NonNull String gistId) {
        if (comments.isEmpty()) {
            manageDisposable(RxHelper.getObservable(Comment.getGistComments(gistId).toObservable())
                    .subscribe(localComments -> sendToView(view -> view.onNotifyAdapter(localComments, 1))));
        } else {
            sendToView(BaseMvp.FAView::hideProgress);
        }
    }

    @Override public void onHandleComment(@NonNull String text, @Nullable Bundle bundle, String gistId) {
        CommentRequestModel model = new CommentRequestModel();
        model.setBody(text);
        manageDisposable(RxHelper.getObservable(RestProvider.getGistService(isEnterprise()).createGistComment(gistId, model))
                .doOnSubscribe(disposable -> sendToView(view -> view.showBlockingProgress(0)))
                .subscribe(comment -> sendToView(view -> view.onAddNewComment(comment)), throwable -> {
                    onError(throwable);
                    sendToView(GistCommentsMvp.View::hideBlockingProgress);
                }));
    }

    @Override public void onItemClick(int position, View v, Comment item) {
        if (getView() == null) return;
        if (v.getId() == R.id.toggle || v.getId() == R.id.toggleHolder) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.comments_menu);
            String username = Login.getUser().getLogin();
            popupMenu.getMenu().findItem(R.id.delete).setVisible(username.equalsIgnoreCase(item.getUser().getLogin()));
            popupMenu.getMenu().findItem(R.id.edit).setVisible(username.equalsIgnoreCase(item.getUser().getLogin()));
            popupMenu.setOnMenuItemClickListener(item1 -> {
                if (getView() == null) return false;
                if (item1.getItemId() == R.id.delete) {
                    getView().onShowDeleteMsg(item.getId());
                } else if (item1.getItemId() == R.id.reply) {
                    getView().onReply(item.getUser(), item.getBody());
                } else if (item1.getItemId() == R.id.edit) {
                    getView().onEditComment(item);
                }
                return true;
            });
            popupMenu.show();
        }
    }

    @Override public void onItemLongClick(int position, View v, Comment item) {
        if (v.getId() == R.id.toggle) {
            if (getView() != null) getView().onReply(item.getUser(), item.getBody());
        } else {
            if (item.getUser() != null && TextUtils.equals(item.getUser().getLogin(), Login.getUser().getLogin())) {
                if (getView() != null) getView().onShowDeleteMsg(item.getId());
            } else {
                onItemClick(position, v, item);
            }
        }
    }
}
