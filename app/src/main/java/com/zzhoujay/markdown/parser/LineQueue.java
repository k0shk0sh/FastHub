package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-2.
 */
public class LineQueue {

    private Line root; // 根节点
    private Line curr; // 当前节点
    private Line last; // 尾节点

    public LineQueue(Line root) {
        this.root = root;
        curr = root;
        last = root;
        while (last.nextLine() != null) {
            last = last.nextLine();
        }
    }

    private LineQueue(LineQueue queue, Line curr) {
        this.root = queue.root;
        this.last = queue.last;
        this.curr = curr;
    }

    public Line nextLine() {
        return curr.nextLine();
    }

    public Line prevLine() {
        return curr.prevLine();
    }

    public Line currLine() {
        return curr;
    }

    public boolean next() {
        if (curr.nextLine() == null) {
            return false;
        }
        curr = curr.nextLine();
        return true;
    }

    public boolean prev() {
        if (curr.prevLine() == null) {
            return false;
        }
        curr = currLine().prevLine();
        return true;
    }

    public boolean end() {
        return curr.nextLine() == null;
    }

    public boolean start() {
        return curr == root;
    }

    public void append(Line line) {
        last.add(line);
        last = line;
    }

    public void insert(Line line) {
        if (curr == last) {
            append(line);
        } else {
            curr.addNext(line);
        }
    }

    public Line removeCurrLine() {
        Line tmp;
        if (curr == last) {
            tmp = last.prevLine();
        } else {
            tmp = curr.nextLine();
            if (curr == root) {
                root = tmp;
            }
        }
        curr.remove();
        Line r = curr;
        curr = tmp;
        return r;
    }

    public void removeNextLine() {
        curr.removeNext();
    }

    public void removePrevLine() {
        if (root == curr.prevLine()) {
            root = curr;
        }
        curr.removePrev();
    }

    public LineQueue copy() {
        return new LineQueue(this, curr);
    }

    public LineQueue copyNext() {
        if (end()) {
            return null;
        }
        return new LineQueue(this, curr.nextLine());
    }


    public void reset() {
        curr = root;
    }

    @Override
    public String toString() {
        Line t = root;
        StringBuilder sb = new StringBuilder();
        int len = 0;
        while (t != null) {
            sb.append(t.toString()).append(",");
            t = t.nextLine();
            len++;
        }
        return "{" + sb.toString() + "}";

    }


}
