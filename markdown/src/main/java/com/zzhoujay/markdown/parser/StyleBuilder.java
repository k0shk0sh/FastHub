package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

/**
 * Created by zhou on 16-6-28.
 * markdown各种样式的构建器
 */
public interface StyleBuilder {

    SpannableStringBuilder em(CharSequence charSequence);

    SpannableStringBuilder italic(CharSequence charSequence);

    SpannableStringBuilder emItalic(CharSequence charSequence);

    SpannableStringBuilder delete(CharSequence charSequence);

    SpannableStringBuilder email(CharSequence charSequence);

    SpannableStringBuilder link(CharSequence title, String link, String hint);

    SpannableStringBuilder image(CharSequence title, String url, String hint);

    SpannableStringBuilder code(CharSequence charSequence);

    SpannableStringBuilder h1(CharSequence charSequence);

    SpannableStringBuilder h2(CharSequence charSequence);

    SpannableStringBuilder h3(CharSequence charSequence);

    SpannableStringBuilder h4(CharSequence charSequence);

    SpannableStringBuilder h5(CharSequence charSequence);

    SpannableStringBuilder h6(CharSequence charSequence);

    SpannableStringBuilder quota(CharSequence charSequence);

    SpannableStringBuilder ul(CharSequence charSequence, int level);

    SpannableStringBuilder ol(CharSequence charSequence, int level, int index);

    SpannableStringBuilder ul2(CharSequence charSequence, int quotaLevel, int bulletLevel);

    SpannableStringBuilder ol2(CharSequence charSequence, int quotaLevel, int bulletLevel, int index);

    SpannableStringBuilder codeBlock(CharSequence... charSequence);

    SpannableStringBuilder codeBlock(String code);

    SpannableStringBuilder gap();

}
