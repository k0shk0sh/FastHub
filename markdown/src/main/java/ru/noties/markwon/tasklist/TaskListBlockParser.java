package ru.noties.markwon.tasklist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.commonmark.node.Block;
import org.commonmark.parser.InlineParser;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess") class TaskListBlockParser extends AbstractBlockParser {

    private static final Pattern PATTERN = Pattern.compile("\\s*-\\s+\\[(x|X|\\s)\\]\\s+(.*)");
    //    private static final Pattern PATTERN = Pattern.compile("(\\s|\\A)#(\\w+)");

    private final TaskListBlock block = new TaskListBlock();

    private final List<Item> items = new ArrayList<>(3);

    private int indent = 0;

    TaskListBlockParser(@NonNull String startLine, int startIndent) {
        items.add(new Item(startLine, startIndent));
        indent = startIndent;
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {

        final BlockContinue blockContinue;

        final String line = line(parserState);

        final int currentIndent = parserState.getIndent();
        if (currentIndent > indent) {
            indent += 2;
        } else if (currentIndent < indent && indent > 1) {
            indent -= 2;
        }

        if (line != null
                && line.length() > 0
                && PATTERN.matcher(line).matches()) {
            blockContinue = BlockContinue.atIndex(parserState.getIndex());
        } else {
            blockContinue = BlockContinue.finished();
        }

        return blockContinue;
    }

    @Override
    public void addLine(CharSequence line) {
        if (length(line) > 0) {
            items.add(new Item(line.toString(), indent));
        }
    }

    @Override
    public void parseInlines(InlineParser inlineParser) {

        Matcher matcher;

        TaskListItem listItem;

        for (Item item : items) {
            matcher = PATTERN.matcher(item.line);
            if (!matcher.matches()) {
                continue;
            }
            listItem = new TaskListItem()
                    .done(isDone(matcher.group(1)))
                    .indent(item.indent / 2);
            inlineParser.parse(matcher.group(2), listItem);
            block.appendChild(listItem);
        }
    }

    static class Factory extends AbstractBlockParserFactory {

        @Override
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {

            final String line = line(state);

            if (line != null
                    && line.length() > 0
                    && PATTERN.matcher(line).matches()) {

                final int length = line.length();
                final int index = state.getIndex();
                final int atIndex = index != 0
                                    ? index + (length - index)
                                    : length;

                return BlockStart.of(new TaskListBlockParser(line, state.getIndent()))
                        .atIndex(atIndex);
            }

            return BlockStart.none();
        }
    }

    @Nullable
    private static String line(@NonNull ParserState state) {
        final CharSequence lineRaw = state.getLine();
        return lineRaw != null
               ? lineRaw.toString()
               : null;
    }

    private static int length(@Nullable CharSequence text) {
        return text != null
               ? text.length()
               : 0;
    }

    private static boolean isDone(@NonNull String value) {
        return "X".equals(value)
                || "x".equals(value);
    }

    private static class Item {

        final String line;
        final int indent;

        Item(@NonNull String line, int indent) {
            this.line = line;
            this.indent = indent;
        }
    }
}
