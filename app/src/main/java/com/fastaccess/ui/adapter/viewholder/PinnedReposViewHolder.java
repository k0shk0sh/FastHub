package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.model.PinnedRepos;
import com.fastaccess.data.dao.model.Repo;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.colors.ColorsProvider;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.LabelSpan;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.text.NumberFormat;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */

public class PinnedReposViewHolder extends BaseViewHolder<PinnedRepos> {

    @BindView(R.id.title) FontTextView title;
    @Nullable @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @Nullable @BindView(R.id.date) FontTextView date;
    @Nullable @BindView(R.id.stars) FontTextView stars;
    @Nullable @BindView(R.id.forks) FontTextView forks;
    @Nullable @BindView(R.id.language) FontTextView language;
    @BindString(R.string.forked) String forked;
    @BindString(R.string.private_repo) String privateRepo;
    @BindColor(R.color.material_indigo_700) int forkColor;
    @BindColor(R.color.material_grey_700) int privateColor;

    private PinnedReposViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
    }

    public static PinnedReposViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter, boolean singleLine) {
        return new PinnedReposViewHolder(getView(viewGroup,
                singleLine ? R.layout.repos_row_item_menu : R.layout.repos_row_item), adapter);
    }

    @Override public void bind(@NonNull PinnedRepos pinnedRepos) {
        Repo repo = pinnedRepos.getPinnedRepo();
        if (repo == null) return;
        if (repo.isFork()) {
            title.setText(SpannableBuilder.builder()
                    .append(" " + forked + " ", new LabelSpan(forkColor))
                    .append(" ")
                    .append(repo.getName(), new LabelSpan(Color.TRANSPARENT)));
        } else if (repo.isPrivateX()) {
            title.setText(SpannableBuilder.builder()
                    .append(" " + privateRepo + " ", new LabelSpan(privateColor))
                    .append(" ")
                    .append(repo.getName(), new LabelSpan(Color.TRANSPARENT)));
        } else {
            title.setText(repo.getFullName());
        }
        String avatar = repo.getOwner() != null ? repo.getOwner().getAvatarUrl() : null;
        String login = repo.getOwner() != null ? repo.getOwner().getLogin() : null;
        boolean isOrg = repo.getOwner() != null && repo.getOwner().isOrganizationType();
        if (avatarLayout != null) {
            avatarLayout.setVisibility(View.VISIBLE);
            avatarLayout.setUrl(avatar, login, isOrg, LinkParserHelper.isEnterprise(repo.getHtmlUrl()));
        }
        if (stars != null && forks != null && date != null && language != null) {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            stars.setText(numberFormat.format(repo.getStargazersCount()));
            forks.setText(numberFormat.format(repo.getForks()));
            date.setText(ParseDateFormat.getTimeAgo(repo.getUpdatedAt()));
            if (!InputHelper.isEmpty(repo.getLanguage())) {
                language.setText(repo.getLanguage());
                language.setTextColor(ColorsProvider.getColorAsColor(repo.getLanguage(), language.getContext()));
                language.setVisibility(View.VISIBLE);
            }
        }
    }
}
