package com.fastaccess.ui.modules.main.donation;

import com.fastaccess.ui.base.mvp.BaseMvp;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

/**
 * Created by Kosh on 24 Mar 2017, 9:16 PM
 */

public interface DonationMvp {

    interface View extends BaseMvp.FAView, BaseViewHolder.OnItemClickListener<String> {}

    interface Presenter {}
}
