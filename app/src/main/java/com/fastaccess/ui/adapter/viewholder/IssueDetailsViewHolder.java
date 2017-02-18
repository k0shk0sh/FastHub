package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.IssueEventAdapterModel;
import com.fastaccess.data.dao.IssueModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:03 AM
 */

public class IssueDetailsViewHolder extends BaseViewHolder<IssueEventAdapterModel> {

    @BindView(R.id.avatarView) AvatarLayout avatarView;
    @BindView(R.id.name) FontTextView name;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.description) FontTextView description;

    private IssueDetailsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static IssueDetailsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new IssueDetailsViewHolder(getView(viewGroup, R.layout.issue_detail_header_row_item), adapter);
    }

    @Override public void bind(@NonNull IssueEventAdapterModel model) {
        IssueModel issueModel = model.getIssueModel();
        avatarView.setUrl(issueModel.getUser().getAvatarUrl(), issueModel.getUser().getLogin());
        name.setText(issueModel.getUser().getLogin());
        date.setText(ParseDateFormat.getTimeAgo(issueModel.getCreatedAt()));
        description.setNestedScrollingEnabled(false);
        if (!InputHelper.isEmpty(issueModel.getBody())) {
            MarkDownProvider.setMdText(description, issueModel.getBody());
        }
    }
}
