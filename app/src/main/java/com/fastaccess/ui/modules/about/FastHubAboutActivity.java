package com.fastaccess.ui.modules.about;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickListener;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

/**
 * Created by danielstone on 11/03/2017.
 */

public class FastHubAbout extends MaterialAboutActivity {
    @Override
    protected MaterialAboutList getMaterialAboutList(Context c) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text("FastHub")
                .icon(R.mipmap.ic_launcher)
                .build());

        try {

            appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(c,
                    new IconicsDrawable(c)
                            .icon(GoogleMaterial.Icon.gmd_info_outline)
                            .color(ContextCompat.getColor(c, R.color.aboutColorIcon))
                            .sizeDp(18),
                    "Version",
                    false));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        appCardBuilder.addItem(ConvenienceBuilder.createRateActionItem(c,
                new IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_star)
                        .color(ContextCompat.getColor(c, R.color.aboutColorIcon))
                        .sizeDp(18),
                "Rate this app",
                null
        ));


        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title("Author");
//        authorCardBuilder.titleColor(ContextCompat.getColor(c, R.color.colorAccent));

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("kosh")
                .subText("k0shk0sh")
                .icon(new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_person)
                        .color(ContextCompat.getColor(c, R.color.aboutColorIcon))
                        .sizeDp(18))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text("Fork on GitHub")
                .icon(new IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .color(ContextCompat.getColor(c, R.color.aboutColorIcon))
                        .sizeDp(18))
                .setOnClickListener(ConvenienceBuilder.createWebsiteOnClickAction(c, Uri.parse("https://github.com/daniel-stoneuk")))
                .build());

        authorCardBuilder.addItem(ConvenienceBuilder.createEmailItem(c,
                new IconicsDrawable(c)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .color(ContextCompat.getColor(c, R.color.aboutColorIcon))
                        .sizeDp(18),
                "Send an email",
                true,
                "kosh20111@gmail.com",
                "Question concerning FastHub"));

        MaterialAboutCard.Builder supportDevCard = new MaterialAboutCard.Builder();
        supportDevCard.title("Support Development");

        supportDevCard.addItem(new MaterialAboutActionItem.Builder()
                .text("Report an issue")
                .subText("Having an issue? Report it here")
                .icon(new IconicsDrawable(c)
                        .icon(GoogleMaterial.Icon.gmd_bug_report)
                        .color(ContextCompat.getColor(c, R.color.aboutColorIcon))
                        .sizeDp(18))
                .setOnClickListener(new MaterialAboutItemOnClickListener() {
                    @Override
                    public void onClick(boolean b) {
                        // TODO: Launch feedback activity
                    }
                })
                .build());


        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build(), supportDevCard.build());
    }

    @Override
    protected CharSequence getActivityTitle() {
        return null;
    }
}
