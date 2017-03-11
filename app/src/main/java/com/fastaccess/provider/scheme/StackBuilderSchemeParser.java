package com.fastaccess.provider.scheme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.modules.code.CodeViewerView;
import com.fastaccess.ui.modules.gists.gist.GistView;
import com.fastaccess.ui.modules.main.MainView;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerView;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerView;
import com.fastaccess.ui.modules.user.UserPagerView;

import java.util.List;

import static android.content.Intent.ACTION_VIEW;

/**
 * Created by Kosh on 09 Dec 2016, 4:44 PM
 */

public class StackBuilderSchemeParser {
    private static final String HOST_DEFAULT = "github.com";
    private static final String HOST_GISTS = "gist.github.com";
    private static final String RAW_AUTHORITY = "raw.githubusercontent.com";
    private static final String API_AUTHORITY = "api.github.com";
    private static final String PROTOCOL_HTTPS = "https";

    public static void launchUri(@NonNull Context context, @NonNull Intent data) {
        if (data.getData() != null) {
            launchUri(context, data.getData());
        }
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data) {
        TaskStackBuilder intent = convert(context, data);
        if (intent != null) {
            intent.startActivities();
        } else {
            Activity activity = ActivityHelper.getActivity(context);
            if (activity == null) {
                ActivityHelper.forceOpenInBrowser(context, data);
            } else {
                ActivityHelper.startCustomTab(activity, data);
            }
        }
    }

    @Nullable private static TaskStackBuilder convert(@NonNull Context context, final Intent intent) {
        if (intent == null) return null;
        if (!ACTION_VIEW.equals(intent.getAction())) return null;
        Uri data = intent.getData();
        return convert(context, data);
    }

    @Nullable private static TaskStackBuilder convert(@NonNull Context context, Uri data) {
        if (data == null) return null;
        if (InputHelper.isEmpty(data.getHost()) || InputHelper.isEmpty(data.getScheme())) {
            String host = data.getHost();
            if (InputHelper.isEmpty(host)) host = HOST_DEFAULT;
            String scheme = data.getScheme();
            if (InputHelper.isEmpty(scheme)) scheme = PROTOCOL_HTTPS;
            String prefix = scheme + "://" + host;
            String path = data.getPath();
            if (!InputHelper.isEmpty(path)) {
                if (path.charAt(0) == '/') {
                    data = Uri.parse(prefix + path);
                } else {
                    data = Uri.parse(prefix + '/' + path);
                }
            } else {
                data = Uri.parse(prefix);
            }
        }

        return getIntentForURI(context, data);
    }

    @Nullable private static TaskStackBuilder getIntentForURI(@NonNull Context context, @NonNull Uri data) {
        if (HOST_GISTS.equals(data.getHost())) {
            String gist = getGistId(data);
            if (gist != null) {
                return TaskStackBuilder.create(context)
                        .addParentStack(MainView.class)
                        .addNextIntent(GistView.createIntent(context, gist));
            }
        } else {
            String authority = data.getAuthority();
            if (TextUtils.equals(authority, HOST_DEFAULT) || TextUtils.equals(authority, RAW_AUTHORITY) ||
                    TextUtils.equals(authority, API_AUTHORITY)) {
                TaskStackBuilder userIntent = getUser(context, data);
                TaskStackBuilder pullRequestIntent = getPullRequestIntent(context, data);
                TaskStackBuilder issueIntent = getIssueIntent(context, data);
                TaskStackBuilder repoIntent = getRepo(context, data);
                TaskStackBuilder commit = getCommit(context, data);
                TaskStackBuilder commits = getCommits(context, data);
                TaskStackBuilder blob = getBlob(context, data);
                Optional<TaskStackBuilder> intentOptional = returnNonNull(userIntent, pullRequestIntent, commit, commits,
                        issueIntent, repoIntent, blob);
                Optional<TaskStackBuilder> empty = Optional.empty();
                if (intentOptional != null && intentOptional.isPresent() && intentOptional != empty) {
                    return intentOptional.get();
                } else {
                    return getGeneralRepo(context, data);
                }
            }
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getPullRequestIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String owner;
        String repo;
        String number;
        if ("pull".equals(segments.get(2)) || "pulls".equals(segments.get(2))) {
            owner = segments.get(0);
            repo = segments.get(1);
            number = segments.get(3);
        } else if ("pull".equals(segments.get(3)) || "pulls".equals(segments.get(3))) {//notifications url.
            owner = segments.get(1);
            repo = segments.get(2);
            number = segments.get(4);
        } else {
            return null;
        }
        if (InputHelper.isEmpty(number))
            return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(RepoPagerView.createIntent(context, repo, owner, RepoPagerMvp.PULL_REQUEST))
                .addNextIntent(PullRequestPagerView.createIntent(context, repo, owner, issueNumber));
    }

    @Nullable private static TaskStackBuilder getIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String owner;
        String repo;
        String number;
        if ("issues".equals(segments.get(2))) {
            owner = segments.get(0);
            repo = segments.get(1);
            number = segments.get(3);
        } else if ("issues".equals(segments.get(3))) {//notifications url.
            owner = segments.get(1);
            repo = segments.get(2);
            number = segments.get(4);
        } else {
            return null;
        }
        if (InputHelper.isEmpty(number))
            return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(RepoPagerView.createIntent(context, repo, owner, RepoPagerMvp.ISSUES))
                .addNextIntent(IssuePagerView.createIntent(context, repo, owner, issueNumber));
    }

    @Nullable private static TaskStackBuilder getRepo(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 2 || segments.size() > 2) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        return TaskStackBuilder.create(context)
                .addParentStack(MainView.class)
                .addNextIntent(RepoPagerView.createIntent(context, repoName, owner));
    }

    /**
     * [[k0shk0sh, FastHub, issues], k0shk0sh/fastHub/(issues,pulls,commits, etc)]
     */
    @Nullable private static TaskStackBuilder getGeneralRepo(@NonNull Context context, @NonNull Uri uri) {
        //TODO parse deeper links to their associate views. meantime fallback to repoPage
        if (uri.getAuthority().equals(HOST_DEFAULT) || uri.getAuthority().equals(API_AUTHORITY)) {
            List<String> segments = uri.getPathSegments();
            if (segments == null || segments.isEmpty()) return null;
            if (segments.size() == 1) {
                return getUser(context, uri);
            } else if (segments.size() > 1) {
                String owner = segments.get(0);
                String repoName = segments.get(1);
                return TaskStackBuilder.create(context)
                        .addParentStack(MainView.class)
                        .addNextIntent(RepoPagerView.createIntent(context, repoName, owner));
            }
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getCommits(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.isEmpty() || segments.size() < 4) return null;
        if (segments.get(3).equals("commits")) {
            String login = segments.get(1);
            String repoId = segments.get(2);
            String sha = segments.get(4);
            return TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(RepoPagerView.createIntent(context, repoId, login))
                    .addNextIntent(CommitPagerView.createIntent(context, repoId, login, sha));
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getCommit(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4 || !"commit".equals(segments.get(2))) return null;
        String login = segments.get(0);
        String repoId = segments.get(1);
        String sha = segments.get(3);
        return TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(RepoPagerView.createIntent(context, repoId, login))
                .addNextIntent(CommitPagerView.createIntent(context, repoId, login, sha));
    }

    @Nullable private static TaskStackBuilder getUser(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty() && segments.size() == 1) {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainView.class)
                    .addNextIntent(UserPagerView.createIntent(context, segments.get(0)));
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getBlob(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String segmentTwo = segments.get(2);
        if (segmentTwo.equals("blob") || segmentTwo.equals("tree")) {
            String fullUrl = uri.toString();
            if (uri.getAuthority().equalsIgnoreCase(HOST_DEFAULT)) {
                fullUrl = "https://" + RAW_AUTHORITY + "/" + segments.get(0) + "/" + segments.get(1) + "/" +
                        segments.get(segments.size() - 2) + "/" + uri.getLastPathSegment();
            }
            if (fullUrl != null) {
                return TaskStackBuilder.create(context)
                        .addParentStack(MainView.class)
                        .addNextIntent(CodeViewerView.createIntent(context, fullUrl));
            }
        } else {
            String authority = uri.getAuthority();
            if (TextUtils.equals(authority, RAW_AUTHORITY)) {
                return TaskStackBuilder.create(context)
                        .addParentStack(MainView.class)
                        .addNextIntent(CodeViewerView.createIntent(context, uri.toString()));
            }
        }
        return null;
    }

    @SafeVarargs private static <T> Optional<T> returnNonNull(T... t) {
        return Stream.of(t).filter(value -> value != null).findFirst();
    }

    @Nullable private static String getGistId(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        return segments != null && !segments.isEmpty() ? segments.get(0) : null;
    }
}
