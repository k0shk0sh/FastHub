package com.fastaccess.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Kosh on 17/12/15 10:25 PM
 */
public class TypeFaceHelper {

    private static Typeface typeFace;

    public static void generateTypeface(Context context) {
        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/app_font.ttf");
    }

    public static void applyTypeface(TextView textView) {
        textView.setTypeface(typeFace);
    }

    public static Typeface getTypeface() {
        return typeFace;
    }
}
