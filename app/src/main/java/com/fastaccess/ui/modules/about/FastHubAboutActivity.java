package com.fastaccess.ui.modules.about;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.widget.Toast;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueView;
import com.fastaccess.ui.modules.user.UserPagerView;

import es.dmoral.toasty.Toasty;

/**
 * Created by danielstone on 12 Mar 2017, 1:57 AM
 */
public class FastHubAboutActivity extends MaterialAboutActivity {
    @Override protected MaterialAboutList getMaterialAboutList(Context context) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();
        try {
            appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(context, ContextCompat.getDrawable(context, R.drawable.ic_issues),
                    getString(R.string.version), false));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appCardBuilder.addItem(ConvenienceBuilder.createRateActionItem(context, ContextCompat.getDrawable(context, R.drawable.ic_star_filled),
                getString(R.string.rate_app), null));

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.report_issue)
                .subText(R.string.report_issue_here)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_bug))
                .setOnClickListener(b -> CreateIssueView.startForResult(this))
                .build());

        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.author);
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Kosh")
                .subText("k0shk0sh")
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                .setOnClickListener(b -> UserPagerView.startActivity(context, "k0shk0sh"))
                .build());
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.fork_github)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                .setOnClickListener(b -> startActivity(RepoPagerView.createIntent(this, "FastHub", "k0shk0sh")))
                .build());

        authorCardBuilder.addItem(ConvenienceBuilder.createEmailItem(context, ContextCompat.getDrawable(context, R.drawable.ic_email),
                getString(R.string.send_email), true, getString(R.string.email_address), getString(R.string.question_concerning_fasthub)));

        MaterialAboutCard.Builder logoAuthor = new MaterialAboutCard.Builder();
        logoAuthor.title(getString(R.string.logo_designer, "Kevin Aguilar"));
        logoAuthor.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.google_plus)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                .setOnClickListener(b -> ActivityHelper.startCustomTab(this, "https://plus.google.com/+KevinAguilarC"))
                .build());
        logoAuthor.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.twitter)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
                .setOnClickListener(b -> ActivityHelper.startCustomTab(this, "https://twitter.com/kevttob"))
                .build());
        logoAuthor.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.website)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_brower))
                .setOnClickListener(b -> ActivityHelper.startCustomTab(this, "https://www.221pixels.com/"))
                .build());


        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build(), logoAuthor.build());
    }

    @Override protected CharSequence getActivityTitle() {
        return getString(R.string.app_name);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == BundleConstant.REQUEST_CODE) {
            Toasty.success(this, getString(R.string.thank_you_for_feedback), Toast.LENGTH_SHORT).show();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return false;//override
    }
}
