package com.zzhoujay.markdown.style;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

/**
 * Created by zhou on 16-7-30.
 * EmailSpan
 */
@SuppressLint("ParcelCreator")
public class EmailSpan extends URLSpan {

    private int color;

    public EmailSpan(String email, int color) {
        super(email);
        this.color = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getURL()));
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        try {
            widget.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }
}
