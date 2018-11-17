package com.fastaccess.github.ui.adapter

/**
 * Created by Kosh on 26.08.18.
 */
//class ProfilePinnedRepoCell(private val node: UserPinnedRepoNodesModel) : SimpleCell<UserPinnedRepoNodesModel>(node) {
//    override fun getLayoutRes(): Int = R.layout.profile_pinned_repo_row_item
//
//    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int, context: Context, payload: Any?) {
//        holder.itemView.apply {
//            title.text = node.name
//            star.text = node.stargazers?.totalCount?.formatNumber() ?: "0"
//            forks.text = node.forkCount?.formatNumber() ?: "0"
//            issues.text = node.issues?.totalCount?.formatNumber() ?: "0"
//            pulls.text = node.pullRequests?.totalCount?.formatNumber() ?: "0"
//            language.isVisible = !node.primaryLanguage?.name.isNullOrEmpty()
//            language.text = node.primaryLanguage?.name ?: ""
//            if (!node.primaryLanguage?.color.isNullOrBlank()) {
//                language.chipIconTint = ColorStateList.valueOf(Color.parseColor(node.primaryLanguage?.color)) ?: null
//            }
//        }
//    }
//}