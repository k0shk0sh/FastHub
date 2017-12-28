package ru.noties.markwon.extension.gh;

import android.support.annotation.NonNull;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.PostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kosh on 20.12.17.
 */

public class GitHubIssueParser implements PostProcessor {

    private static final Pattern ISSUE_PATTERN = Pattern.compile("(\\s|\\A)#(\\w+)");
    private static final Pattern MENTION_PATTERN = Pattern.compile("(\\s|\\A)@(\\w+)");

    private final boolean forMention;

    public GitHubIssueParser(boolean forMention) {this.forMention = forMention;}

    @Override public Node process(Node node) {
        GitHubIssueVisitor gitHubIssueVistor = new GitHubIssueVisitor(forMention);
        node.accept(gitHubIssueVistor);
        return node;
    }

    private class GitHubIssueVisitor extends AbstractVisitor {
        int inLink = 0;
        private boolean forMention;

        GitHubIssueVisitor(boolean forMention) {
            this.forMention = forMention;
        }

        @Override public void visit(Link link) {
            inLink++;
            super.visit(link);
            inLink--;
        }

        @Override public void visit(Text text) {
            if (inLink == 0) {
                if (forMention) {
                    linkifyMention(text);
                } else {
                    linkifyHashTag(text);
                }
            }
        }

        private void linkifyHashTag(@NonNull Text text) {
            String literal = text.getLiteral();
            List<GitHubLinksSpan> links = result(literal, ISSUE_PATTERN);
            if (links == null) return;
            Node lastNode = text;
            int last = 0;
            for (GitHubLinksSpan link : links) {
                String linkText = literal.substring(link.getBeginIndex(), link.getEndIndex());
                if (link.getBeginIndex() != last) {
                    lastNode = insertNode(new Text(literal.substring(last, link.getBeginIndex())), lastNode);
                }
                Text contentNode = new Text(linkText);
                Link linkNode = new Link(linkText, null);
                linkNode.appendChild(contentNode);
                lastNode = insertNode(linkNode, lastNode);
                last = link.getEndIndex();
            }
            if (last != literal.length()) {
                insertNode(new Text(literal.substring(last)), lastNode);
            }
            text.unlink();
        }

        private void linkifyMention(@NonNull Text text) {
            String literal = text.getLiteral();
            List<GitHubLinksSpan> links = result(literal, MENTION_PATTERN);
            if (links == null) return;
            Node lastNode = text;
            int last = 0;
            for (GitHubLinksSpan link : links) {
                String linkText = literal.substring(link.getBeginIndex(), link.getEndIndex());
                if (link.getBeginIndex() != last) {
                    lastNode = insertNode(new Text(literal.substring(last, link.getBeginIndex())), lastNode);
                }
                Text contentNode = new Text(linkText);
                Link linkNode = new Link(linkText, null);
                linkNode.appendChild(contentNode);
                lastNode = insertNode(linkNode, lastNode);
                last = link.getEndIndex();
            }
            if (last != literal.length()) {
                insertNode(new Text(literal.substring(last)), lastNode);
            }
            text.unlink();
        }

        private Node insertNode(Node node, Node insertAfterNode) {
            insertAfterNode.insertAfter(node);
            return node;
        }

        private List<GitHubLinksSpan> result(@NonNull String input, @NonNull Pattern pattern) {
            List<GitHubLinksSpan> linkSpans = new ArrayList<>();
            Matcher matcher = pattern.matcher(input);
            boolean m = matcher.find();
            if (m) {
                MatchResult result = matcher.toMatchResult();
                final int max = matcher.groupCount() - 1;
                for (int i = 0; i < max; i++) {
                    String group = matcher.group(i);
                    if (group == null) continue;
                    linkSpans.add(new GitHubLinksSpan(result.start(i), result.end(i)));
                }
            }
            return linkSpans;
        }
    }

    private class GitHubLinksSpan {
        private int beginIndex;
        private int endIndex;

        public GitHubLinksSpan(int beginIndex, int endIndex) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }
}
