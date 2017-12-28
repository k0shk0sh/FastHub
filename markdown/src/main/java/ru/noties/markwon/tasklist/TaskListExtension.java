package ru.noties.markwon.tasklist;

import android.support.annotation.NonNull;

import org.commonmark.parser.Parser;

/**
 * @since 1.0.1
 */
public class TaskListExtension implements Parser.ParserExtension {

    @NonNull
    public static TaskListExtension create() {
        return new TaskListExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customBlockParserFactory(new TaskListBlockParser.Factory());
    }
}
