package com.fastaccess.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by kosh20111 on 3/11/2015. CopyRights @ Innov8tif
 * <p>
 * Input Helper to validate stuff related to input fields.
 */
public class InputHelper {


    private static boolean isWhiteSpaces(String s) {
        return s != null && s.matches("\\s+");
    }

    public static boolean isEmpty(String text) {
        return text == null || TextUtils.isEmpty(text) || isWhiteSpaces(text);
    }

    public static boolean isEmpty(Object text) {
        return text == null || TextUtils.isEmpty(text.toString()) || isWhiteSpaces(text.toString());
    }

    public static boolean isEmpty(EditText text) {
        return text == null || isEmpty(text.getText().toString());
    }

    public static boolean isEmpty(TextView text) {
        return text == null || isEmpty(text.getText().toString());
    }

    public static boolean isEmpty(TextInputLayout txt) {
        return txt == null || isEmpty(txt.getEditText());
    }

    public static String toString(EditText editText) {
        return editText.getText().toString();
    }

    public static String toString(TextView editText) {
        return editText.getText().toString();
    }

    public static String toString(TextInputLayout textInputLayout) {
        return toString(textInputLayout.getEditText());
    }

    public static String toNA(@Nullable String value) {
        return isEmpty(value) ? "N/A" : value;
    }

    @NonNull public static String toString(@NonNull Object object) {
        return !isEmpty(object) ? object.toString() : "";
    }

    public static long toLong(TextView textView) {
        if (!isEmpty(textView)) {
            return Long.valueOf(toString(textView));
        }
        return 0;
    }
}
