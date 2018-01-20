package com.fastaccess.provider.emoji;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * This class represents an emoji.<br>
 * <br>
 * This object is immutable so it can be used safely in a multithreaded context.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class Emoji {
    private final String description;
    private final boolean supportsFitzpatrick;
    private final List<String> aliases;
    private final List<String> tags;
    private String unicode;
    private String htmlDec;
    private String htmlHex;

    /**
     * Constructor for the Emoji.
     *
     * @param description
     *         The description of the emoji
     * @param supportsFitzpatrick
     *         Whether the emoji supports Fitzpatrick modifiers
     * @param aliases
     *         the aliases for this emoji
     * @param tags
     *         the tags associated with this emoji
     * @param bytes
     *         the bytes that represent the emoji
     */
    protected Emoji(
            String description,
            boolean supportsFitzpatrick,
            List<String> aliases,
            List<String> tags,
            byte... bytes
    ) {
        this.description = description;
        this.supportsFitzpatrick = supportsFitzpatrick;
        this.aliases = Collections.unmodifiableList(aliases);
        this.tags = Collections.unmodifiableList(tags);

        int count = 0;
        try {
            this.unicode = new String(bytes, "UTF-8");
            int stringLength = getUnicode().length();
            String[] pointCodes = new String[stringLength];
            String[] pointCodesHex = new String[stringLength];
            for (int offset = 0; offset < stringLength; ) {
                final int codePoint = getUnicode().codePointAt(offset);
                pointCodes[count] = String.format(Locale.getDefault(), "&#%d;", codePoint);
                pointCodesHex[count++] = String.format("&#x%x;", codePoint);
                offset += Character.charCount(codePoint);
            }
            this.htmlDec = stringJoin(pointCodes, count);
            this.htmlHex = stringJoin(pointCodesHex, count);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to replace String.join, since it was only introduced in java8
     *
     * @param array
     *         the array to be concatenated
     * @return concatenated String
     */
    private String stringJoin(String[] array, int count) {
        String joined = "";
        for (int i = 0; i < count; i++)
            joined += array[i];
        return joined;
    }

    /**
     * Returns the description of the emoji
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns wether the emoji supports the Fitzpatrick modifiers or not
     *
     * @return true if the emoji supports the Fitzpatrick modifiers
     */
    public boolean supportsFitzpatrick() {
        return this.supportsFitzpatrick;
    }

    /**
     * Returns the aliases of the emoji
     *
     * @return the aliases (unmodifiable)
     */
    public List<String> getAliases() {
        return this.aliases;
    }

    /**
     * Returns the tags of the emoji
     *
     * @return the tags (unmodifiable)
     */
    public List<String> getTags() {
        return this.tags;
    }

    /**
     * Returns the unicode representation of the emoji
     *
     * @return the unicode representation
     */
    public String getUnicode() {
        return this.unicode;
    }

    /**
     * Returns the unicode representation of the emoji associated with the
     * provided Fitzpatrick modifier.<br>
     * If the modifier is null, then the result is similar to
     * {@link Emoji#getUnicode()}
     *
     * @param fitzpatrick
     *         the fitzpatrick modifier or null
     * @return the unicode representation
     * @throws UnsupportedOperationException
     *         if the emoji doesn't support the Fitzpatrick modifiers
     */
    public String getUnicode(Fitzpatrick fitzpatrick) {
        if (!this.supportsFitzpatrick()) {
            throw new UnsupportedOperationException(
                    "Cannot get the unicode with a fitzpatrick modifier, " +
                            "the emoji doesn't support fitzpatrick."
            );
        } else if (fitzpatrick == null) {
            return this.getUnicode();
        }
        return this.getUnicode() + fitzpatrick.unicode;
    }

    /**
     * Returns the HTML decimal representation of the emoji
     *
     * @return the HTML decimal representation
     */
    public String getHtmlDecimal() {
        return this.htmlDec;
    }

    /**
     * @return the HTML hexadecimal representation
     * @deprecated identical to {@link #getHtmlHexadecimal()} for backwards-compatibility. Use that instead.
     */
    public String getHtmlHexidecimal() {
        return this.getHtmlHexadecimal();
    }

    /**
     * Returns the HTML hexadecimal representation of the emoji
     *
     * @return the HTML hexadecimal representation
     */
    public String getHtmlHexadecimal() {
        return this.htmlHex;
    }

    @Override
    public boolean equals(Object other) {
        return !(other == null || !(other instanceof Emoji)) &&
                ((Emoji) other).getUnicode().equals(getUnicode());
    }

    @Override
    public int hashCode() {
        return unicode.hashCode();
    }

    /**
     * Returns the String representation of the Emoji object.<br>
     * <br>
     * Example:<br>
     * <code>Emoji {
     * description='smiling face with open mouth and smiling eyes',
     * supportsFitzpatrick=false,
     * aliases=[smile],
     * tags=[happy, joy, pleased],
     * unicode='😄',
     * htmlDec='&amp;#128516;',
     * htmlHex='&amp;#x1f604;'
     * }</code>
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return "Emoji{" +
                "description='" + description + '\'' +
                ", supportsFitzpatrick=" + supportsFitzpatrick +
                ", aliases=" + aliases +
                ", tags=" + tags +
                ", unicode='" + unicode + '\'' +
                ", htmlDec='" + htmlDec + '\'' +
                ", htmlHex='" + htmlHex + '\'' +
                '}';
    }
}
