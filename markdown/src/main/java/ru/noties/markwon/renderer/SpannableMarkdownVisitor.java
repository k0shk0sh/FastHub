package ru.noties.markwon.renderer;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Log;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.ext.gfm.tables.TableBody;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.CustomBlock;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.Link;
import org.commonmark.node.ListBlock;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.node.ThematicBreak;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import ru.noties.markwon.SpannableBuilder;
import ru.noties.markwon.SpannableConfiguration;
import ru.noties.markwon.extension.emoji.Emoji;
import ru.noties.markwon.extension.emoji.loader.EmojiManager;
import ru.noties.markwon.extension.emoji.loader.EmojiModel;
import ru.noties.markwon.renderer.html.SpannableHtmlParser;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.spans.AsyncDrawableSpan;
import ru.noties.markwon.spans.BlockQuoteSpan;
import ru.noties.markwon.spans.BulletListItemSpan;
import ru.noties.markwon.spans.CodeSpan;
import ru.noties.markwon.spans.EmphasisSpan;
import ru.noties.markwon.spans.HeadingSpan;
import ru.noties.markwon.spans.LinkSpan;
import ru.noties.markwon.spans.OrderedListItemSpan;
import ru.noties.markwon.spans.StrongEmphasisSpan;
import ru.noties.markwon.spans.TableRowSpan;
import ru.noties.markwon.spans.TaskListSpan;
import ru.noties.markwon.spans.ThematicBreakSpan;
import ru.noties.markwon.tasklist.TaskListBlock;
import ru.noties.markwon.tasklist.TaskListItem;

@SuppressWarnings("WeakerAccess")
public class SpannableMarkdownVisitor extends AbstractVisitor {

    private final SpannableConfiguration configuration;
    private final SpannableBuilder builder;
    private final Deque<HtmlInlineItem> htmlInlineItems;

    private int blockQuoteIndent;
    private int listLevel;

    private List<TableRowSpan.Cell> pendingTableRow;
    private boolean tableRowIsHeader;
    private int tableRows;

    public SpannableMarkdownVisitor(
            @NonNull SpannableConfiguration configuration,
            @NonNull SpannableBuilder builder
    ) {
        this.configuration = configuration;
        this.builder = builder;
        this.htmlInlineItems = new ArrayDeque<>(2);
    }

    @Override
    public void visit(Text text) {
        builder.append(text.getLiteral());
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        final int length = builder.length();
        visitChildren(strongEmphasis);
        setSpan(length, new StrongEmphasisSpan());
    }

    @Override
    public void visit(Emphasis emphasis) {
        final int length = builder.length();
        visitChildren(emphasis);
        setSpan(length, new EmphasisSpan());
    }

    @Override
    public void visit(BlockQuote blockQuote) {

        newLine();
        if (blockQuoteIndent != 0) {
            builder.append('\n');
        }

        final int length = builder.length();

        blockQuoteIndent += 1;

        visitChildren(blockQuote);

        setSpan(length, new BlockQuoteSpan(configuration.theme()));

        blockQuoteIndent -= 1;

        newLine();
        if (blockQuoteIndent == 0) {
            builder.append('\n');
        }
    }

    @Override
    public void visit(Code code) {

        final int length = builder.length();

        // NB, in order to provide a _padding_ feeling code is wrapped inside two unbreakable spaces
        // unfortunately we cannot use this for multiline code as we cannot control where a new line break will be inserted
        builder.append('\u00a0');
        builder.append(code.getLiteral());
        builder.append('\u00a0');

        setSpan(length, new CodeSpan(
                configuration.theme(),
                false
        ));
    }

    @Override
    public void visit(FencedCodeBlock fencedCodeBlock) {

        newLine();

        final int length = builder.length();

        // empty lines on top & bottom
        builder.append('\u00a0').append('\n');
        builder.append(
                configuration.syntaxHighlight()
                        .highlight(fencedCodeBlock.getInfo(), fencedCodeBlock.getLiteral())
        );
        builder.append('\u00a0').append('\n');

        setSpan(length, new CodeSpan(
                configuration.theme(),
                true
        ));

        newLine();
        builder.append('\n');
    }

    @Override
    public void visit(BulletList bulletList) {
        visitList(bulletList);
    }

    @Override
    public void visit(OrderedList orderedList) {
        visitList(orderedList);
    }

    private void visitList(Node node) {
        newLine();
        visitChildren(node);
        newLine();
        if (listLevel == 0 && blockQuoteIndent == 0) {
            builder.append('\n');
        }
    }

    @Override
    public void visit(ListItem listItem) {

        final int length = builder.length();

        blockQuoteIndent += 1;
        listLevel += 1;

        final Node parent = listItem.getParent();
        if (parent instanceof OrderedList) {

            final int start = ((OrderedList) parent).getStartNumber();

            visitChildren(listItem);

            // todo| in order to provide real RTL experience there must be a way to provide this string
            setSpan(length, new OrderedListItemSpan(
                    configuration.theme(),
                    String.valueOf(start) + "." + '\u00a0'
            ));

            // after we have visited the children increment start number
            final OrderedList orderedList = (OrderedList) parent;
            orderedList.setStartNumber(orderedList.getStartNumber() + 1);

        } else {

            visitChildren(listItem);

            setSpan(length, new BulletListItemSpan(
                    configuration.theme(),
                    listLevel - 1
            ));
        }

        blockQuoteIndent -= 1;
        listLevel -= 1;

        newLine();
    }

    @Override
    public void visit(ThematicBreak thematicBreak) {

        newLine();

        final int length = builder.length();
        builder.append(' '); // without space it won't render
        setSpan(length, new ThematicBreakSpan(configuration.theme()));

        newLine();
        builder.append('\n');
    }

    @Override
    public void visit(Heading heading) {

        newLine();

        final int length = builder.length();
        visitChildren(heading);
        setSpan(length, new HeadingSpan(configuration.theme(), heading.getLevel()));

        newLine();

        // after heading we add another line anyway (no additional checks)
        builder.append('\n');
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        // at first here was a new line, but here should be a space char
        builder.append(' ');
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        newLine();
    }

    /**
     * @since 1.0.1
     */
    @Override
    public void visit(CustomBlock customBlock) {
        if (customBlock instanceof TaskListBlock) {
            blockQuoteIndent += 1;
            visitChildren(customBlock);
            blockQuoteIndent -= 1;
            newLine();
            builder.append('\n');
        } else {
            super.visit(customBlock);
        }
    }

    @Override
    public void visit(CustomNode customNode) {
        if (customNode instanceof Emoji) {
            Emoji emoji = (Emoji) customNode;
            visitChildren(customNode);
            visitEmoji(emoji);
        } else if (customNode instanceof Strikethrough) {
            final int length = builder.length();
            visitChildren(customNode);
            setSpan(length, new StrikethroughSpan());

        } else if (customNode instanceof TaskListItem) {

            // new in 1.0.1

            final TaskListItem listItem = (TaskListItem) customNode;

            final int length = builder.length();

            blockQuoteIndent += listItem.indent();

            visitChildren(customNode);

            setSpan(length, new TaskListSpan(
                    configuration.theme(),
                    blockQuoteIndent,
                    listItem.done()
            ));

            newLine();

            blockQuoteIndent -= listItem.indent();

        }
        if (!handleTableNodes(customNode)) {
            super.visit(customNode);
        }
    }

    private boolean handleTableNodes(CustomNode node) {
        Log.e("node", node.getClass().getSimpleName());
        final boolean handled;

        if (node instanceof TableBody) {
            visitChildren(node);
            tableRows = 0;
            handled = true;
            newLine();
            builder.append('\n');
        } else if (node instanceof TableRow) {

            final int length = builder.length();
            visitChildren(node);

            if (pendingTableRow != null) {
                builder.append(' ');

                final TableRowSpan span = new TableRowSpan(
                        configuration.theme(),
                        pendingTableRow,
                        tableRowIsHeader,
                        tableRows % 2 == 1
                );

                tableRows = tableRowIsHeader
                            ? 0
                            : tableRows + 1;

                setSpan(length, span);
                newLine();
                pendingTableRow = null;
            }

            handled = true;
        } else if (node instanceof TableCell) {

            final TableCell cell = (TableCell) node;
            final int length = builder.length();
            visitChildren(cell);
            if (pendingTableRow == null) {
                pendingTableRow = new ArrayList<>(2);
            }

            pendingTableRow.add(new TableRowSpan.Cell(
                    tableCellAlignment(cell.getAlignment()),
                    builder.removeFromEnd(length)
            ));

            tableRowIsHeader = cell.isHeader();

            handled = true;
        } else {
            handled = false;
        }
        return handled;
    }

    @Override
    public void visit(Paragraph paragraph) {

        final boolean inTightList = isInTightList(paragraph);

        if (!inTightList) {
            newLine();
        }

        visitChildren(paragraph);

        if (!inTightList) {
            newLine();

            if (blockQuoteIndent == 0) {
                builder.append('\n');
            }
        }
    }

    @Override
    public void visit(Image image) {

        final int length = builder.length();

        visitChildren(image);

        // we must check if anything _was_ added, as we need at least one char to render
        if (length == builder.length()) {
            builder.append('\uFFFC');
        }

        final Node parent = image.getParent();
        final boolean link = parent != null && parent instanceof Link;
        final String destination = configuration.urlProcessor().process(image.getDestination());

        setSpan(
                length,
                new AsyncDrawableSpan(
                        configuration.theme(),
                        new AsyncDrawable(
                                destination,
                                configuration.asyncDrawableLoader()
                        ),
                        AsyncDrawableSpan.ALIGN_BOTTOM,
                        link
                )
        );

        // todo, maybe, if image is not inside a link, we should make it clickable, so
        // user can open it in external viewer?
    }

    @Override
    public void visit(HtmlBlock htmlBlock) {
        // http://spec.commonmark.org/0.18/#html-blocks
        final Spanned spanned = configuration.htmlParser().getSpanned(null, htmlBlock.getLiteral());
        if (!TextUtils.isEmpty(spanned)) {
            builder.append(spanned);
        }
    }

    @Override
    public void visit(HtmlInline htmlInline) {

        final SpannableHtmlParser htmlParser = configuration.htmlParser();
        final SpannableHtmlParser.Tag tag = htmlParser.parseTag(htmlInline.getLiteral());

        if (tag != null) {

            final boolean voidTag = tag.voidTag();
            if (!voidTag && tag.opening()) {
                // push in stack
                htmlInlineItems.push(new HtmlInlineItem(tag, builder.length()));
                visitChildren(htmlInline);
            } else {

                if (!voidTag) {
                    if (htmlInlineItems.size() > 0) {
                        final HtmlInlineItem item = htmlInlineItems.pop();
                        final Object span = htmlParser.getSpanForTag(item.tag);
                        if (span != null) {
                            setSpan(item.start, span);
                        }
                    }
                } else {

                    final Spanned html = htmlParser.getSpanned(tag, htmlInline.getLiteral());
                    if (!TextUtils.isEmpty(html)) {
                        builder.append(html);
                    }

                }
            }
        } else {
            // todo, should we append just literal?
//            builder.append(htmlInline.getLiteral());
            visitChildren(htmlInline);
        }
    }

    @Override
    public void visit(Link link) {
        final int length = builder.length();
        visitChildren(link);
        final String destination = configuration.urlProcessor().process(link.getDestination());
        setSpan(length, new LinkSpan(configuration.theme(), destination, configuration.linkResolver()));
    }

    private void visitEmoji(Emoji emoji) {
        EmojiModel unicode = EmojiManager.getForAlias(emoji.getEmoji());
        if (unicode != null && unicode.getUnicode() != null) {
            builder.append(unicode.getUnicode());
        }
    }

    private void setSpan(int start, @NonNull Object span) {
        builder.setSpan(span, start, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void newLine() {
        if (builder.length() > 0
                && '\n' != builder.lastChar()) {
            builder.append('\n');
        }
    }

    private boolean isInTightList(Paragraph paragraph) {
        final Node parent = paragraph.getParent();
        if (parent != null) {
            final Node gramps = parent.getParent();
            if (gramps != null && gramps instanceof ListBlock) {
                ListBlock list = (ListBlock) gramps;
                return list.isTight();
            }
        }
        return false;
    }

    @TableRowSpan.Alignment
    private static int tableCellAlignment(TableCell.Alignment alignment) {
        final int out;
        if (alignment != null) {
            switch (alignment) {
                case CENTER:
                    out = TableRowSpan.ALIGN_CENTER;
                    break;
                case RIGHT:
                    out = TableRowSpan.ALIGN_RIGHT;
                    break;
                default:
                    out = TableRowSpan.ALIGN_LEFT;
                    break;
            }
        } else {
            out = TableRowSpan.ALIGN_LEFT;
        }
        return out;
    }

    private static class HtmlInlineItem {

        final SpannableHtmlParser.Tag tag;
        final int start;

        HtmlInlineItem(SpannableHtmlParser.Tag tag, int start) {
            this.tag = tag;
            this.start = start;
        }
    }
}
