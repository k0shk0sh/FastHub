package com.zzhoujay.markdown;

import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.zzhoujay.markdown.parser.StyleBuilderImpl;

import java.io.IOException;

/**
 * Created by zhou on 16-6-25.
 * Markdown解析器
 */
public class MarkDown {

    public static Spanned fromMarkdown(String source, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(source, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Spanned fromMarkdown(String source, TextView textView, int codeColor, int headerColor, Html.ImageGetter imageGetter) {
        MarkDownParser parser = new MarkDownParser(source, new StyleBuilderImpl(textView, codeColor, headerColor, imageGetter));
        try {
            return parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
