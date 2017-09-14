package android.net.http;

/**
 * Robolectric requires this class be available in the classpath, otherwise {@link
 * org.robolectric.Shadows#shadowOf(android.os.Looper)} fails. We don't use AndroidHttpClient, so
 * include a stub to make Robolectric happy.
 *
 * @see https://github.com/robolectric/robolectric/issues/1862
 */
public class AndroidHttpClient {}
