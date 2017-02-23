package com.fastaccess.provider.scheme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.modules.code.CodeViewerView;
import com.fastaccess.ui.modules.gists.gist.GistView;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerView;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerView;
import com.fastaccess.ui.modules.user.UserPagerView;

import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;

/**
 * Created by Kosh on 09 Dec 2016, 4:44 PM
 */

public class SchemeParser {


    private static final String HOST_DEFAULT = "github.com";
    private static final String HOST_GISTS = "gist.github.com";
    private static final String PROTOCOL_HTTPS = "https";

    public static void launchUri(@NonNull Context context, @NonNull Intent data) {
        Intent intent = convert(context, data);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            Activity activity = ActivityHelper.getActivity(context);
            if (activity == null) {
                context.startActivity(new Intent(ACTION_VIEW, data.getData()).addCategory(CATEGORY_BROWSABLE));
            } else {
                ActivityHelper.startCustomTab(activity, data.getData());
            }
        }
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data) {
        Intent intent = convert(context, data);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            Activity activity = ActivityHelper.getActivity(context);
            if (activity == null) {
                context.startActivity(new Intent(ACTION_VIEW, data).addCategory(CATEGORY_BROWSABLE));
            } else {
                ActivityHelper.startCustomTab(activity, data);
            }
        }
    }

    @Nullable private static Intent convert(@NonNull Context context, final Intent intent) {
        if (intent == null) return null;
        if (!ACTION_VIEW.equals(intent.getAction())) return null;
        Uri data = intent.getData();
        return convert(context, data);
    }

    @Nullable private static Intent convert(@NonNull Context context, Uri data) {
        if (data == null) return null;
        if (InputHelper.isEmpty(data.getHost()) || InputHelper.isEmpty(data.getScheme())) {
            String host = data.getHost();
            if (InputHelper.isEmpty(host)) host = HOST_DEFAULT;
            String scheme = data.getScheme();
            if (InputHelper.isEmpty(scheme)) scheme = PROTOCOL_HTTPS;
            String prefix = scheme + "://" + host;
            String path = data.getPath();
            if (!InputHelper.isEmpty(path))
                if (path.charAt(0) == '/') data = Uri.parse(prefix + path);
                else data = Uri.parse(prefix + '/' + path);
            else data = Uri.parse(prefix);
        }

        return getIntentForURI(context, data);
    }

    @Nullable private static Intent getIntentForURI(@NonNull Context context, @NonNull Uri data) {
        if (HOST_GISTS.equals(data.getHost())) {
            String gist = getGistId(data);
            if (gist != null) {
                return GistView.createIntent(context, gist);
            }
        } else {
            Intent userIntent = getUser(context, data);
            Intent pullRequestIntent = getPullRequestIntent(context, data);
            Intent issueIntent = getIssueIntent(context, data);
            Intent repoIntent = getRepo(context, data);
            Intent commit = getCommit(context, data);
            Intent commits = getCommits(context, data);
            Intent blob = getBlob(context, data);
            Optional<Intent> intentOptional = returnNonNull(userIntent, pullRequestIntent, commit, commits, issueIntent, repoIntent, blob);
            Optional<Intent> empty = Optional.empty();
            if (intentOptional != null && intentOptional.isPresent() && intentOptional != empty)
                return intentOptional.get();
        }
        return null;
    }

    @Nullable private static Intent getPullRequestIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        if (!"pull".equals(segments.get(2))) return null;
        String owner = segments.get(0);
        String repo = segments.get(1);
        String number = segments.get(3);
        if (InputHelper.isEmpty(number))
            return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return PullRequestPagerView.createIntent(context, repo, owner, issueNumber);
    }

    @Nullable private static Intent getIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        if (!"issues".equals(segments.get(2))) return null;
        String owner = segments.get(0);
        String repo = segments.get(1);
        String number = segments.get(3);
        if (InputHelper.isEmpty(number))
            return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return IssuePagerView.createIntent(context, repo, owner, issueNumber);
    }

    @Nullable private static Intent getRepo(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 2 || segments.size() > 2) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        return RepoPagerView.createIntent(context, repoName, owner);
    }

    @Nullable private static Intent getCommits(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.isEmpty() || segments.size() < 4) return null;
        if (segments.get(3).equals("commits")) {
            String login = segments.get(1);
            String repoId = segments.get(2);
            String sha = segments.get(4);
            return CommitPagerView.createIntent(context, repoId, login, sha);
        }
        return null;
    }

    @Nullable private static Intent getCommit(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4 || !"commit".equals(segments.get(2))) return null;
        String login = segments.get(0);
        String repoId = segments.get(1);
        String sha = segments.get(3);
        return CommitPagerView.createIntent(context, repoId, login, sha);
    }

    @Nullable private static String getGistId(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        return segments != null && !segments.isEmpty() ? segments.get(0) : null;
    }

    @Nullable private static Intent getUser(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty() && segments.size() == 1) {
            return UserPagerView.createIntent(context, segments.get(0));
        }
        return null;
    }

    @Nullable private static Intent getBlob(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String segmentTwo = segments.get(2);
        if (segmentTwo.equals("blob") || segmentTwo.equals("tree")) {
            String fullUrl = uri.toString();
            return CodeViewerView.createIntent(context, fullUrl);
        }
        return null;
    }

    @SafeVarargs private static <T> Optional<T> returnNonNull(T... t) {
        return Stream.of(t).filter(value -> value != null).findFirst();
    }
}
