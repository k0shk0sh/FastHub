package ru.noties.markwon.tasklist;

import org.commonmark.node.CustomNode;

/**
 * @since 1.0.1
 */
@SuppressWarnings("WeakerAccess")
public class TaskListItem extends CustomNode {

    private boolean done;
    private int indent;

    public boolean done() {
        return done;
    }

    public TaskListItem done(boolean done) {
        this.done = done;
        return this;
    }

    public int indent() {
        return indent;
    }

    public TaskListItem indent(int indent) {
        this.indent = indent;
        return this;
    }
}
