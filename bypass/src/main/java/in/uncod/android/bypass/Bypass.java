package in.uncod.android.bypass;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import in.uncod.android.bypass.Element.Type;
import in.uncod.android.bypass.style.CodeStyle;
import in.uncod.android.bypass.style.HorizontalLineSpan;
import in.uncod.android.bypass.style.ParagraphStyle;
import in.uncod.android.bypass.style.QuoteStyle;

public class Bypass {
    static {
        System.loadLibrary("bypass");
    }

    private final Options options;
    private final int cornerRadius;
    private final int blockQuoteLineWidth;
    private final int listItemIndent;
    private final int blockQuoteIndent;
    private final int codeBlockIndent;
    private final int hruleSize;
    private final int hruleTopBottomPadding;
    private final Map<Element, Integer> mOrderedListNumber = new ConcurrentHashMap<>();
    private ImageSpanClickListener mImageSpanClickListener;

    public static final class Options {
        private int blockQuoteColor = -16776961;
        private float blockQuoteIndentSize = 11.0f;
        private int blockQuoteIndentUnit = 1;
        private int blockQuoteLineColor = -12627531;
        private int blockQuoteLineUnit = 1;
        private int blockQuoteLineWidth = 2;
        private int codeBlockColor = Color.parseColor("#f6f8fa");
        private float codeBlockIndentSize = 11.0f;
        private int codeBlockIndentUnit = 1;
        private int cornerRadius = 2;
        private int cornerRadiusUnit = 1;
        private float[] headerSizes = new float[]{1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1.0f};
        private int hruleColor = -7829368;
        private float hruleSize = 1.0f;
        private int hruleUnit = 1;
        private float listItemIndentSize = 10.0f;
        private int listItemIndentUnit = 1;
        private String unorderedListItem = "•";


        public Options setHeaderSizes(float[] fArr) {
            if (fArr == null) {
                throw new IllegalArgumentException("headerSizes must not be null");
            } else if (fArr.length != 6) {
                throw new IllegalArgumentException("headerSizes must have 6 elements (h1 through h6)");
            } else {
                this.headerSizes = fArr;
                return this;
            }
        }

        public Options setUnorderedListItem(String str) {
            this.unorderedListItem = str;
            return this;
        }

        public Options setListItemIndentSize(int i, float f) {
            this.listItemIndentUnit = i;
            this.listItemIndentSize = f;
            return this;
        }

        public Options setBlockQuoteColor(int i) {
            this.blockQuoteColor = i;
            return this;
        }

        public Options setBlockQuoteIndentSize(int i, float f) {
            this.blockQuoteIndentUnit = i;
            this.blockQuoteIndentSize = f;
            return this;
        }

        public Options setCodeBlockIndentSize(int i, float f) {
            this.codeBlockIndentUnit = i;
            this.codeBlockIndentSize = f;
            return this;
        }

        public Options setCodeBlockColor(int i) {
            this.codeBlockColor = i;
            return this;
        }

        public Options setHruleColor(int i) {
            this.hruleColor = i;
            return this;
        }

        public Options setHruleSize(int i, float f) {
            this.hruleUnit = i;
            this.hruleSize = f;
            return this;
        }
    }

    public interface ImageGetter {
        Drawable getDrawable(String source);
    }


    public Bypass() {
        this(new Options());
    }

    public Bypass(@NonNull Options options) {
        this.options = options;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        this.listItemIndent = (int) TypedValue.applyDimension(options.listItemIndentUnit, options.listItemIndentSize, displayMetrics);
        this.blockQuoteIndent = (int) TypedValue.applyDimension(options.blockQuoteIndentUnit, options.blockQuoteIndentSize, displayMetrics);
        this.codeBlockIndent = (int) TypedValue.applyDimension(options.codeBlockIndentUnit, options.codeBlockIndentSize, displayMetrics);
        this.hruleSize = (int) TypedValue.applyDimension(options.hruleUnit, options.hruleSize, displayMetrics);
        this.blockQuoteLineWidth = (int) TypedValue.applyDimension(options.blockQuoteLineUnit, (float) options.blockQuoteLineWidth,
                displayMetrics);
        this.hruleTopBottomPadding = ((int) displayMetrics.density) * 10;
        this.cornerRadius = (int) TypedValue.applyDimension(options.cornerRadiusUnit, (float) options.cornerRadius, displayMetrics);
    }

    public void setImageSpanClickListener(ImageSpanClickListener listener) {
        mImageSpanClickListener = listener;
    }

    public CharSequence markdownToSpannable(@NonNull String markdown) {
        return markdownToSpannable(markdown, null);
    }

    public CharSequence markdownToSpannable(@NonNull String markdown, @Nullable ImageGetter imageGetter) {
        Document document = processMarkdown(markdown);
        int size = document.getElementCount();
        CharSequence[] spans = new CharSequence[size];
        for (int i = 0; i < size; i++) {
            spans[i] = recurseElement(document.getElement(i), i, size, imageGetter);
        }
        return TextUtils.concat(spans);
    }

    @SuppressWarnings("JniMissingFunction") private native Document processMarkdown(String markdown);

    // The 'numberOfSiblings' parameters refers to the number of siblings within the parent, including
    // the 'element' parameter, as in "How many siblings are you?" rather than "How many siblings do
    // you have?".
    private CharSequence recurseElement(@NonNull Element element, int indexWithinParent, int numberOfSiblings,
                                        @Nullable ImageGetter imageGetter) {
        Type type = element.getType();
        boolean isOrderedList = false;
        if (type == Type.LIST) {
            String flagsStr = element.getAttribute("flags");
            if (flagsStr != null) {
                int flags = Integer.parseInt(flagsStr);
                isOrderedList = (flags & Element.F_LIST_ORDERED) != 0;
                if (isOrderedList) {
                    mOrderedListNumber.put(element, 1);
                }
            }
        }

        int size = element.size();
        CharSequence[] spans = new CharSequence[size];
        for (int i = 0; i < size; i++) {
            spans[i] = recurseElement(element.children[i], i, size, imageGetter);
        }
        if (isOrderedList) {
            mOrderedListNumber.remove(this);
        }
        CharSequence concat = TextUtils.concat(spans);
        SpannableStringBuilder builder = new ReverseSpannableStringBuilder();
        String text = element.getText();
        if (element.size() == 0 && element.getParent() != null && element.getParent().getType() != Type.BLOCK_CODE) {
            text = text.replace('\n', ' ');
        }
        Drawable imageDrawable = null;
        String imageLink = element.getAttribute("link");
        if (type == Type.IMAGE && imageGetter != null && !TextUtils.isEmpty(imageLink)) {
            imageDrawable = imageGetter.getDrawable(imageLink);
        }

        switch (type) {
            case LIST:
                if (element.getParent() != null
                        && element.getParent().getType() == Type.LIST_ITEM) {
                    builder.append("\n");
                }
                break;
            case LINEBREAK:
                builder.append("\n");
                break;
            case LIST_ITEM:
                builder.append(" ");
                if (mOrderedListNumber.containsKey(element.getParent())) {
                    int number = mOrderedListNumber.get(element.getParent());
                    builder.append(Integer.toString(number)).append(".");
                    mOrderedListNumber.put(element.getParent(), number + 1);
                } else {
                    builder.append(options.unorderedListItem);
                }
                builder.append("  ");
                break;
            case AUTOLINK:
                builder.append(element.getAttribute("link"));
                break;
            case HRULE:
                builder.append("-");
                break;
            case IMAGE:
                // Display alt text (or title text) if there is no image
                if (imageDrawable == null) {
                    String show = element.getAttribute("alt");
                    if (TextUtils.isEmpty(show)) {
                        show = element.getAttribute("title");
                    }
                    if (!TextUtils.isEmpty(show)) {
                        show = "[" + show + "]";
                        builder.append(show);
                    }
                } else {
                    // Character to be replaced
                    builder.append("\uFFFC");
                }
                break;
        }

        builder.append(text);
        builder.append(concat);
        if (element.getParent() != null || indexWithinParent < (numberOfSiblings - 1)) {
            if (type == Type.LIST_ITEM) {
                if (element.size() == 0 || !element.children[element.size() - 1].isBlockElement()) {
                    builder.append("\n");
                }
            } else if (element.isBlockElement() && type != Type.BLOCK_QUOTE) {
                if (type == Type.LIST) {
                    // If this is a nested list, don't include newlines
                    if (element.getParent() == null || element.getParent().getType() != Type.LIST_ITEM) {
                        builder.append("\n");
                    }
                } else if (element.getParent() != null
                        && element.getParent().getType() == Type.LIST_ITEM) {
                    // List items should never double-space their entries
                    builder.append("\n");
                } else {
                    builder.append("\n\n");
                }
            }
        }

        switch (type) {
            case HEADER:
                String levelStr = element.getAttribute("level");
                int level = Integer.parseInt(levelStr);
                setSpan(builder, new RelativeSizeSpan(options.headerSizes[level - 1]));
                setSpan(builder, new StyleSpan(Typeface.BOLD));
                break;
            case LIST:
                setBlockSpan(builder, new LeadingMarginSpan.Standard(listItemIndent));
                break;
            case EMPHASIS:
                setSpan(builder, new StyleSpan(Typeface.ITALIC));
                break;
            case DOUBLE_EMPHASIS:
                setSpan(builder, new StyleSpan(Typeface.BOLD));
                break;
            case TRIPLE_EMPHASIS:
                setSpan(builder, new StyleSpan(Typeface.BOLD_ITALIC));
                break;
            case BLOCK_CODE:
                setSpan(builder, new ParagraphStyle(this.options.codeBlockColor, (float) this.cornerRadius));
                setSpan(builder, new LeadingMarginSpan.Standard(this.codeBlockIndent));
                setSpan(builder, new TypefaceSpan("monospace"));
                break;
            case CODE_SPAN:
                setSpan(builder, new CodeStyle(this.options.codeBlockColor));
                setSpan(builder, new TypefaceSpan("monospace"));
                break;
            case LINK:
            case AUTOLINK:
                String link = element.getAttribute("link");
                if (!TextUtils.isEmpty(link) && Patterns.EMAIL_ADDRESS.matcher(link).matches()) {
                    link = "mailto:" + link;
                }
                setSpan(builder, new URLSpan(link));
                break;
            case BLOCK_QUOTE:
                setBlockSpan(builder, new QuoteStyle(this.blockQuoteLineWidth, this.options
                        .blockQuoteLineColor));
                break;
            case STRIKETHROUGH:
                setSpan(builder, new StrikethroughSpan());
                break;
            case HRULE:
                setSpan(builder, new HorizontalLineSpan(options.hruleColor, hruleSize, hruleTopBottomPadding));
                break;
            case IMAGE:
                if (imageDrawable != null) {
                    setClickableImageSpan(builder, new ImageSpan(imageDrawable), imageLink);
                }
                break;
            case SUPERSCRIPT:
                setSpan(builder, new SuperscriptSpan());
                break;
        }

        return builder;
    }

    private static void setSpan(@NonNull SpannableStringBuilder builder, @NonNull Object what) {
        builder.setSpan(what, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // These have trailing newlines that we want to avoid spanning
    private static void setBlockSpan(@NonNull SpannableStringBuilder builder, @NonNull Object what) {
        int length = Math.max(0, builder.length() - 1);
        builder.setSpan(what, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setClickableImageSpan(@NonNull final SpannableStringBuilder builder, @NonNull final ImageSpan what,
                                       @NonNull final String link) {
        builder.setSpan(what, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                if (mImageSpanClickListener != null) {
                    mImageSpanClickListener.onImageClicked(what, link);
                }
                widget.invalidate();
            }
        }, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}