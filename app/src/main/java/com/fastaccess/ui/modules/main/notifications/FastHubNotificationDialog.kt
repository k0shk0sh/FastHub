package com.fastaccess.ui.modules.main.notifications

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.text.Html
import android.view.View
import butterknife.OnClick
import com.fastaccess.R
import com.fastaccess.data.dao.model.AbstractFastHubNotification.NotificationType
import com.fastaccess.data.dao.model.FastHubNotification
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import kotlinx.android.synthetic.main.dialog_guide_layout.*

/**
 * Created by Kosh on 17.11.17.
 */
class FastHubNotificationDialog : BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    init {
        suppressAnimation = true
        isCancelable = false
    }

    private val model by lazy { arguments?.getParcelable<FastHubNotification>(BundleConstant.ITEM) }

    @OnClick(R.id.cancel) fun onCancel() {
        dismiss()
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        model?.let {
            title?.text = it.title
            description?.text = Html.fromHtml(it.body)
            it.isRead = true
            FastHubNotification.update(it)
        } ?: dismiss()
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.dialog_guide_layout

    companion object {
        @JvmStatic private val TAG = FastHubNotificationDialog::class.java.simpleName

        fun newInstance(model: FastHubNotification): FastHubNotificationDialog {
            val fragment = FastHubNotificationDialog()
            fragment.arguments = Bundler.start()
                    .put(BundleConstant.ITEM, model)
                    .end()
            return fragment
        }

        fun show(fragmentManager: FragmentManager, model: FastHubNotification? = null) {
            val notification = model ?: FastHubNotification.getLatest()
            notification?.let {
                if (it.type == NotificationType.PROMOTION || it.type == NotificationType.PURCHASE && model == null) {
                    if (PrefGetter.isProEnabled()) {
                        it.isRead = true
                        FastHubNotification.update(it)
                        return
                    }
                }
                newInstance(it).show(fragmentManager, TAG)
            }
        }

        fun show(fragmentManager: FragmentManager) {
            show(fragmentManager, null)
        }
    }
}