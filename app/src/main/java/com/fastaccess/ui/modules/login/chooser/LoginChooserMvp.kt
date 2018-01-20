package com.fastaccess.ui.modules.login.chooser

import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.modules.settings.LanguageBottomSheetDialog
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder

interface LoginChooserMvp {

    interface View : BaseMvp.FAView, LanguageBottomSheetDialog.LanguageDialogListener,
            BaseViewHolder.OnItemClickListener<Login> {
        fun onAccountsLoaded(accounts: List<Login>?)
    }
}