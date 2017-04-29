package com.zzhoujay.markdown.parser;

/**
 * 获取Tag内容
 */
public interface TagGetter {

    /**
     * 获取Line特定Tag的内容
     *
     * @param tag   tag
     * @param line  Line
     * @param group group
     * @return content
     */
    CharSequence get(int tag, Line line, int group);

    /**
     * 获取line特定Tag的内容
     *
     * @param tag   tag
     * @param line  line
     * @param group group
     * @return content
     */
    CharSequence get(int tag, CharSequence line, int group);
}
