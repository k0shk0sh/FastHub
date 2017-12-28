package ru.noties.markwon.renderer.html;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TagParser {


    private static final Set<String> VOID_TAGS;
    static {
        final String[] tags = {
                "area", "base", "br", "col", "embed", "hr", "img", "input",
                "keygen", "link", "meta", "param", "source", "track", "wbr"
        };
        final Set<String> set = new HashSet<>(tags.length);
        Collections.addAll(set, tags);
        VOID_TAGS = Collections.unmodifiableSet(set);
    }


    TagParser() {
    }

    @Nullable
    SpannableHtmlParser.Tag parse(String html) {

        final SpannableHtmlParser.Tag tag;

        final int length = html != null
                ? html.length()
                : 0;

        // absolutely minimum (`<i>`)
        if (length < 3) {
            tag = null;
        } else {

//            // okay, we will consider a tag a void one if it's in our void list tag

            final boolean closing = '<' == html.charAt(0) && '/' == html.charAt(1);
            final boolean voidTag;

            Map<String, String> attributes = null;

            final StringBuilder builder = new StringBuilder();

            String name = null;
            String pendingAttribute = null;

            char c;
            char valueDelimiter = '\0';

            for (int i = 0; i < length; i++) {

                c = html.charAt(i);

                // no more handling
                if ('>' == c
                        || '\\' == c) {
                    break;
                }

                if (name == null) {
                    if (Character.isSpaceChar(c)) {
                        //noinspection StatementWithEmptyBody
                        if (builder.length() == 0) {
                            // ignore it, we must wait until we have tagName
                        } else {

                            name = builder.toString();

                            // clear buffer
                            builder.setLength(0);
                        }
                    } else {
                        if (Character.isLetterOrDigit(c)) {
                            builder.append(c);
                        } /*else {
                        // we allow non-letter-digit only if builder.length == 0
                        // if we have already started
                    }*/
                    }
                } else if (pendingAttribute == null) {
                    // we start checking for attribute
                    // ignore non-letter-digits before
                    if (Character.isLetterOrDigit(c)) {
                        builder.append(c);
                    } else /*if ('=' == c)*/ {

                        // attribute name is finished (only if we have already added something)
                        // else it's trailing chars that we are not interested in
                        if (builder.length() > 0) {
                            pendingAttribute = builder.toString();
                            builder.setLength(0);
                        }
                    }
                } else {
                    // first char that we will meet will be the delimiter
                    if (valueDelimiter == '\0') {
                        valueDelimiter = c;
                    } else {
                        if (c == valueDelimiter) {
                            if (attributes == null) {
                                attributes = new HashMap<>(3);
                            }
                            attributes.put(pendingAttribute, builder.toString());
                            pendingAttribute = null;
                            valueDelimiter = '\0';
                            builder.setLength(0);
                        } else {
                            builder.append(c);
                        }
                    }
                }
            }

            if (builder.length() > 0) {
                if (name == null) {
                    name = builder.toString();
                } else if (pendingAttribute != null) {
                    if (attributes == null) {
                        attributes = new HashMap<>(3);
                    }
                    attributes.put(pendingAttribute, builder.toString());
                }
            }

            // in case of wrong parsing
            if (name == null) {
                tag = null;
            } else {

                voidTag = !closing && VOID_TAGS.contains(name);

                final Map<String, String> attributesMap;
                if (attributes == null
                        || attributes.size() == 0) {
                    //noinspection unchecked
                    attributesMap = Collections.EMPTY_MAP;
                } else {
                    attributesMap = Collections.unmodifiableMap(attributes);
                }

                tag = new SpannableHtmlParser.Tag(html, name, attributesMap, !closing, voidTag);
            }
        }

        return tag;
    }
}
