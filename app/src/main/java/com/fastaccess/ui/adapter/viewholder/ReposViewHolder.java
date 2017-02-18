package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.RepoModel;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.text.NumberFormat;

import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class ReposViewHolder extends BaseViewHolder<RepoModel> {

    @BindView(R.id.title) FontTextView title;
    @BindView(R.id.date) FontTextView date;
    @BindView(R.id.stars) FontTextView stars;
    @BindView(R.id.forks) FontTextView forks;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindString(R.string.forked) String forked;

    private ReposViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static ReposViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new ReposViewHolder(getView(viewGroup, R.layout.repos_row_item), adapter);
    }

    public void bind(@NonNull RepoModel repo, boolean isStarred, boolean withImage) {
        if (repo.isFork()) {
            title.setText(SpannableBuilder.builder().bold(forked).append(" ").append(repo.getName()));
        } else {
            title.setText(!isStarred ? repo.getName() : repo.getFullName());
        }
        if (withImage) {
            String avatar = repo.getOwner() != null ? repo.getOwner().getAvatarUrl() : null;
            String login = repo.getOwner() != null ? repo.getOwner().getLogin() : null;
            avatarLayout.setVisibility(View.VISIBLE);
            avatarLayout.setUrl(avatar, login);
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        stars.setText(numberFormat.format(repo.getStargazersCount()));
        forks.setText(numberFormat.format(repo.getForks()));
        date.setText(ParseDateFormat.getTimeAgo(repo.getUpdatedAt()));
    }

    @Override public void bind(@NonNull RepoModel repo) {}
}
