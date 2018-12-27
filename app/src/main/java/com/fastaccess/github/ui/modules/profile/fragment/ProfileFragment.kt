package com.fastaccess.github.ui.modules.profile.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.fastaccess.data.model.FragmentType
import com.fastaccess.data.model.ViewPagerModel
import com.fastaccess.data.persistence.models.UserModel
import com.fastaccess.data.repository.LoginRepositoryProvider
import com.fastaccess.data.repository.isMe
import com.fastaccess.github.R
import com.fastaccess.github.base.BaseFragment
import com.fastaccess.github.base.BasePagerFragment
import com.fastaccess.github.base.BaseViewModel
import com.fastaccess.github.extensions.isTrue
import com.fastaccess.github.extensions.observeNotNull
import com.fastaccess.github.extensions.observeNull
import com.fastaccess.github.extensions.timeAgo
import com.fastaccess.github.ui.adapter.PagerAdapter
import com.fastaccess.github.ui.adapter.ProfileOrgsAdapter
import com.fastaccess.github.ui.adapter.ProfilePinnedReposAdapter
import com.fastaccess.github.ui.modules.profile.feeds.ProfileFeedFragment
import com.fastaccess.github.ui.modules.profile.followersandfollowings.ProfileFollowersFragment
import com.fastaccess.github.ui.modules.profile.fragment.viewmodel.ProfileViewModel
import com.fastaccess.github.ui.modules.profile.gists.ProfileGistsFragment
import com.fastaccess.github.ui.modules.profile.repos.ProfileReposFragment
import com.fastaccess.github.ui.modules.profile.starred.ProfileStarredReposFragment
import com.fastaccess.github.ui.widget.AnchorSheetBehavior
import com.fastaccess.github.ui.widget.recyclerview.lm.SafeGridLayoutManager
import com.fastaccess.github.utils.EXTRA
import com.fastaccess.github.utils.EXTRA_TWO
import com.fastaccess.github.utils.extensions.addDivider
import com.fastaccess.github.utils.extensions.getDrawable
import com.fastaccess.github.utils.extensions.setBottomSheetCallback
import com.github.zagum.expandicon.ExpandIconView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.appbar_center_title_layout_bottomsheet.*
import kotlinx.android.synthetic.main.profile_bottom_sheet.*
import kotlinx.android.synthetic.main.profile_fragment_layout.*
import javax.inject.Inject

/**
 * Created by Kosh on 18.08.18.
 */
class ProfileFragment : BasePagerFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var loginRepositoryProvider: LoginRepositoryProvider

    private val viewModel by lazy { ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java) }
    private val behaviour by lazy { AnchorSheetBehavior.from(bottomSheet) }
    private val adapter by lazy {
        PagerAdapter(childFragmentManager, arrayListOf(
            ViewPagerModel(getString(R.string.feeds), ProfileFeedFragment.newInstance(loginBundle), FragmentType.FEEDS),
            ViewPagerModel(getString(R.string.repos), ProfileReposFragment.newInstance(loginBundle), FragmentType.REPOS),
            ViewPagerModel(getString(R.string.starred), ProfileStarredReposFragment.newInstance(loginBundle), FragmentType.STARRED),
            ViewPagerModel(getString(R.string.gists), ProfileGistsFragment.newInstance(loginBundle), FragmentType.GISTS),
            ViewPagerModel(getString(R.string.followers), ProfileFollowersFragment.newInstance(loginBundle, true), FragmentType.FOLLOWERS),
            ViewPagerModel(getString(R.string.following), ProfileFollowersFragment.newInstance(loginBundle, false), FragmentType.FOLLOWINGS)
        ))
    }
    private val pinnedReposAdapter by lazy { ProfilePinnedReposAdapter(arrayListOf()) }
    private val orgsAdapter by lazy { ProfileOrgsAdapter(arrayListOf()) }
    private val loginBundle: String by lazy { arguments?.getString(EXTRA) ?: "" }
    private val tabBundle: String? by lazy { arguments?.getString(EXTRA_TWO) }

    override fun viewModel(): BaseViewModel? = viewModel
    override fun layoutRes(): Int = R.layout.profile_fragment_layout
    override fun onFragmentCreatedWithUser(view: View, savedInstanceState: Bundle?) {
        setupToolbar(R.string.profile)
        username.text = loginBundle
        toolbar.navigationIcon = getDrawable(R.drawable.ic_back)
        swipeRefresh.setOnRefreshListener {

            viewModel.getUserFromRemote(loginBundle)
        }

        (organizationList.layoutManager as SafeGridLayoutManager).setIconSize(resources.getDimensionPixelSize(R.dimen.header_icon_zie))
        organizationList.adapter = orgsAdapter
        pinnedList.adapter = pinnedReposAdapter
        pinnedList.addDivider()

        observeChanges()

        behaviour.state = AnchorSheetBehavior.STATE_ANCHOR
        behaviour.setBottomSheetCallback({ newState ->
            when (newState) {
                AnchorSheetBehavior.STATE_EXPANDED -> toggleArrow.setState(ExpandIconView.MORE, true)
                AnchorSheetBehavior.STATE_COLLAPSED -> toggleArrow.setState(ExpandIconView.LESS, true)
                else -> toggleArrow.setFraction(0.5f, false)
            }
        })


        toggleArrow.setOnClickListener {
            if (behaviour.state != AnchorSheetBehavior.STATE_EXPANDED) {
                behaviour.state = AnchorSheetBehavior.STATE_EXPANDED
            } else {
                behaviour.state = AnchorSheetBehavior.STATE_COLLAPSED
            }
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) = expandBottomSheet()
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(p0: TabLayout.Tab?) = expandBottomSheet()

            private fun expandBottomSheet() {
                if (viewModel.isFirstLaunch) {
                    viewModel.isFirstLaunch = false
                    return
                }
                (behaviour.state != AnchorSheetBehavior.STATE_EXPANDED).isTrue {
                    behaviour.state = AnchorSheetBehavior.STATE_EXPANDED
                }
            }
        })
        scrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            (behaviour.state != AnchorSheetBehavior.STATE_COLLAPSED).isTrue {
                behaviour.state = AnchorSheetBehavior.STATE_COLLAPSED
            }
        }
        followers.setOnClickListener { if (pager.adapter != null) selectTab(FragmentType.FOLLOWERS) }
        following.setOnClickListener { if (pager.adapter != null) selectTab(FragmentType.FOLLOWINGS) }
    }

    override fun onPageSelected(page: Int) = (pager.adapter?.instantiateItem(pager, page) as? BaseFragment)?.onScrollToTop() ?: Unit

    private fun observeChanges() {
        viewModel.getUser(loginBundle).observeNull(this) { user ->
            if (user == null) {
                viewModel.getUserFromRemote(loginBundle)
            } else {
                initUI(user)
            }
        }

        viewModel.isBlocked.observeNotNull(this) {
            blockBtn.isEnabled = true
            blockBtn.text = if (it) getString(R.string.unblock) else getString(R.string.block)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI(user: UserModel) {
        addDisposal(loginRepositoryProvider.isMe(loginBundle) { isMe ->
            actionsHolder.isVisible = !isMe
            blockBtn.isVisible = !isMe
            blockBtn.isEnabled = false
            if (!isMe) viewModel.checkBlockingState(loginBundle)
        })
        following.text = "${getString(R.string.following)}: ${user.following?.totalCount ?: 0}"
        followers.text = "${getString(R.string.followers)}: ${user.followers?.totalCount ?: 0}"
        swipeRefresh.isRefreshing = false
        followBtn.setText(if (user.viewerIsFollowing == true) R.string.unfollow else R.string.follow)
        followBtn.setOnClickListener { viewModel.followUnfollowUser(loginBundle, user.viewerIsFollowing) }
        description.isVisible = user.bio?.isNotEmpty() == true
        description.text = user.bio ?: ""
        email.isVisible = user.email?.isNotEmpty() == true
        email.text = user.email
        company.isVisible = user.company?.isNotEmpty() == true
        company.text = user.company ?: ""
        joined.text = user.createdAt?.timeAgo() ?: ""
        joined.isVisible = true
        location.isVisible = user.location?.isNotEmpty() == true
        location.text = user.location ?: ""
        name.isVisible = user.name?.isNotEmpty() == true
        name.text = user.name ?: ""
        developerProgram.isVisible = user.isDeveloperProgramMember == true
        user.organizations?.nodes?.let { orgs ->
            organizationHolder.isVisible = orgs.isNotEmpty()
            orgsAdapter.insertNew(orgs)
        }
        user.pinnedRepositories?.pinnedRepositories?.let { nodes ->
            pinnedHolder.isVisible = nodes.isNotEmpty()
            pinnedReposAdapter.insertNew(nodes)
        }
        blockBtn.setOnClickListener { viewModel.blockUnblockUser(loginBundle) }
        userImageView.loadAvatar(user.avatarUrl)

        if (pager.adapter == null) {
            pager.offscreenPageLimit = 5
            pager.adapter = adapter
            tabs.setupWithViewPager(pager)
            val type = FragmentType.getTypeSafely(tabBundle ?: "")
            selectTab(type)
        }
    }

    private fun selectTab(type: FragmentType?) {
        type?.let {
            val index = adapter.getIndex(it)
            if (index == -1) return
            pager.currentItem = index
        }
    }

    companion object {
        fun newInstance(login: String, tab: String? = null) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA, login)
                putString(EXTRA_TWO, tab)
            }
        }
    }
}