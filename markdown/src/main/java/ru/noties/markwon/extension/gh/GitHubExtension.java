package ru.noties.markwon.extension.gh;


import org.commonmark.Extension;
import org.commonmark.parser.Parser;

/**
 * Created by kosh on 20/08/2017.
 */

public class GitHubExtension implements Parser.ParserExtension {
    private final boolean forMention;

    private GitHubExtension(boolean forMention) {this.forMention = forMention;}

    public static Extension create(boolean forMention) {
        return new GitHubExtension(forMention);
    }

    @Override public void extend(Parser.Builder parserBuilder) {
        parserBuilder.postProcessor(new GitHubIssueParser(forMention));
    }
}
