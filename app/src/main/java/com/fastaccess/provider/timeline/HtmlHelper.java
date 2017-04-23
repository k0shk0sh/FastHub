package com.fastaccess.provider.timeline;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.handler.BetterLinkMovementExtended;
import com.fastaccess.provider.timeline.handler.DrawableHandler;
import com.fastaccess.provider.timeline.handler.PreTagHandler;
import com.fastaccess.provider.timeline.handler.QouteHandler;

import net.nightwhistler.htmlspanner.HtmlSpanner;

/**
 * Created by Kosh on 21 Apr 2017, 11:24 PM
 */

public class HtmlHelper {


    public static void getComment(@NonNull TextView textView, @NonNull String html) {
        HtmlSpanner mySpanner = new HtmlSpanner();
        BetterLinkMovementExtended betterLinkMovementMethod = BetterLinkMovementExtended.linkifyHtml(textView);
        betterLinkMovementMethod.setOnLinkClickListener((view, url) -> {
            SchemeParser.launchUri(view.getContext(), Uri.parse(url));
            return true;
        });
        int windowBackground = ViewHelper.getWindowBackground(textView.getContext());
        mySpanner.registerHandler("pre", new PreTagHandler(windowBackground, true));
        mySpanner.registerHandler("img", new DrawableHandler(textView));
        mySpanner.registerHandler("g-emoji", new DrawableHandler(textView));
        mySpanner.registerHandler("code", new PreTagHandler(windowBackground, false));
        mySpanner.registerHandler("blockquote", new QouteHandler(windowBackground));
        textView.setText(mySpanner.fromHtml(html));
    }
}
