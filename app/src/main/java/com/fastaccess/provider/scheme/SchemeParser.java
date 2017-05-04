package com.fastaccess.provider.scheme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.annimon.stream.Optional;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.gists.gist.GistActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerActivity;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerActivity;
import com.fastaccess.ui.modules.user.UserPagerActivity;

import java.util.List;

import static com.fastaccess.provider.scheme.LinkParserHelper.API_AUTHORITY;
import static com.fastaccess.provider.scheme.LinkParserHelper.HOST_DEFAULT;
import static com.fastaccess.provider.scheme.LinkParserHelper.HOST_GISTS;
import static com.fastaccess.provider.scheme.LinkParserHelper.HOST_GISTS_RAW;
import static com.fastaccess.provider.scheme.LinkParserHelper.IGNORED_LIST;
import static com.fastaccess.provider.scheme.LinkParserHelper.PROTOCOL_HTTPS;
import static com.fastaccess.provider.scheme.LinkParserHelper.RAW_AUTHORITY;
import static com.fastaccess.provider.scheme.LinkParserHelper.getBlobBuilder;
import static com.fastaccess.provider.scheme.LinkParserHelper.returnNonNull;

/**
 * Created by Kosh on 09 Dec 2016, 4:44 PM
 */

public class SchemeParser {

    public static void launchUri(@NonNull Context context, @NonNull Uri data) {
        launchUri(context, data, false);
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn) {
        launchUri(context, data, showRepoBtn, false);
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn, boolean isService) {
        Logger.e(data);
        Intent intent = convert(context, data, showRepoBtn);
        if (intent != null) {
            if (isService) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Activity activity = ActivityHelper.getActivity(context);
            if (activity != null) {
                ActivityHelper.startCustomTab(activity, data);
            } else {
                ActivityHelper.openChooser(context, data);
            }
        }
    }

    @Nullable private static Intent convert(@NonNull Context context, Uri data, boolean showRepoBtn) {
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
        if (!data.getPathSegments().isEmpty()) {
            if (IGNORED_LIST.contains(data.getPathSegments().get(0))) return null;
        } else {
            return null;
        }
        return getIntentForURI(context, data, showRepoBtn);
    }

    @Nullable private static Intent getIntentForURI(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn) {
        if (HOST_GISTS.equals(data.getHost())) {
            String gist = getGistId(data);
            if (gist != null) {
                return GistActivity.createIntent(context, gist);
            }
        } else if (HOST_GISTS_RAW.equalsIgnoreCase(data.getHost())) {
            return getGistFile(context, data);
        } else {
            String authority = data.getAuthority();
            if (TextUtils.equals(authority, HOST_DEFAULT) || TextUtils.equals(authority, RAW_AUTHORITY) ||
                    TextUtils.equals(authority, API_AUTHORITY)) {
                Intent userIntent = getUser(context, data);
                Intent repoIssues = getRepoIssueIntent(context, data);
                Intent repoPulls = getRepoPullRequestIntent(context, data);
                Intent createIssueIntent = getCreateIssueIntent(context, data);
                Intent pullRequestIntent = getPullRequestIntent(context, data, showRepoBtn);
                Intent issueIntent = getIssueIntent(context, data, showRepoBtn);
                Intent repoIntent = getRepo(context, data);
                Intent commit = getCommit(context, data, showRepoBtn);
                Intent commits = getCommits(context, data, showRepoBtn);
                Intent blob = getBlob(context, data);
                Optional<Intent> intentOptional = returnNonNull(userIntent, repoIssues, repoPulls, pullRequestIntent, commit, commits,
                        createIssueIntent, issueIntent, repoIntent, blob);
                Optional<Intent> empty = Optional.empty();
                if (intentOptional != null && intentOptional.isPresent() && intentOptional != empty) {
                    return intentOptional.get();
                } else {
                    return getGeneralRepo(context, data);
                }
            }
        }
        return null;
    }

    @Nullable private static Intent getPullRequestIntent(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = null;
        String repo = null;
        String number = null;
        if (segments.size() > 3) {
            if (("pull".equals(segments.get(2)) || "pulls".equals(segments.get(2)))) {
                owner = segments.get(0);
                repo = segments.get(1);
                number = segments.get(3);
            } else if (("pull".equals(segments.get(3)) || "pulls".equals(segments.get(3))) && segments.size() > 4) {
                owner = segments.get(1);
                repo = segments.get(2);
                number = segments.get(4);
            } else {
                return null;
            }
        }
        if (InputHelper.isEmpty(number)) return null;
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return PullRequestPagerActivity.createIntent(context, repo, owner, issueNumber, showRepoBtn);
    }

    @Nullable private static Intent getIssueIntent(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = null;
        String repo = null;
        String number = null;
        if (segments.size() > 3) {
            if (segments.get(2).equalsIgnoreCase("issues")) {
                owner = segments.get(0);
                repo = segments.get(1);
                number = segments.get(3);
            } else if (segments.get(3).equalsIgnoreCase("issues") && segments.size() > 4) {
                owner = segments.get(1);
                repo = segments.get(2);
                number = segments.get(4);
            } else {
                return null;
            }
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
        return IssuePagerActivity.createIntent(context, repo, owner, issueNumber, showRepoBtn);
    }

    @Nullable private static Intent getRepo(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 2 || segments.size() > 2) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        return RepoPagerActivity.createIntent(context, repoName, owner);
    }

    /**
     * [[k0shk0sh, FastHub, issues], k0shk0sh/fastHub/(issues,pulls,commits, etc)]
     */
    @Nullable private static Intent getGeneralRepo(@NonNull Context context, @NonNull Uri uri) {
        //TODO parse deeper links to their associate views. meantime fallback to repoPage
        if (uri.getAuthority().equals(HOST_DEFAULT) || uri.getAuthority().equals(API_AUTHORITY)) {
            List<String> segments = uri.getPathSegments();
            if (segments == null || segments.isEmpty()) return null;
            if (segments.size() == 1) {
                return getUser(context, uri);
            } else if (segments.size() > 1) {
                if (segments.get(0).equalsIgnoreCase("repos") && segments.size() >= 2) {
                    String owner = segments.get(1);
                    String repoName = segments.get(2);
                    return RepoPagerActivity.createIntent(context, repoName, owner);
                } else {
                    String owner = segments.get(0);
                    String repoName = segments.get(1);
                    return RepoPagerActivity.createIntent(context, repoName, owner);
                }
            }
        }
        return null;
    }

    @Nullable private static Intent getCommits(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.isEmpty() || segments.size() < 3) return null;
        String login = null;
        String repoId = null;
        String sha = null;
        if (segments.size() > 3 && segments.get(3).equals("commits")) {
            login = segments.get(1);
            repoId = segments.get(2);
            sha = segments.get(4);
        } else if (segments.size() > 2 && segments.get(2).equals("commits")) {
            login = segments.get(0);
            repoId = segments.get(1);
            sha = uri.getLastPathSegment();
        }
        if (login != null && sha != null && repoId != null) {
            return CommitPagerActivity.createIntent(context, repoId, login, sha, showRepoBtn);
        }
        return null;
    }

    @Nullable private static Intent getCommit(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3 || !"commit".equals(segments.get(2))) return null;
        String login = segments.get(0);
        String repoId = segments.get(1);
        String sha = segments.get(3);
        return CommitPagerActivity.createIntent(context, repoId, login, sha, showRepoBtn);
    }

    @Nullable private static String getGistId(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        return segments != null && !segments.isEmpty() ? segments.get(0) : null;
    }

    @Nullable private static Intent getUser(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty() && segments.size() == 1) {
            return UserPagerActivity.createIntent(context, segments.get(0));
        } else if (segments != null && !segments.isEmpty() && segments.size() > 1 && segments.get(0).equalsIgnoreCase("orgs")) {
            return UserPagerActivity.createIntent(context, segments.get(1), true);
        }
        return null;
    }

    @Nullable private static Intent getBlob(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String segmentTwo = segments.get(2);
        if (segmentTwo.equals("blob") || segmentTwo.equals("tree")) {
            Uri urlBuilder = getBlobBuilder(uri);
            return CodeViewerActivity.createIntent(context, urlBuilder.toString(), uri.toString());
        } else {
            String authority = uri.getAuthority();
            if (TextUtils.equals(authority, RAW_AUTHORITY)) {
                return CodeViewerActivity.createIntent(context, uri.toString(), uri.toString());
            }
        }
        return null;
    }

    @Nullable private static Intent getRepoIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && segments.size() == 3 && uri.getLastPathSegment().equalsIgnoreCase("issues")) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            return RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.ISSUES);
        }
        return null;
    }

    @Nullable private static Intent getRepoPullRequestIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && segments.size() == 3 && uri.getLastPathSegment().equalsIgnoreCase("pulls")) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            return RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.PULL_REQUEST);
        }
        return null;
    }

    /**
     * https://github.com/owner/repo/issues/new
     */
    @Nullable private static Intent getCreateIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (uri.getLastPathSegment() == null) return null;
        if (segments == null || segments.size() < 3 || !uri.getLastPathSegment().equalsIgnoreCase("new")) return null;
        if ("issues".equals(segments.get(2))) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            return CreateIssueActivity.getIntent(context, owner, repo);
        }
        return null;
    }

    @Nullable private static Intent getGistFile(@NonNull Context context, @NonNull Uri uri) {
        if (uri.getHost().equalsIgnoreCase(HOST_GISTS_RAW)) {
            return CodeViewerActivity.createIntent(context, uri.toString(), uri.toString());
        }
        return null;
    }
}
