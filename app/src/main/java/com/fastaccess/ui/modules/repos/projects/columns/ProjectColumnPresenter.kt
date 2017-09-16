package com.fastaccess.ui.modules.repos.projects.columns

import android.view.View
import android.widget.PopupMenu
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.Logger
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.rest.RestProvider
import com.fastaccess.provider.scheme.SchemeParser
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import java.util.*

/**
 * Created by Hashemsergani on 11.09.17.
 */

class ProjectColumnPresenter : BasePresenter<ProjectColumnMvp.View>(), ProjectColumnMvp.Presenter {

    private val projects = ArrayList<ProjectCardModel>()
    private var page: Int = 0
    private var previousTotal: Int = 0
    private var lastPage = Integer.MAX_VALUE

    override fun onItemClick(position: Int, v: View, item: ProjectCardModel) {
        if (v.id == R.id.editCard) {
            view?.let {
                val popupMenu = PopupMenu(v.context, v)
                popupMenu.inflate(R.menu.project_card_menu)
                popupMenu.menu.findItem(R.id.share).isVisible = !item.contentUrl.isNullOrBlank()
                popupMenu.menu.findItem(R.id.copy).isVisible = !item.contentUrl.isNullOrBlank()
                popupMenu.menu.findItem(R.id.edit).isVisible = it.isOwner() && !item.note.isNullOrBlank()
                popupMenu.menu.findItem(R.id.delete).isVisible = it.isOwner() && !item.note.isNullOrBlank()
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> sendToView { it.onEditCard(item.note, position) }
                        R.id.delete -> sendToView { it.onDeleteCard(position) }
                        R.id.share -> if (!item.contentUrl.isNullOrBlank()) {
                            ActivityHelper.shareUrl(v.context, item.contentUrl)
                        }
                        R.id.copy -> if (!item.contentUrl.isNullOrBlank()) {
                            AppHelper.copyToClipboard(v.context, item.contentUrl)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
        } else {
            if (!item.contentUrl.isNullOrBlank()) {
                SchemeParser.launchUri(v.context, item.contentUrl)
            }
        }
    }

    override fun onItemLongClick(position: Int, v: View?, item: ProjectCardModel?) {}

    override fun getCards(): ArrayList<ProjectCardModel> = projects

    override fun getCurrentPage(): Int = page

    override fun getPreviousTotal(): Int = previousTotal

    override fun setCurrentPage(page: Int) {
        this.page = page
    }

    override fun setPreviousTotal(previousTotal: Int) {
        this.previousTotal = previousTotal
    }

    override fun onCallApi(page: Int, parameter: Long?): Boolean {
        if (page == 1) {
            lastPage = Integer.MAX_VALUE
            sendToView { view -> view.getLoadMore().reset() }
        }
        if (page > lastPage || lastPage == 0) {
            sendToView({ it.hideProgress() })
            return false
        }
        currentPage = page
        makeRestCall(RestProvider.getProjectsService(isEnterprise).getProjectCards(parameter!!, page),
                { response ->
                    lastPage = response.last
                    Logger.e(response.items as List<Any>?)
                    sendToView({ it.onNotifyAdapter(response.items, page) })
                })
        return true
    }

    override fun onEditOrDeleteColumn(text: String?, column: ProjectColumnModel) {
        if (text.isNullOrBlank()) {
            manageDisposable(RxHelper.getObservable(RestProvider.getProjectsService(isEnterprise).deleteColumn(column.id))
                    .doOnSubscribe {
                        showBlockingProgress()
                    }
                    .subscribe({
                        if (it.code() == 204) {
                            sendToView { it.deleteColumn() }
                        } else {
                            sendToView { it.showMessage(R.string.error, R.string.network_error) }
                        }
                    }, {
                        hideBlockingProgress()
                        onError(it)
                    }))
        } else {
            val body = ProjectColumnModel()
            body.name = text
            manageDisposable(RxHelper.getObservable(RestProvider.getProjectsService(isEnterprise).updateColumn(column.id, body))
                    .doOnSubscribe {
                        showBlockingProgress()
                    }
                    .subscribe({
                        hideBlockingProgress()
                    }, {
                        hideBlockingProgress()
                        onError(it)
                    }))
        }
    }

    override fun onDeleteCard(position: Int, card: ProjectCardModel) {
        manageDisposable(RxHelper.getObservable(RestProvider.getProjectsService(isEnterprise).deleteCard(card.id.toLong()))
                .doOnSubscribe {
                    showBlockingProgress()
                }
                .subscribe({
                    if (it.code() == 204) {
                        sendToView { it.onRemoveCard(position) }
                    } else {
                        sendToView { it.showMessage(R.string.error, R.string.network_error) }
                    }
                }, {
                    hideBlockingProgress()
                    onError(it)
                }))
    }

    override fun createCard(text: String, columnId: Long) {
        val body = ProjectCardModel()
        body.note = text
        manageDisposable(RxHelper.getObservable(RestProvider.getProjectsService(isEnterprise).createCard(columnId, body))
                .doOnSubscribe {
                    showBlockingProgress()
                }
                .subscribe({ response ->
                    sendToView { it.addCard(response) }
                }, {
                    hideBlockingProgress()
                    onError(it)
                }))
    }

    override fun editCard(text: String, card: ProjectCardModel, position: Int) {
        val body = ProjectCardModel()
        body.note = text
        manageDisposable(RxHelper.getObservable(RestProvider.getProjectsService(isEnterprise).updateCard(card.id.toLong(), body))
                .doOnSubscribe {
                    showBlockingProgress()
                }
                .subscribe({ response ->
                    sendToView { it.updateCard(response, position) }
                }, {
                    hideBlockingProgress()
                    onError(it)
                }))
    }

    private fun showBlockingProgress() {
        sendToView({ v -> v.showBlockingProgress() })
    }

    private fun hideBlockingProgress() {
        sendToView({ v -> v.hideBlockingProgress() })
    }
}