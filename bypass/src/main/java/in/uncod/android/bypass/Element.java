package in.uncod.android.bypass;

import java.util.HashMap;
import java.util.Map;

public class Element {
    public static final int F_LIST_ORDERED = 1;
    Map<String, String> attributes = new HashMap();
    Element[] children;
    int nestLevel = 0;
    Element parent;
    String text;
    Type type;

    public enum Type {
        BLOCK_CODE(0),
        BLOCK_QUOTE(1),
        BLOCK_HTML(2),
        HEADER(3),
        HRULE(4),
        LIST(5),
        LIST_ITEM(6),
        PARAGRAPH(7),
        TABLE(8),
        TABLE_CELL(9),
        TABLE_ROW(10),
        AUTOLINK(267),
        CODE_SPAN(268),
        DOUBLE_EMPHASIS(269),
        EMPHASIS(270),
        IMAGE(271),
        LINEBREAK(272),
        LINK(273),
        RAW_HTML_TAG(274),
        TRIPLE_EMPHASIS(275),
        TEXT(276),
        STRIKETHROUGH(277),
        SUPERSCRIPT(278);

        private static Type[] TypeValues = null;
        private final int value;

        static {
            TypeValues = values();
        }

        private Type(int i) {
            this.value = i;
        }

        public static Type fromInteger(int i) {
            for (Type type : TypeValues) {
                if (type.value == i) {
                    return type;
                }
            }
            return null;
        }
    }

    public Element(String str, int i) {
        this.text = str;
        this.type = Type.fromInteger(i);
    }

    public void setParent(Element element) {
        this.parent = element;
    }

    public void setChildren(Element[] elementArr) {
        this.children = elementArr;
    }

    public void addAttribute(String str, String str2) {
        this.attributes.put(str, str2);
    }

    public String getAttribute(String str) {
        return (String) this.attributes.get(str);
    }

    public Element getParent() {
        return this.parent;
    }

    public String getText() {
        return this.text;
    }

    public int size() {
        if (this.children != null) {
            return this.children.length;
        }
        return 0;
    }

    public Type getType() {
        return this.type;
    }

    public boolean isBlockElement() {
        return (this.type.value & 256) == 0;
    }

    public boolean isSpanElement() {
        return (this.type.value & 256) == 256;
    }
}