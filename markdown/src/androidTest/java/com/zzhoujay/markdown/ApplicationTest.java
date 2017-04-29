package com.zzhoujay.markdown;

import android.app.Application;
import android.graphics.Color;
import android.test.ApplicationTestCase;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);

        Pattern pattern = Pattern.compile("#\\s+(.*)");
        String test = "# hello";
        SpannableStringBuilder builder = new SpannableStringBuilder(test);
        builder.setSpan(new ForegroundColorSpan(Color.RED), 1, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Matcher matcher = pattern.matcher(builder);
        if (matcher.find()) {
            Log.i("find", matcher.group(1));
            System.out.println(matcher.group(1));
        } else {
            Log.i("find", matcher.group(1));
            System.out.println("gg");
        }
    }
}