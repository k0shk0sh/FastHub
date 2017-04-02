package com.zzhoujay.markdown;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.zzhoujay.markdown.parser.Line;
import com.zzhoujay.markdown.parser.LineQueue;
import com.zzhoujay.markdown.parser.QueueConsumer;
import com.zzhoujay.markdown.parser.StyleBuilder;
import com.zzhoujay.markdown.parser.Tag;
import com.zzhoujay.markdown.parser.TagHandler;
import com.zzhoujay.markdown.parser.TagHandlerImpl;
import com.zzhoujay.markdown.style.ScaleHeightSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhou on 16-6-25.
 */
class MarkDownParser {


    private BufferedReader reader;
    private TagHandler tagHandler;

    MarkDownParser(BufferedReader reader, StyleBuilder styleBuilder) {
        this.reader = reader;
        tagHandler = new TagHandlerImpl(styleBuilder);
    }

    MarkDownParser(InputStream inputStream, StyleBuilder styleBuilder) {
        this(new BufferedReader(new InputStreamReader(inputStream)), styleBuilder);
    }

    MarkDownParser(String text, StyleBuilder styleBuilder) {
        this(new BufferedReader(new StringReader(text)), styleBuilder);
    }


    public Spannable parse() throws IOException {
        LineQueue queue = collect();
        return parse(queue);
    }

    /**
     * 收集 String -> LineQueue
     *
     * @return LineQueue
     * @throws IOException
     */
    private LineQueue collect() throws IOException {
        String line;
        Line root = null;
        LineQueue queue = null;
        while ((line = reader.readLine()) != null) {
            if (!(tagHandler.imageId(line) || tagHandler.linkId(line))) {
                Line l = new Line(line);
                if (root == null) {
                    root = l;
                    queue = new LineQueue(root);
                } else {
                    queue.append(l);
                }
            }
        }
        return queue;
    }

    /**
     * 解析LineQueue
     *
     * @param queue LineQueue
     * @return Spanned
     */
    private Spannable parse(final LineQueue queue) {
        tagHandler.setQueueProvider(new QueueConsumer.QueueProvider() {
            @Override
            public LineQueue getQueue() {
                return queue;
            }
        });
        removeCurrBlankLine(queue);
        boolean notBlock;// 当前Line不是CodeBlock
        do {

            notBlock = queue.prevLine() != null && (queue.prevLine().getType() == Line.LINE_TYPE_OL || queue.prevLine().getType() == Line.LINE_TYPE_UL)
                    && (tagHandler.find(Tag.UL, queue.currLine()) || tagHandler.find(Tag.OL, queue.currLine()));
            // 处理CodeBlock
            if (!notBlock && (tagHandler.codeBlock1(queue.currLine()) || tagHandler.codeBlock2(queue.currLine()))) {
                continue;
            }
            // 合并未换行的Line，并处理一些和Quota嵌套相关的问题
            boolean isNewLine = tagHandler.find(Tag.NEW_LINE, queue.currLine()) || tagHandler.find(Tag.GAP, queue.currLine()) || tagHandler.find(Tag.H, queue.currLine());
            if (isNewLine) {
                if (queue.nextLine() != null)
                    handleQuotaRelevant(queue, true);
                removeNextBlankLine(queue);
            } else {
                while (queue.nextLine() != null && !removeNextBlankLine(queue)) {
                    if (tagHandler.find(Tag.CODE_BLOCK_1, queue.nextLine()) || tagHandler.find(Tag.CODE_BLOCK_2, queue.nextLine()) ||
                            tagHandler.find(Tag.GAP, queue.nextLine()) || tagHandler.find(Tag.UL, queue.nextLine()) ||
                            tagHandler.find(Tag.OL, queue.nextLine()) || tagHandler.find(Tag.H, queue.nextLine())) {
                        break;
                    }
                    if (handleQuotaRelevant(queue, false)) break;
                }
                removeNextBlankLine(queue);
            }
            // 解析style
            if (tagHandler.gap(queue.currLine()) || tagHandler.quota(queue.currLine()) || tagHandler.ol(queue.currLine()) ||
                    tagHandler.ul(queue.currLine()) || tagHandler.h(queue.currLine())) {
                continue;
            }
            queue.currLine().setStyle(SpannableStringBuilder.valueOf(queue.currLine().getSource()));
            tagHandler.inline(queue.currLine());
        } while (queue.next());
        return merge(queue);
    }

    /**
     * 处理Quota嵌套相关问题
     *
     * @param queue LineQueue
     * @param onlyH 只处理Title相关的问题
     * @return true：已处理
     */
    private boolean handleQuotaRelevant(LineQueue queue, boolean onlyH) {
        int nextQuotaCount = tagHandler.findCount(Tag.QUOTA, queue.nextLine(), 1);
        int currQuotaCount = tagHandler.findCount(Tag.QUOTA, queue.currLine(), 1);
        if (nextQuotaCount > 0 && nextQuotaCount > currQuotaCount) {
            return true;
        } else {
            String source = queue.nextLine().getSource();
            if (nextQuotaCount > 0) {
                source = source.replaceFirst("^\\s{0,3}(>\\s+){" + nextQuotaCount + "}", "");
            }
            if (currQuotaCount == nextQuotaCount) {
                if (findH1_2(queue, currQuotaCount, source)) return true;
                if (findH2_2(queue, currQuotaCount, source)) return true;
            }
            if (onlyH) {
                return false;
            }
            if (tagHandler.find(Tag.UL, source) || tagHandler.find(Tag.OL, source) || tagHandler.find(Tag.H, source)) {
                return true;
            } else {
                queue.currLine().setSource(queue.currLine().getSource() + ' ' + source);
                queue.removeNextLine();
            }
        }
        return false;
    }

    private boolean findH2_2(LineQueue queue, int currQuotaCount, String source) {
        if (tagHandler.find(Tag.H2_2, source)) {
            String currLineSource = queue.currLine().getSource();
            Matcher m = Pattern.compile("^\\s{0,3}(>\\s+?){" + currQuotaCount + "}(.*)").matcher(queue.currLine().getSource());
            String newCurrLineSource;
            if (m.find()) {
                int start = m.start(2);
                int end = m.end(2);
                newCurrLineSource = currLineSource.substring(0, start) + "## " + currLineSource.subSequence(start, end);
            } else {
                newCurrLineSource = "## " + currLineSource;
            }
            queue.currLine().setSource(newCurrLineSource);
            queue.removeNextLine();
            return true;
        }
        return false;
    }

    private boolean findH1_2(LineQueue queue, int currQuotaCount, String source) {
        if (tagHandler.find(Tag.H1_2, source)) {
            String currLineSource = queue.currLine().getSource();
            Matcher m = Pattern.compile("^\\s{0,3}(>\\s+?){" + currQuotaCount + "}(.*)").matcher(currLineSource);
            String newCurrLineSource;
            if (m.find()) {
                int start = m.start(2);
                int end = m.end(2);
                newCurrLineSource = currLineSource.substring(0, start) + "# " + currLineSource.subSequence(start, end);
            } else {
                newCurrLineSource = "# " + currLineSource;
            }
            queue.currLine().setSource(newCurrLineSource);
            queue.removeNextLine();
            return true;
        }
        return false;
    }

    /**
     * 合并LineQueue -> Spanned
     *
     * @param queue LineQueue
     * @return Spanned
     */
    private Spannable merge(LineQueue queue) {
        queue.reset();
        SpannableStringBuilder builder = new SpannableStringBuilder();
        do {
            Line curr = queue.currLine();
            Line next = queue.nextLine();
            builder.append(curr.getStyle());
            if (next == null) {
                break;
            }
            builder.append('\n');
            switch (curr.getType()) {
                case Line.LINE_TYPE_QUOTA:
                    if (next.getType() != Line.LINE_TYPE_QUOTA) {
                        builder.append('\n');
                    }
                    break;
                case Line.LINE_TYPE_UL:
                    if (next.getType() == Line.LINE_TYPE_UL)
                        builder.append(listMarginBottom());
                    builder.append('\n');
                    break;
                case Line.LINE_TYPE_OL:
                    if (next.getType() == Line.LINE_TYPE_OL) {
                        builder.append(listMarginBottom());
                    }
                    builder.append('\n');
                    break;
                default:
                    builder.append('\n');
            }
        } while (queue.next());
        return builder;
    }

    /**
     * 从下个Line开始移除空Line
     *
     * @param queue LineQueue
     * @return 是否移除了
     */
    private boolean removeNextBlankLine(LineQueue queue) {
        boolean flag = false;
        while (queue.nextLine() != null) {
            if (tagHandler.find(Tag.BLANK, queue.nextLine())) {
                queue.removeNextLine();
                flag = true;
            } else {
                break;
            }
        }
        return flag;
    }

    /**
     * 从当前行开始移除空Line
     *
     * @param queue LineQueue
     * @return true：移除了Line
     */
    private boolean removeCurrBlankLine(LineQueue queue) {
        boolean flag = false;
        while (queue.currLine() != null) {
            if (tagHandler.find(Tag.BLANK, queue.currLine())) {
                queue.removeCurrLine();
                flag = true;
            } else {
                break;
            }
        }
        return flag;
    }


    private SpannableString listMarginBottom() {
        SpannableString ss = new SpannableString(" ");
        ss.setSpan(new ScaleHeightSpan(0.4f), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

}
