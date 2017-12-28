package ru.noties.markwon;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.widget.TextView;

import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import ru.noties.markwon.extension.emoji.EmojiExtension;
import ru.noties.markwon.extension.gh.GitHubExtension;
import ru.noties.markwon.il.AsyncDrawableLoader;
import ru.noties.markwon.renderer.SpannableRenderer;
import ru.noties.markwon.spans.AsyncDrawable;
import ru.noties.markwon.tasklist.TaskListExtension;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Markwon {

    /**
     * Helper method to obtain a Parser with registered strike-through &amp; table extensions
     * &amp; task lists (added in 1.0.1)
     *
     * @return a Parser instance that is supported by this library
     * @since 1.0.0
     */
    @NonNull
    public static Parser createParser() {
        return new Parser.Builder()
                .extensions(Arrays.asList(
                        GitHubExtension.create(false),
                        GitHubExtension.create(true),
                        EmojiExtension.create(),
                        TaskListExtension.create(),
                        StrikethroughExtension.create(),
                        AutolinkExtension.create(),
                        TablesExtension.create(),
                        InsExtension.create(),
                        YamlFrontMatterExtension.create()
                ))
                .build();
    }

    /**
     * @see #setMarkdown(TextView, SpannableConfiguration, String)
     * @since 1.0.0
     */
    public static void setMarkdown(@NonNull TextView view, @NonNull String markdown) {
        final SpannableConfiguration configuration = SpannableConfiguration.builder(view.getContext())
                .asyncDrawableLoader(asyncDrawableLoader(new OkHttpClient.Builder()
                        .cache(new Cache(view.getContext().getCacheDir(), 1024L * 20))
                        .followRedirects(true)
                        .retryOnConnectionFailure(true)
                        .build(), Executors.newCachedThreadPool(), view.getResources()))
                .build();
        setMarkdown(view, configuration, markdown);
    }

    /**
     * Parses submitted raw markdown, converts it to CharSequence (with Spannables)
     * and applies it to view
     *
     * @param view
     *         {@link TextView} to set markdown into
     * @param configuration
     *         a {@link SpannableConfiguration} instance
     * @param markdown
     *         raw markdown String (for example: {@code `**Hello**`})
     * @see #markdown(SpannableConfiguration, String)
     * @see #setText(TextView, CharSequence)
     * @see SpannableConfiguration
     * @since 1.0.0
     */
    public static void setMarkdown(@NonNull TextView view, @NonNull SpannableConfiguration configuration, @NonNull String markdown) {
        setText(view, markdown(configuration, markdown));
    }

    /**
     * Helper method to apply parsed markdown. Please note, that if images or tables are used
     *
     * @param view
     *         {@link TextView} to set markdown into
     * @param text
     *         parsed markdown
     * @see #scheduleDrawables(TextView)
     * @see #scheduleTableRows(TextView)
     * @since 1.0.0
     */
    public static void setText(@NonNull TextView view, CharSequence text) {

        unscheduleDrawables(view);
        unscheduleTableRows(view);

        // update movement method (for links to be clickable)
//        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(text);

        // schedule drawables (dynamic drawables that can change bounds/animate will be correctly updated)
        scheduleDrawables(view);
        scheduleTableRows(view);
    }

    /**
     * Returns parsed markdown with default {@link SpannableConfiguration} obtained from {@link Context}
     *
     * @param context
     *         {@link Context}
     * @param markdown
     *         raw markdown
     * @return parsed markdown
     * @since 1.0.0
     */
    @NonNull
    public static CharSequence markdown(@NonNull Context context, @NonNull String markdown) {
        final SpannableConfiguration configuration = SpannableConfiguration.create(context);
        return markdown(configuration, markdown);
    }

    /**
     * Returns parsed markdown with provided {@link SpannableConfiguration}
     *
     * @param configuration
     *         a {@link SpannableConfiguration}
     * @param markdown
     *         raw markdown
     * @return parsed markdown
     * @see SpannableConfiguration
     * @since 1.0.0
     */
    @NonNull
    public static CharSequence markdown(@NonNull SpannableConfiguration configuration, @NonNull String markdown) {
        final Parser parser = createParser();
        final Node node = parser.parse(markdown);
        final SpannableRenderer renderer = new SpannableRenderer();
        return renderer.render(configuration, node);
    }

    /**
     * This method adds support for {@link ru.noties.markwon.spans.AsyncDrawable} to be used. As
     * textView seems not to support drawables that change bounds (and gives no means
     * to update the layout), we create own {@link android.graphics.drawable.Drawable.Callback}
     * and apply it. So, textView can display drawables, that are: async (loading from disk, network);
     * dynamic (requires `invalidate`) - GIF, animations.
     * Please note, that this method should be preceded with {@link #unscheduleDrawables(TextView)}
     * in order to avoid keeping drawables in memory after they have been removed from layout
     *
     * @param view
     *         a {@link TextView}
     * @see ru.noties.markwon.spans.AsyncDrawable
     * @see ru.noties.markwon.spans.AsyncDrawableSpan
     * @see DrawablesScheduler#schedule(TextView)
     * @see DrawablesScheduler#unschedule(TextView)
     * @since 1.0.0
     */
    public static void scheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.schedule(view);
    }

    /**
     * De-references previously scheduled {@link ru.noties.markwon.spans.AsyncDrawableSpan}&#39;s
     *
     * @param view
     *         a {@link TextView}
     * @see #scheduleDrawables(TextView)
     * @since 1.0.0
     */
    public static void unscheduleDrawables(@NonNull TextView view) {
        DrawablesScheduler.unschedule(view);
    }

    /**
     * This method is required in order to use tables. A bit of background:
     * this library uses a {@link android.text.style.ReplacementSpan} to
     * render tables, but the flow is not really flexible. We are required
     * to return `size` (width) of our replacement, but we are not provided
     * with the total one (canvas width). In order to correctly calculate height of our
     * table cell text, we must have available width first. This method gives
     * ability for {@link ru.noties.markwon.spans.TableRowSpan} to invalidate
     * `view` when it encounters such a situation (when available width is not known or have changed).
     * Precede this call with {@link #unscheduleTableRows(TextView)} in order to
     * de-reference previously scheduled {@link ru.noties.markwon.spans.TableRowSpan}&#39;s
     *
     * @param view
     *         a {@link TextView}
     * @see #unscheduleTableRows(TextView)
     * @since 1.0.0
     */
    public static void scheduleTableRows(@NonNull TextView view) {
        TableRowsScheduler.schedule(view);
    }

    /**
     * De-references previously scheduled {@link ru.noties.markwon.spans.TableRowSpan}&#39;s
     *
     * @param view
     *         a {@link TextView}
     * @see #scheduleTableRows(TextView)
     * @since 1.0.0
     */
    public static void unscheduleTableRows(@NonNull TextView view) {
        TableRowsScheduler.unschedule(view);
    }

    private static AsyncDrawable.Loader asyncDrawableLoader(
            OkHttpClient client,
            ExecutorService executorService,
            Resources resources) {
        return AsyncDrawableLoader.builder()
                .client(client)
                .executorService(executorService)
                .resources(resources)
                .build();
    }

    private Markwon() {
    }
}
