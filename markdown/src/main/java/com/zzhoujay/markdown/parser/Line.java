package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

/**
 * Created by zhou on 16-6-28.
 * 代表每一行文本
 */
public class Line {

    public static final int LINE_NORMAL = 0; // 普通文本
    public static final int LINE_TYPE_QUOTA = 1; // 引用
    public static final int LINE_TYPE_UL = 2; // 无序列表
    public static final int LINE_TYPE_OL = 3; // 有序列表
    public static final int LINE_TYPE_H1 = 4; // H1
    public static final int LINE_TYPE_H2 = 5; // H2
    public static final int LINE_TYPE_H3 = 6; // H3
    public static final int LINE_TYPE_H4 = 7; // H4
    public static final int LINE_TYPE_H5 = 8; // H5
    public static final int LINE_TYPE_H6 = 9; // H6
    public static final int LINE_TYPE_CODE_BLOCK_2 = 10; // 代码块1
    public static final int LINE_TYPE_CODE_BLOCK_1 = 11; // 代码块2
    public static final int LINE_TYPE_GAP = 12; // 间隔

    public static final int HANDLE_BY_ROOT=1;

    private Line prev; // 前一个节点
    private Line next; // 下一个节点
    private Line parent; // 父节点
    private Line child; // 子节点

    private String source; // 源文本
    private CharSequence style; // 样式
    private int type; // 种类
    private int count; // 数量
    private int attr; // 属性
    private int handle; // 0
    private int data;


    public Line(String source) {
        this.source = source;
        count = 1;
        type = LINE_NORMAL;
    }

    private Line(Line line) {
        this.source = line.source;
        this.count = line.count;
        this.attr = line.attr;
        if (line.style != null) {
            this.style = new SpannableStringBuilder(line.style);
        }
        this.type = line.type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setStyle(CharSequence style) {
        this.style = style;
    }

    public CharSequence getStyle() {
        return style;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public int getAttr() {
        return attr;
    }

    public Line get() {
        return this;
    }

    public Line nextLine() {
        return next;
    }

    public Line prevLine() {
        return prev;
    }

    public Line childLine() {
        return child;
    }

    public Line parentLine() {
        return parent;
    }

    /**
     * 添加为下一个节点
     *
     * @param line Line
     * @return Line
     */
    public Line addNext(Line line) {
        if (line == null) {
            next = null;
        } else {
            if (line.next != null) {
                line.next.prev = null;
            }
            line.next = next;
            if (next != null) {
                next.prev = line;
            }
            if (line.prev != null) {
                line.prev.next = null;
            }
            line.prev = this;
            next = line;
            if (child != null) {
                child.addNext(line.child);
            }
        }
        return line;
    }

    /**
     * 添加为上一个节点
     *
     * @param line Line
     * @return Line
     */
    public Line addPrev(Line line) {
        if (line == null) {
            prev = null;
        } else {
            if (line.prev != null) {
                line.prev.next = null;
            }
            line.prev = prev;
            if (prev != null) {
                prev.next = line;
            }
            if (line.next != null) {
                line.next.prev = null;
            }
            line.next = this;
            prev = line;
            if (child != null) {
                child.addPrev(line.child);
            }
        }
        return line;
    }

    public Line add(Line line) {
        return addNext(line);
    }

    /**
     * 删除当前节点，包括其子节点包（会导致链表断开）
     */
    private void delete() {
        if (child != null) {
            child.delete();
        }
        if (prev != null) {
            prev.next = null;
        }
        prev = null;
        if (next != null) {
            next.prev = null;
        }
        next = null;
    }

    /**
     * 移除当前节点，重新连上前后两个节点，包括其子节点（不会导致链表断开）
     */
    private void reduce() {
        if (child != null) {
            child.reduce();
        }
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
        next = null;
        prev = null;
    }

    /**
     * 移除当前节点并且不会导致主链表断开
     */
    public void remove() {
        if (parent == null) {
            reduce();
        } else {
            delete();
        }
    }

    /**
     * 移除下一个节点
     *
     * @return this
     */
    public Line removeNext() {
        if (next != null) {
            next.remove();
        }
        return this;
    }

    /**
     * 移除上一行
     *
     * @return this
     */
    public Line removePrev() {
        if (prev != null) {
            prev.remove();
        }
        return this;
    }

    /**
     * 添加子节点，并将子节点和前后节点的子节点建立连接
     *
     * @param line child
     */
    public void addChild(Line line) {
        if (child != null) {
            child.parent = null;
        }
        child = line;
        if (line.parent != null) {
            line.parent.child = null;
        }
        line.parent = this;
        attachChildToNext();
        attachChildToPrev();
    }

    /**
     * 将子节点和下一个节点之间建立连接
     */
    public void attachChildToNext() {
        if (child != null && next != null) {
            if (child.next != null) {
                child.next.prev = null;
            }
            child.next = next.child;
            if (next.child != null) {
                if (next.child.prev != null) {
                    next.child.prev.next = null;
                }
                next.child.prev = child;
            }
            child.attachChildToNext();
        }
    }

    /**
     * 将子节点和上一个节点之间建立连接
     */
    public void attachChildToPrev() {
        if (child != null && prev != null) {
            if (child.prev != null) {
                child.prev.next = null;
            }
            child.prev = prev.child;
            if (prev.child != null) {
                if (prev.child.next != null) {
                    prev.child.next.prev = null;
                }
                prev.child.next = child;
            }
            child.attachChildToPrev();
        }
    }

    /**
     * 将自身作为子节点添加到某个节点
     *
     * @param line parent
     */
    public void attachToParent(Line line) {
        line.addChild(this);
    }

    /**
     * 从父节点上解绑
     */
    public void unAttachFromParent() {
        if (parent != null) {
            delete();
            parent.child = null;
        }
        parent = null;
    }

    /**
     * 创建一个子节点
     *
     * @param src 子节点的source
     * @return 子节点
     */
    public Line createChild(String src) {
        Line c = new Line(src);
        addChild(c);
        return c;
    }

    /**
     * 将自身及父节点copy为下一个节点
     *
     * @return copy产生的对象
     */
    public Line copyToNext() {
        Line p = null;
        if (parent != null) {
            p = parent.copyToNext();
        }
        Line line = new Line(this);
        if (p == null) {
            line.next = next;
            if (next != null) {
                next.prev = line;
            }
            line.prev = this;
            next = line;
        } else {
            p.addChild(line);
        }
        return line;
    }

    /**
     * 将自身及父节点copy为上一个节点
     *
     * @return copy产生的对象
     */
    public Line copyToPrev() {
        Line p = null;
        if (parent != null) {
            p = parent.copyToPrev();
        }
        Line line = new Line(this);
        if (p == null) {
            line.prev = prev;
            if (prev != null) {
                prev.next = line;
            }
            line.next = this;
            prev = this;
        } else {
            p.addChild(line);
        }
        return line;
    }


    @Override
    public String toString() {
        return source;
    }

}
