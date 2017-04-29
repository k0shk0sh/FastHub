package com.fastaccess.provider.timeline;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.TextView;

import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.scheme.SchemeParser;
import com.fastaccess.provider.timeline.handler.BetterLinkMovementExtended;
import com.fastaccess.provider.timeline.handler.DrawableHandler;
import com.fastaccess.provider.timeline.handler.PreTagHandler;
import com.fastaccess.provider.timeline.handler.QouteHandler;

import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.handlers.BoldHandler;
import net.nightwhistler.htmlspanner.handlers.TableHandler;


/**
 * Created by Kosh on 21 Apr 2017, 11:24 PM
 */

public class HtmlHelper {
    public static void parseHtmlIntoTextView(@NonNull TextView textView, @NonNull String html) {
        HtmlSpanner mySpanner = new HtmlSpanner();
        mySpanner.setStripExtraWhiteSpace(true);
        BetterLinkMovementExtended betterLinkMovementMethod = BetterLinkMovementExtended.linkifyHtml(textView);
        betterLinkMovementMethod.setOnLinkClickListener((view, url) -> {
            SchemeParser.launchUri(view.getContext(), Uri.parse(url));
            return true;
        });
        int windowBackground = ViewHelper.getWindowBackground(textView.getContext());
        mySpanner.registerHandler("pre", new PreTagHandler(windowBackground, true));
        mySpanner.registerHandler("code", new PreTagHandler(windowBackground, false));
        mySpanner.registerHandler("img", new DrawableHandler(textView));
        mySpanner.registerHandler("g-emoji", new DrawableHandler(textView));
        mySpanner.registerHandler("blockquote", new QouteHandler(windowBackground));
        mySpanner.registerHandler("b", new BoldHandler());
        mySpanner.registerHandler("strong", new BoldHandler());
        TableHandler tableHandler = new TableHandler();
        tableHandler.setTextColor(ViewHelper.generateTextColor(windowBackground));
        WindowManager windowManager = (WindowManager) textView.getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        tableHandler.setTableWidth((int) (point.x / 1.2));
        tableHandler.setTextSize(18.0F);
        mySpanner.registerHandler("table", tableHandler);
        textView.setText(mySpanner.fromHtml(html));
    }
}
