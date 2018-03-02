package com.fastaccess.ui.modules.user;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.evernote.android.state.State;
import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.data.dao.TabsCountStateModel;
import com.fastaccess.data.dao.model.Login;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.profile.org.repos.OrgReposFragment;
import com.fastaccess.ui.modules.profile.repos.ProfileReposFragment;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.ViewPagerView;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.OnClick;
import shortbread.Shortcut;

/**
 * Created by Kosh on 03 Dec 2016, 8:00 AM
 */

@Shortcut(id = "profile", icon = R.drawable.ic_app_shortcut_profile, shortLabelRes = R.string.profile, backStack = {MainActivity.class}, rank = 4)
public class UserPagerActivity extends BaseActivity<UserPagerMvp.View, UserPagerPresenter> implements UserPagerMvp.View {

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.tabbedPager) ViewPagerView pager;
    @BindView(R.id.fab) FloatingActionButton fab;
    @State int index;
    @State String login;
    @State boolean isOrg;
    @State HashSet<TabsCountStateModel> counts = new HashSet<>();

    public static void startActivity(@NonNull Context context, @NonNull String login, boolean isOrg,
                                     boolean isEnterprise, int index) {
        context.startActivity(createIntent(context, login, isOrg, isEnterprise, index));
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String login) {
        return createIntent(context, login, false);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String login, boolean isOrg) {
        return createIntent(context, login, isOrg, false, -1);
    }

    public static Intent createIntent(@NonNull Context context, @NonNull String login, boolean isOrg,
                                      boolean isEnterprise, int index) {
        Intent intent = new Intent(context, UserPagerActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.EXTRA, login)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .put(BundleConstant.EXTRA_TYPE, isOrg)
                .put(BundleConstant.EXTRA_TWO, index)
                .end());
        if (context instanceof Service || context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override protected int layout() {
        return R.layout.tabbed_pager_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override protected boolean isSecured() {
        return false;
    }

    @NonNull @Override public UserPagerPresenter providePresenter() {
        return new UserPagerPresenter();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Login currentUser = Login.getUser();
        if (currentUser == null) {
            onRequireLogin();
            return;
        }
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                login = getIntent().getExtras().getString(BundleConstant.EXTRA);
                isOrg = getIntent().getExtras().getBoolean(BundleConstant.EXTRA_TYPE);
                index = getIntent().getExtras().getInt(BundleConstant.EXTRA_TWO, -1);
                if (!InputHelper.isEmpty(login)) {
                    if (isOrg) {
                        getPresenter().checkOrgMembership(login);
                    } else {
                        if (!Login.getUser().getLogin().equalsIgnoreCase(login)) getPresenter().onCheckBlocking(login);
                    }
                }
            } else {
                Login user = Login.getUser();
                if (user == null) {
                    onRequireLogin();
                    return;
                }
                login = user.getLogin();
            }
        }
        if (InputHelper.isEmpty(login)) {
            finish();
            return;
        }
        setTaskName(login);
        setTitle(login);
        if (login.equalsIgnoreCase(currentUser.getLogin())) {
            selectProfile();
        }
        if (!isOrg) {
            FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getSupportFragmentManager(),
                    FragmentPagerAdapterModel.buildForProfile(this, login));
            pager.setAdapter(adapter);
            tabs.setTabGravity(TabLayout.GRAVITY_FILL);
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabs.setupWithViewPager(pager);
            if (savedInstanceState == null) {
                if (index != -1) {
                    pager.setCurrentItem(index);
                }
            }
        } else {
            if (getPresenter().getIsMember() == -1) {
                getPresenter().checkOrgMembership(login);
            } else {
                onInitOrg(getPresenter().isMember == 1);
            }
        }
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager) {
            @Override public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onScrollTop(tab.getPosition());
            }
        });
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override public void onPageSelected(int position) {
                super.onPageSelected(position);
                hideShowFab(position);
            }
        });
        if (!isOrg) {
            if (savedInstanceState != null && !counts.isEmpty()) {
                Stream.of(counts).forEach(this::updateCount);
            }
        }
        hideShowFab(pager.getCurrentItem());
    }

    @Override public void onScrollTop(int index) {
        if (pager == null || pager.getAdapter() == null) return;
        Fragment fragment = (BaseFragment) pager.getAdapter().instantiateItem(pager, index);
        if (fragment instanceof BaseFragment) {
            ((BaseFragment) fragment).onScrollTop(index);
        }
    }

    @Override public void hideProgress() {
        super.hideProgress();
    }

    @Override public void onNavigateToFollowers() {
        pager.setCurrentItem(5);
    }

    @Override public void onNavigateToFollowing() {
        pager.setCurrentItem(6);
    }

    @Override public void onInitOrg(boolean isMember) {
        hideProgress();
        FragmentsPagerAdapter adapter = new FragmentsPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapterModel.buildForOrg(this, login, isMember));
        pager.setAdapter(adapter);
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabs.setupWithViewPager(pager);
        setTaskName(login);
    }

    @Override public void onUserBlocked() {
        showMessage(R.string.success, R.string.user_blocked);
        onInvalidateMenu();
    }

    @Override public void onInvalidateMenu() {
        hideProgress();
        supportInvalidateOptionsMenu();
    }

    @Override public void onUserUnBlocked() {
        showMessage(R.string.success, R.string.user_unblocked);
        onInvalidateMenu();
    }

    @Override public void onCheckType(boolean isOrg) {
        if (!this.isOrg == isOrg) {
            startActivity(this, login, isOrg, isEnterprise(), index);
            finish();
        }
    }

    @Override public void onSetBadge(int tabIndex, int count) {
        TabsCountStateModel model = new TabsCountStateModel();
        model.setTabIndex(tabIndex);
        model.setCount(count);
        counts.add(model);
        if (tabs != null) {
            updateCount(model);
        }
    }

    @OnClick(R.id.fab) public void onRepoFilterClicked() {
        if (isOrg) {
            int position;
            if (getPresenter().getIsMember() == 1) {
                position = 2;
            } else {
                position = 1;
            }
            OrgReposFragment fragment = ((OrgReposFragment) pager.getAdapter().instantiateItem(pager, position));
            fragment.onRepoFilterClicked();
        } else {
            ProfileReposFragment fragment = ((ProfileReposFragment) pager.getAdapter().instantiateItem(pager, 2));
            fragment.onRepoFilterClicked();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share && !InputHelper.isEmpty(login)) {
            ActivityHelper.shareUrl(this, new Uri.Builder().scheme("https")
                    .authority(LinkParserHelper.HOST_DEFAULT)
                    .appendPath(login)
                    .toString());
            return true;
        } else if (item.getItemId() == R.id.block && !InputHelper.isEmpty(login)) {
            getPresenter().onBlockUser(login);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        Logger.e(getPresenter().isUserBlockedRequested(), getPresenter().isUserBlocked());
        if (getPresenter().isUserBlockedRequested()) {
            Login login = Login.getUser();
            if (login != null && !isOrg) {
                String username = login.getLogin();
                if (!username.equalsIgnoreCase(this.login)) {
                    menu.findItem(R.id.block)
                            .setIcon(getPresenter().isUserBlocked() ? R.drawable.ic_unlock : R.drawable.ic_lock)
                            .setTitle(getPresenter().isUserBlocked() ? getString(R.string.unblock) : getString(R.string.block))
                            .setVisible(true);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void hideShowFab(int position) {
        if (isOrg) {
            if (getPresenter().getIsMember() == 1) {
                if (position == 2) {
                    fab.show();
                } else {
                    fab.hide();
                }
            } else {
                if (position == 1) {
                    fab.show();
                } else {
                    fab.hide();
                }
            }
        } else {
            if (position == 2) {
                fab.show();
            } else {
                fab.hide();
            }
        }
    }

    private void updateCount(@NonNull TabsCountStateModel model) {
        TextView tv = ViewHelper.getTabTextView(tabs, model.getTabIndex());
        tv.setText(SpannableBuilder.builder()
                .append(getString(R.string.starred))
                .append("   ")
                .append("(")
                .bold(String.valueOf(model.getCount()))
                .append(")"));
    }
}
