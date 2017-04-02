package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-10.
 * TagHandler
 */
public interface TagHandler extends TagFinder, QueueConsumer, TagGetter {

    boolean h(Line line);

    boolean h1(Line line);

    boolean h2(Line line);

    boolean h3(Line line);

    boolean h4(Line line);

    boolean h5(Line line);

    boolean h6(Line line);

    boolean quota(Line line);

    boolean ul(Line line);

    boolean ol(Line line);

    boolean gap(Line line);

    boolean em(Line line);

    boolean italic(Line line);

    boolean emItalic(Line line);

    boolean code(Line line);

    boolean email(Line line);

    boolean delete(Line line);

    boolean autoLink(Line line);

    boolean link(Line line);

    boolean link2(Line line);

    boolean linkId(String line);

    boolean image(Line line);

    boolean image2(Line line);

    boolean imageId(String line);

    boolean inline(Line line);

    boolean codeBlock1(Line line);

    boolean codeBlock2(Line line);

}
