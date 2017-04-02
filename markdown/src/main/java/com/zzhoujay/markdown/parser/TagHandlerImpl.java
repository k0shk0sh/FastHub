package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;
import android.util.Pair;
import android.util.SparseArray;

import com.zzhoujay.markdown.style.CodeSpan;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-7-10.
 * TagHandlerImpl
 */
public class TagHandlerImpl implements TagHandler {

    private static final Matcher matcherH1_2 = Pattern.compile("^\\s*=+\\s*$").matcher("");
    private static final Matcher matcherH2_2 = Pattern.compile("^\\s*-+\\s*$").matcher("");

    private static final Matcher matcherH = Pattern.compile("^\\s*#{1,6}\\s*([^#]*)(\\s*#)?").matcher("");
    private static final Matcher matcherH1 = Pattern.compile("^\\s*#\\s*([^#]*)(\\s*#)?").matcher("");
    private static final Matcher matcherH2 = Pattern.compile("^\\s*#{2}\\s*([^#]*)(\\s*#)?").matcher("");
    private static final Matcher matcherH3 = Pattern.compile("^\\s*#{3}\\s*([^#]*)(\\s*#)?").matcher("");
    private static final Matcher matcherH4 = Pattern.compile("^\\s*#{4}\\s*([^#]*)(\\s*#)?").matcher("");
    private static final Matcher matcherH5 = Pattern.compile("^\\s*#{5}\\s*([^#]*)(\\s*#)?").matcher("");
    private static final Matcher matcherH6 = Pattern.compile("^\\s*#{6}\\s*([^#]*)(\\s*#)?").matcher("");

    private static final Matcher matcherQuota = Pattern.compile("^\\s{0,3}>\\s(.*)").matcher("");
    private static final Matcher matcherUl = Pattern.compile("^\\s*[*+-]\\s+(.*)").matcher("");
    private static final Matcher matcherOl = Pattern.compile("^\\s*\\d+\\.\\s+(.*)").matcher("");

    private static final Matcher matcherItalic = Pattern.compile("[^*_]*(([*_])([^*_].*?)\\2)").matcher("");
    private static final Matcher matcherEm = Pattern.compile("[^*_]*(([*_])\\2([^*_].*?)\\2\\2)").matcher("");
    private static final Matcher matcherEmItalic = Pattern.compile("[^*_]*(([*_])\\2\\2([^*_].*?)\\2\\2\\2)").matcher("");
    private static final Matcher matcherDelete = Pattern.compile("[^~]*((~{2,4})([^~].*?)\\2)").matcher("");
    private static final Matcher matcherCode = Pattern.compile("[^`]*((`+)([^`].*?)\\2)").matcher("");

    private static final Matcher matcherLink = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\(\\s*(\\S*?)(\\s+(['\"])(.*?)\\5)?\\s*?\\))").matcher("");
    private static final Matcher matcherImage = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\(\\s*(\\S*?)(\\s+(['\"])(.*?)\\5)?\\s*?\\))").matcher("");
    private static final Matcher matcherLink2 = Pattern.compile(".*?(\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])").matcher("");
    private static final Matcher matcherLinkId = Pattern.compile("^\\s*\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*$").matcher("");
    private static final Matcher matcherImage2 = Pattern.compile(".*?(!\\[\\s*(.*?)\\s*]\\s*\\[\\s*(.*?)\\s*])").matcher("");
    private static final Matcher matcherImageId = Pattern.compile("^\\s*!\\[\\s*(.*?)\\s*]:\\s*(\\S+?)(\\s+(['\"])(.*?)\\4)?\\s*$").matcher("");

    private static final Matcher matcherEmail = Pattern.compile(".*?(<(\\S+@\\S+\\.\\S+)>).*?").matcher("");
    private static final Matcher matcherAutoLink = Pattern.compile("((https|http|ftp|rtsp|mms)?://)[^\\s]+").matcher("");

    private static final Matcher matcherEndSpace = Pattern.compile("(.*?) {2} *$").matcher("");
    private static final Matcher matcherInlineSpace = Pattern.compile("\\S*(\\s+)\\S+").matcher("");

    private static final Matcher matcherCodeBlock = Pattern.compile("^( {4}|\\t)(.*)").matcher("");
    private static final Matcher matcherCodeBlock2 = Pattern.compile("^\\s*```").matcher("");

    private static final Matcher matcherBlankLine = Pattern.compile("^\\s*$").matcher("");

    private static final Matcher matcherGap = Pattern.compile("^\\s*([-*]\\s*){3,}$").matcher("");

    private static final SparseArray<Matcher> matchers = new SparseArray<>();// matcher缓冲区

    static {
        matchers.put(Tag.CODE_BLOCK_1, matcherCodeBlock);
        matchers.put(Tag.CODE_BLOCK_2, matcherCodeBlock2);
        matchers.put(Tag.H1, matcherH1);
        matchers.put(Tag.H2, matcherH2);
        matchers.put(Tag.H3, matcherH3);
        matchers.put(Tag.H4, matcherH4);
        matchers.put(Tag.H5, matcherH5);
        matchers.put(Tag.H6, matcherH6);
        matchers.put(Tag.H, matcherH);
        matchers.put(Tag.QUOTA, matcherQuota);
        matchers.put(Tag.UL, matcherUl);
        matchers.put(Tag.OL, matcherOl);
        matchers.put(Tag.EM, matcherEm);
        matchers.put(Tag.ITALIC, matcherItalic);
        matchers.put(Tag.EM_ITALIC, matcherEmItalic);
        matchers.put(Tag.EMAIL, matcherEmail);
        matchers.put(Tag.AUTO_LINK, matcherAutoLink);
        matchers.put(Tag.DELETE, matcherDelete);
        matchers.put(Tag.LINK, matcherLink);
        matchers.put(Tag.LINK2, matcherLink2);
        matchers.put(Tag.LINK_ID, matcherLinkId);
        matchers.put(Tag.IMAGE, matcherImage);
        matchers.put(Tag.IMAGE2, matcherImage2);
        matchers.put(Tag.IMAGE_ID, matcherImageId);
        matchers.put(Tag.BLANK, matcherBlankLine);
        matchers.put(Tag.NEW_LINE, matcherEndSpace);
        matchers.put(Tag.GAP, matcherGap);
        matchers.put(Tag.H1_2, matcherH1_2);
        matchers.put(Tag.H2_2, matcherH2_2);
        matchers.put(Tag.CODE, matcherCode);
    }

    private StyleBuilder styleBuilder;
    private QueueProvider queueProvider;
    private HashMap<String, Pair<String, String>> idLinkLinks;
    private HashMap<String, Pair<String, String>> idImageUrl;


    public TagHandlerImpl(StyleBuilder styleBuilder) {
        this.styleBuilder = styleBuilder;
        idImageUrl = new HashMap<>();
        idLinkLinks = new HashMap<>();
    }

    @Override
    public boolean h(Line line) {
        return h6(line) || h5(line) || h4(line) || h3(line) || h2(line) || h1(line);
    }

    @Override
    public boolean h1(Line line) {
        Matcher matcher = obtain(Tag.H1, line.getSource());
        if (matcher != null && matcher.find()) {
            line.setType(Line.LINE_TYPE_H1);
            line.setStyle(SpannableStringBuilder.valueOf(matcher.group(1)));
            inline(line);
            line.setStyle(styleBuilder.h1(line.getStyle()));
            return true;
        }
        return false;
    }

    @Override
    public boolean h2(Line line) {
        Matcher matcher = obtain(Tag.H2, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H2);
            line.setStyle(SpannableStringBuilder.valueOf(matcher.group(1)));
            inline(line);
            line.setStyle(styleBuilder.h2(line.getStyle()));
            return true;
        }
        return false;
    }

    @Override
    public boolean h3(Line line) {
        Matcher matcher = obtain(Tag.H3, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H3);
            line.setStyle(SpannableStringBuilder.valueOf(matcher.group(1)));
            inline(line);
            line.setStyle(styleBuilder.h3(line.getStyle()));

            return true;
        }
        return false;
    }

    @Override
    public boolean h4(Line line) {
        Matcher matcher = obtain(Tag.H4, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H4);
            line.setStyle(SpannableStringBuilder.valueOf(matcher.group(1)));
            inline(line);
            line.setStyle(styleBuilder.h4(line.getStyle()));

            return true;
        }
        return false;
    }

    @Override
    public boolean h5(Line line) {
        Matcher matcher = obtain(Tag.H5, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H5);
            line.setStyle(SpannableStringBuilder.valueOf(matcher.group(1)));
            inline(line);
            line.setStyle(styleBuilder.h5(line.getStyle()));

            return true;
        }
        return false;
    }

    @Override
    public boolean h6(Line line) {
        Matcher matcher = obtain(Tag.H6, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_H6);
            line.setStyle(SpannableStringBuilder.valueOf(matcher.group(1)));
            inline(line);
            line.setStyle(styleBuilder.h6(line.getStyle()));

            return true;
        }
        return false;
    }

    @Override
    public boolean quota(Line line) {
        LineQueue queue = queueProvider.getQueue();
        line = line.get();

        Matcher matcher = obtain(Tag.QUOTA, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_QUOTA);
            Line child = line.createChild(matcher.group(1));
            line.attachChildToNext();
            line.attachChildToPrev();

            Line prev = queue.prevLine();
            if (line.parentLine() == null && prev != null && prev.getType() == Line.LINE_TYPE_QUOTA) {
                SpannableStringBuilder style = new SpannableStringBuilder(" ");
                styleBuilder.quota(style);
                while (prev.childLine() != null && prev.childLine().getType() == Line.LINE_TYPE_QUOTA) {
                    prev = prev.childLine();
                    styleBuilder.quota(style);
                }
                prev.copyToNext();
                queue.prevLine().setStyle(style);
            }
            if (quota(child) || ul(child) || ol(child) || h(child)) {
                if (child.getHandle() == Line.HANDLE_BY_ROOT) {
                    if (line.parentLine() == null) {
                        if (child.getData() == Line.LINE_TYPE_UL) {
                            line.setStyle(styleBuilder.ul2(child.getStyle(), findCount(Tag.QUOTA, line, 1) - 1, child.getAttr()));
                        } else {
                            line.setStyle(styleBuilder.ol2(child.getStyle(), findCount(Tag.QUOTA, line, 1) - 1, child.getAttr(), child.getCount()));
                        }
                    } else {
                        line.setData(child.getData());
                        line.setStyle(child.getStyle());
                        line.setAttr(child.getAttr());
                        line.setCount(child.getCount());
                        line.setHandle(Line.HANDLE_BY_ROOT);
                    }
                    return true;
                }
            } else {
                child.setStyle(SpannableStringBuilder.valueOf(child.getSource()));
                inline(child);
            }

            line.setStyle(styleBuilder.quota(child.getStyle()));
            return true;
        }
        return false;
    }

    @Override
    public boolean ul(Line line) {
        return ul(line, 0);
    }

    private boolean ul(Line line, int level) {
        Matcher matcher = obtain(Tag.UL, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_UL);
            Line line1 = line.createChild(matcher.group(1));
            line.setAttr(0);

            Line parent = line.parentLine();
            LineQueue queue = queueProvider.getQueue();
            Line prev = line.prevLine();

            boolean inQuota = queue.currLine().getType() == Line.LINE_TYPE_QUOTA;
            if (inQuota) {
                line.setHandle(Line.HANDLE_BY_ROOT);
                line.setData(Line.LINE_TYPE_UL);
            }


            if (prev != null && (prev.getType() == Line.LINE_TYPE_OL || prev.getType() == Line.LINE_TYPE_UL)) {
                if (level > 0) {
                    line.setAttr(level);
                } else {
                    String m = line.getSource().substring(matcher.start(), matcher.start(1) - 2);
                    m = m.replaceAll("\\t", "    ");
                    if (m.length() > prev.getAttr() * 2 + 1)
                        line.setAttr(prev.getAttr() + 1);
                    else
                        line.setAttr(m.length() / 2);
                }

            }
            if (inQuota) {
                line.setStyle(" ");
            } else {
                line.setStyle(styleBuilder.ul(" ", line.getAttr()));
            }
            if (find(Tag.UL, line1)) {
                int nextLevel = line.getAttr() + 1;
                line1.unAttachFromParent();

                if (parent != null) {
                    Line p = parent.copyToNext();
                    p.addChild(line1);
                    queue.next();
                    ul(line1, nextLevel);
                    if (inQuota) {
                        while (p.parentLine() != null) {
                            p = p.parentLine();
                        }
                        p.setStyle(styleBuilder.ul2(line1.getStyle(), findCount(Tag.QUOTA, p, 1) - 1, line1.getAttr()));
                    } else {
                        while (p != null && p.getType() == Line.LINE_TYPE_QUOTA) {
                            p.setStyle(styleBuilder.quota(line1.getStyle()));
                            p = p.parentLine();
                        }
                    }
                } else {
                    line.addNext(line1);
                    queue.next();
                    ul(queue.currLine(), nextLevel);
                }

                return true;
            }
            if (find(Tag.OL, line1)) {
                int nextLevel = line.getAttr() + 1;
                line1.unAttachFromParent();

                if (parent != null) {
                    Line p = parent.copyToNext();
                    p.addChild(line1);
                    queue.next();
                    ol(line1, nextLevel);
                    if (inQuota) {
                        while (p.parentLine() != null) {
                            p = p.parentLine();
                        }
                        p.setStyle(styleBuilder.ol2(line1.getStyle(), findCount(Tag.QUOTA, p, 1) - 1, line1.getAttr(), line1.getCount()));
                    } else {
                        while (p != null && p.getType() == Line.LINE_TYPE_QUOTA) {
                            p.setStyle(styleBuilder.quota(line1.getStyle()));
                            p = p.parentLine();
                        }
                    }

                } else {
                    line.addNext(line1);
                    queue.next();
                    ol(queue.currLine(), nextLevel);
                }

                return true;
            }

            CharSequence userText;
            if (h(line1)) {
                userText = line1.getStyle();
            } else {
                userText = line1.getSource();
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            line.setStyle(builder);
            inline(line);
            if (!inQuota) {
                line.setStyle(styleBuilder.ul(line.getStyle(), line.getAttr()));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean ol(Line line) {
        return ol(line, 0);
    }

    private boolean ol(Line line, int level) {
        Matcher matcher = obtain(Tag.OL, line.getSource());
        if (matcher.find()) {
            line.setType(Line.LINE_TYPE_OL);
            Line line1 = new Line(matcher.group(1));
            line.setAttr(0);

            Line parent = line.parentLine();
            LineQueue queue = queueProvider.getQueue();
            Line prev = line.prevLine();

            boolean inQuota = queue.currLine().getType() == Line.LINE_TYPE_QUOTA;
            if (inQuota) {
                line.setHandle(Line.HANDLE_BY_ROOT);
                line.setData(Line.LINE_TYPE_OL);
            }

            if (prev != null && (prev.getType() == Line.LINE_TYPE_OL || prev.getType() == Line.LINE_TYPE_UL)) {
                if (level > 0) {
                    line.setAttr(level);
                } else {
                    String m = line.getSource().substring(matcher.start(), matcher.start(1) - 2);
                    m = m.replaceAll("\\t", "    ");
                    if (m.length() > prev.getAttr() * 2 + 1)
                        line.setAttr(prev.getAttr() + 1);
                    else
                        line.setAttr(m.length() / 2);
                }

            }
            if (prev != null && prev.getType() == Line.LINE_TYPE_OL && prev.getAttr() == line.getAttr()) {
                line.setCount(prev.getCount() + 1);
            } else {
                line.setCount(1);
            }
            if (inQuota) {
                line.setStyle(" ");
            } else {
                line.setStyle(styleBuilder.ol(" ", line.getAttr(), line.getCount()));
            }
            if (find(Tag.UL, line1)) {
                int nextLevel = line.getAttr() + 1;
                line1.unAttachFromParent();

                if (parent != null) {
                    Line p = parent.copyToNext();
                    p.addChild(line1);
                    queue.next();
                    ul(line1, nextLevel);
                    if (inQuota) {
                        while (p.parentLine() != null) {
                            p = p.parentLine();
                        }
                        p.setStyle(styleBuilder.ul2(line1.getStyle(), findCount(Tag.QUOTA, p, 1) - 1, line1.getAttr()));
                    } else {
                        while (p != null && p.getType() == Line.LINE_TYPE_QUOTA) {
                            p.setStyle(styleBuilder.quota(line1.getStyle()));
                            p = p.parentLine();
                        }
                    }
                } else {
                    line.addNext(line1);
                    queue.next();
                    ul(queue.currLine(), nextLevel);
                }

                return true;
            }
            if (find(Tag.OL, line1)) {
                int nextLevel = line.getAttr() + 1;
                line1.unAttachFromParent();

                if (parent != null) {
                    Line p = parent.copyToNext();
                    p.addChild(line1);
                    queue.next();
                    ol(line1, nextLevel);
                    if (inQuota) {
                        while (p.parentLine() != null) {
                            p = p.parentLine();
                        }
                        p.setStyle(styleBuilder.ol2(line1.getStyle(), findCount(Tag.QUOTA, p, 1) - 1, line1.getAttr(), line1.getCount()));
                    } else {
                        while (p != null && p.getType() == Line.LINE_TYPE_QUOTA) {
                            p.setStyle(styleBuilder.quota(line1.getStyle()));
                            p = p.parentLine();
                        }
                    }
                } else {
                    line.addNext(line1);
                    queue.next();
                    ol(queue.currLine(), nextLevel);
                }

                return true;
            }

            CharSequence userText;
            if (h(line1)) {
                userText = line1.getStyle();
            } else {
                userText = line1.getSource();
            }
            SpannableStringBuilder builder;
            if (userText instanceof SpannableStringBuilder) {
                builder = (SpannableStringBuilder) userText;
            } else {
                builder = new SpannableStringBuilder(userText);
            }
            line.setStyle(builder);
            inline(line);

            if (!inQuota) {
                line.setStyle(styleBuilder.ol(line.getStyle(), line.getAttr(), line.getCount()));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean gap(Line line) {
        line = line.get();
        Matcher matcher = obtain(Tag.GAP, line.getSource());
        if (matcher.matches()) {
            line.setType(Line.LINE_TYPE_GAP);
            line.setStyle(styleBuilder.gap());
            return true;
        }
        return false;
    }

    @Override
    public boolean em(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.EM, builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.em(sb));
            em(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean italic(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.ITALIC, builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.italic(sb));
            italic(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean emItalic(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.EM_ITALIC, builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.emItalic(sb));
            emItalic(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean code(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.CODE, builder);
        if (matcher.find()) {
            String content = matcher.group(3);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.code(content));
            code(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean email(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.EMAIL, builder);
        if (matcher.find()) {
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(2), matcher.end(2));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.email(sb));
            email(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.DELETE, builder);
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            if (checkInCode(builder, start, end)) {
                continue;
            }
            SpannableStringBuilder sb = (SpannableStringBuilder) builder.subSequence(matcher.start(3), matcher.end(3));
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.delete(sb));
            delete(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean autoLink(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.AUTO_LINK, builder);
        boolean m = false;
        while (matcher.find()) {
            String content = matcher.group();
            builder.delete(matcher.start(), matcher.end());
            builder.insert(matcher.start(), styleBuilder.link(content, content, ""));
            m = true;
        }
        return m;
    }

    @Override
    public boolean link(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.LINK, builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(3);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.link(title, link, hint));
            link(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean link2(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.LINK2, builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String id = matcher.group(3);
            Pair<String, String> link = idLinkLinks.get(id);
            if (link != null) {
                builder.delete(matcher.start(1), matcher.end(1));
                builder.insert(matcher.start(1), styleBuilder.link(title, link.first, link.second));
            } else {
                return false;
            }
            link2(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean linkId(String line) {
        Matcher matcher = obtain(Tag.LINK_ID, line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String link = matcher.group(2);
            String hint = matcher.group(5);
            idLinkLinks.put(id, new Pair<>(link, hint));
            return true;
        }
        return false;
    }

    @Override
    public boolean image(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.IMAGE, builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String link = matcher.group(3);
            String hint = matcher.group(6);
            builder.delete(matcher.start(1), matcher.end(1));
            builder.insert(matcher.start(1), styleBuilder.image(title, link, hint));
            image(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean image2(Line line) {
        line = line.get();
        SpannableStringBuilder builder = (SpannableStringBuilder) line.getStyle();
        Matcher matcher = obtain(Tag.IMAGE2, builder);
        if (matcher.find()) {
            String title = matcher.group(2);
            String id = matcher.group(3);
            Pair<String, String> image = idImageUrl.get(id);
            if (image != null) {
                builder.delete(matcher.start(1), matcher.end(1));
                builder.insert(matcher.start(1), styleBuilder.image(title, image.first, image.second));
            } else {
                return false;
            }
            image2(line);
            return true;
        }
        return false;
    }

    @Override
    public boolean imageId(String line) {
        Matcher matcher = obtain(Tag.IMAGE_ID, line);
        if (matcher.find()) {
            String id = matcher.group(1);
            String link = matcher.group(2);
            String hint = matcher.group(5);
            idImageUrl.put(id, new Pair<>(link, hint));
            return true;
        }
        return false;
    }

    @Override
    public boolean codeBlock1(Line line) {
        Matcher matcher = obtain(Tag.CODE_BLOCK_1, line.getSource());
        if (matcher.find()) {
            String content = matcher.group(2);
            LineQueue queue = queueProvider.getQueue();
            Line next = queue.nextLine();
            StringBuilder sb = new StringBuilder(content);
            StringBuilder bsb = new StringBuilder();

            while (next != null) {
                CharSequence r = get(Tag.CODE_BLOCK_1, next, 2);
                if (r == null) {
                    if (find(Tag.BLANK, next)) {
                        bsb.append(' ').append('\n');
                    } else {
                        break;
                    }
                } else {
                    if (bsb.length() != 0) {
                        sb.append(bsb).append(r);
                        bsb.delete(0, sb.length());
                    } else {
                        sb.append('\n').append(r);
                    }
                }
                queue.removeNextLine();
                next = queue.nextLine();
            }


            line.setType(Line.LINE_TYPE_CODE_BLOCK_1);
            line.setStyle(styleBuilder.codeBlock(sb.toString()));
            return true;
        }
        return false;
    }


    @Override
    public boolean codeBlock2(Line line) {
        if (find(Tag.CODE_BLOCK_2, line)) {
            LineQueue queue = queueProvider.getQueue();
            LineQueue nextQueue = queue.copy();
            boolean find = false;
            while (nextQueue.nextLine() != null) {
                if (find(Tag.CODE_BLOCK_2, nextQueue.nextLine())) {
                    nextQueue.next();
                    removePrevBlankLine(nextQueue);
                    removeNextBlankLine(queue);
                    find = true;
                    break;
                }
                nextQueue.next();
            }
            if (find) {
                StringBuilder sb = new StringBuilder();
                queue.next();
                queue.removePrevLine();
                while (queue.currLine() != nextQueue.currLine()) {
                    sb.append(queue.currLine().getSource()).append('\n');
                    queue.next();
                    queue.removePrevLine();
                }
                removeNextBlankLine(nextQueue);
                nextQueue.currLine().setType(Line.LINE_TYPE_CODE_BLOCK_2);
                nextQueue.currLine().setStyle(styleBuilder.codeBlock(sb.toString()));
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean inline(Line line) {
        boolean flag = code(line);
        flag = emItalic(line) || flag;
        flag = em(line) || flag;
        flag = italic(line) || flag;
        flag = delete(line) || flag;
        flag = email(line) || flag;
        flag = image(line) || flag;
        flag = image2(line) || flag;
        flag = link(line) || flag;
        flag = link2(line) || flag;
        flag = autoLink(line) || flag;
        return flag;
    }

    @Override
    public boolean find(int tag, Line line) {
        return line != null && find(tag, line.getSource());
    }

    @Override
    public boolean find(int tag, String line) {
        if (line == null) {
            return false;
        }
        Matcher m = obtain(tag, line);
        return m != null && m.find();
    }

    @Override
    public int findCount(int tag, Line line, int group) {
        return line == null ? 0 : findCount(tag, line.getSource(), group);
    }

    @Override
    public int findCount(int tag, String line, int group) {
        if (line == null) {
            return 0;
        }

        Matcher matcher = obtain(tag, line);
        if (matcher == null) {
            return 0;
        }
        if (matcher.find()) {
            return findCount(tag, matcher.group(group), group) + 1;
        }

        return 0;
    }

    private boolean checkInCode(SpannableStringBuilder builder, int start, int end) {
        CodeSpan[] css = builder.getSpans(0, builder.length(), CodeSpan.class);
        for (CodeSpan cs : css) {
            int c_start = builder.getSpanStart(cs);
            int c_end = builder.getSpanEnd(cs);
            if (!(c_start >= end || c_end <= start)) {
                return true;
            }
        }
        return false;
    }

    private Matcher obtain(int tag, CharSequence src) {
        Matcher m = matchers.get(tag, null);
        if (m != null) {
            m.reset(src);
        }
        return m;
    }

    public void setQueueProvider(QueueProvider queueProvider) {
        this.queueProvider = queueProvider;
    }


    @Override
    public CharSequence get(int tag, Line line, int group) {
        return get(tag, line.getSource(), group);
    }

    @Override
    public CharSequence get(int tag, CharSequence line, int group) {
        Matcher m = obtain(tag, line);
        return m.find() ? m.group(group) : null;
    }

    private void removeNextBlankLine(LineQueue queue) {
        while (queue.nextLine() != null) {
            if (find(Tag.BLANK, queue.nextLine())) {
                queue.removeNextLine();
            } else {
                return;
            }
        }
    }

    private void removePrevBlankLine(LineQueue queue) {
        while (queue.prevLine() != null) {
            if (find(Tag.BLANK, queue.prevLine())) {
                queue.removePrevLine();
            } else {
                return;
            }
        }
    }


}
