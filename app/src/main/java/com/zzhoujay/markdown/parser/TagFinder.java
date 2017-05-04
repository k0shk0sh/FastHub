package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-10.
 * TagFinder
 */
public interface TagFinder {

    /**
     * 检查对应tag是否存在
     *
     * @param tag  Tag Id
     * @param line line
     * @return true：存在，false不存在
     */
    boolean find(int tag, Line line);

    /**
     * 检查对应tag是否存在
     *
     * @param tag  Tag Id
     * @param line line
     * @return true：存在，false不存在
     */
    boolean find(int tag, String line);

    /**
     * 检查对应tag的个数
     *
     * @param tag   tag id
     * @param line  line
     * @param group group
     * @return 对应tag的次数
     */
    int findCount(int tag, Line line, int group);

    /**
     * 检查对应tag的个数
     *
     * @param tag   tag id
     * @param line  line
     * @param group group
     * @return 对应tag的次数
     */
    int findCount(int tag, String line, int group);

}
