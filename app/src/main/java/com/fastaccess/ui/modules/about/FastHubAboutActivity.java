package com.fastaccess.ui.modules.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.provider.tasks.version.CheckVersionService;
import com.fastaccess.provider.theme.ThemeEngine;
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog;
import com.fastaccess.ui.modules.main.donation.DonationActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;
import com.fastaccess.ui.modules.user.UserPagerActivity;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import es.dmoral.toasty.Toasty;

/**
 * Created by danielstone on 12 Mar 2017, 1:57 AM
 */
public class FastHubAboutActivity extends MaterialAboutActivity {

    private View malRecyclerview;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeEngine.INSTANCE.applyForAbout(this);
        super.onCreate(savedInstanceState);
        malRecyclerview = findViewById(R.id.mal_recyclerview);
    }

    @NonNull @Override protected MaterialAboutList getMaterialAboutList(@NonNull Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
        buildApp(context, appCardBuilder);
        MaterialAboutCard.Builder miscCardBuilder = new MaterialAboutCard.Builder();
        buildMisc(context, miscCardBuilder);
        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        buildAuthor(context, authorCardBuilder);
        MaterialAboutCard.Builder newLogoAuthor = new MaterialAboutCard.Builder();
        MaterialAboutCard.Builder logoAuthor = new MaterialAboutCard.Builder();
        buildLogo(context, newLogoAuthor, logoAuthor);
        return new MaterialAboutList(appCardBuilder.build(), miscCardBuilder.build(), authorCardBuilder.build(),
                newLogoAuthor.build(), logoAuthor.build());
    }

    @Override protected CharSequence getActivityTitle() {
        return getString(R.string.app_name);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            Toasty.success(App.getInstance(), getString(R.string.thank_you_for_feedback), Toast.LENGTH_SHORT).show();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return false;//override
    }

    private void buildLogo(Context context, MaterialAboutCard.Builder newLogoAuthor, MaterialAboutCard.Builder logoAuthor) {
        newLogoAuthor.title(getString(R.string.logo_designer, "Cookicons"));
        newLogoAuthor.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.google_plus)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "https://plus.google.com/+CookiconsDesign"))
                .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.twitter)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                        .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "https://twitter.com/mcookie"))
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.website)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_brower))
                        .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "https://cookicons.co/"))
                        .build());

        logoAuthor.title(String.format("Old %s", getString(R.string.logo_designer, "Kevin Aguilar")));
        logoAuthor.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.google_plus)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "https://plus.google.com/+KevinAguilarC"))
                .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.twitter)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                        .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "https://twitter.com/kevttob"))
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.website)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_brower))
                        .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "http://kevaguilar.com/"))
                        .build());
    }

    private void buildAuthor(Context context, MaterialAboutCard.Builder authorCardBuilder) {
        authorCardBuilder.title(R.string.author);
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Kosh Sergani")
                .subText("k0shk0sh")
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                .setOnClickAction(() -> UserPagerActivity.startActivity(context, "k0shk0sh", false, false,0))
                .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.fork_github)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                        .setOnClickAction(() -> startActivity(RepoPagerActivity.createIntent(this, "FastHub", "k0shk0sh")))
                        .build())
                .addItem(ConvenienceBuilder.createEmailItem(context, ContextCompat.getDrawable(context, R.drawable.ic_email),
                        getString(R.string.send_email), true, getString(R.string.email_address), getString(R.string.question_concerning_fasthub)));
    }

    private void buildMisc(Context context, MaterialAboutCard.Builder miscCardBuilder) {
        miscCardBuilder.title(R.string.about)
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.support_development)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_heart))
                        .setOnClickAction(() -> startActivity(new Intent(context, DonationActivity.class)))
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.changelog)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_track_changes))
                        .setOnClickAction(() -> new ChangelogBottomSheetDialog().show(getSupportFragmentManager(), "ChangelogBottomSheetDialog"))
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.join_slack)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_slack))
                        .setOnClickAction(() -> ActivityHelper.startCustomTab(this, "http://rebrand.ly/fasthub"))
                        .build())
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.open_source_libs)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                        .setOnClickAction(() -> new LibsBuilder()
                                .withActivityStyle(AppHelper.isNightMode(getResources()) ? Libs.ActivityStyle.DARK : Libs.ActivityStyle.LIGHT)
                                .withAutoDetect(true)
                                .withActivityTitle(this.getResources().getString(R.string.open_source_libs))
                                .withAboutIconShown(true)
                                .withAboutVersionShown(true)
                                .start(this))
                        .build());
    }

    private void buildApp(Context context, MaterialAboutCard.Builder appCardBuilder) {
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(getString(R.string.version))
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_update))
                .subText(BuildConfig.VERSION_NAME)
                .setOnClickAction(() -> startService(new Intent(this, CheckVersionService.class)))
                .build())
                .addItem(ConvenienceBuilder.createRateActionItem(context, ContextCompat.getDrawable(context, R.drawable.ic_star_filled),
                        getString(R.string.rate_app), null))
                .addItem(new MaterialAboutActionItem.Builder()
                        .text(R.string.report_issue)
                        .subText(R.string.report_issue_here)
                        .icon(ContextCompat.getDrawable(context, R.drawable.ic_bug))
                        .setOnClickAction(() -> CreateIssueActivity.startForResult(this, CreateIssueActivity.startForResult(this), malRecyclerview))
                        .build());
    }
}
