package com.fastaccess.provider.timeline;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.handler.DrawableHandler;
import com.fastaccess.provider.timeline.handler.PreTagHandler;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

/**
 * Created by Kosh on 21 Apr 2017, 11:24 PM
 */

public class HtmlHelper {


    public static void getComment(@NonNull TextView textView, @NonNull String html) {
        HtmlSpanner mySpanner = new HtmlSpanner();
        BetterLinkMovementMethod betterLinkMovementMethod = BetterLinkMovementMethod.linkifyHtml(textView);
        betterLinkMovementMethod.setOnLinkClickListener((view, url) -> {
            SchemeParser.launchUri(view.getContext(), Uri.parse(url));
            return true;
        });
        mySpanner.registerHandler("pre", new PreTagHandler(ViewHelper.getWindowBackground(textView.getContext()), true));
        mySpanner.registerHandler("img", new DrawableHandler(textView, true));
        mySpanner.registerHandler("g-emoji", new DrawableHandler(textView, false));
        mySpanner.registerHandler("code", new PreTagHandler(ViewHelper.getWindowBackground(textView.getContext()), false));
        textView.setText(mySpanner.fromHtml(html));
    }
}
