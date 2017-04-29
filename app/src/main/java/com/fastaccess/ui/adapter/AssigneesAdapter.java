package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.model.User;
import com.fastaccess.ui.adapter.viewholder.AssigneesViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */

public class AssigneesAdapter extends BaseRecyclerAdapter<User, AssigneesViewHolder, BaseViewHolder.OnItemClickListener<User>> {

    public interface OnSelectAssignee {
        boolean isAssigneeSelected(int position);

        void onToggleSelection(int position, boolean select);
    }

    private final OnSelectAssignee onSelectAssignee;

    public AssigneesAdapter(@NonNull List<User> data, @Nullable OnSelectAssignee onSelectAssignee) {
        super(data);
        this.onSelectAssignee = onSelectAssignee;
    }

    @Override protected AssigneesViewHolder viewHolder(ViewGroup parent, int viewType) {
        return AssigneesViewHolder.newInstance(parent, onSelectAssignee, this);
    }

    @Override protected void onBindView(AssigneesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }
}
