package com.fastaccess.provider.scheme;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.annimon.stream.Optional;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.modules.code.CodeViewerView;
import com.fastaccess.ui.modules.gists.gist.GistView;
import com.fastaccess.ui.modules.repos.RepoPagerView;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerView;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueView;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerView;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerView;
import com.fastaccess.ui.modules.user.UserPagerView;

import java.util.List;

import static com.fastaccess.provider.scheme.LinkParserHelper.*;
import static com.fastaccess.provider.scheme.LinkParserHelper.API_AUTHORITY;
import static com.fastaccess.provider.scheme.LinkParserHelper.HOST_DEFAULT;
import static com.fastaccess.provider.scheme.LinkParserHelper.HOST_GISTS;
import static com.fastaccess.provider.scheme.LinkParserHelper.HOST_GISTS_RAW;
import static com.fastaccess.provider.scheme.LinkParserHelper.PROTOCOL_HTTPS;
import static com.fastaccess.provider.scheme.LinkParserHelper.RAW_AUTHORITY;
import static com.fastaccess.provider.scheme.LinkParserHelper.returnNonNull;

/**
 * Created by Kosh on 09 Dec 2016, 4:44 PM
 */

public class SchemeParser {

    public static void launchUri(@NonNull Context context, @NonNull Uri data) {
        launchUri(context, data, false);
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn) {
        Intent intent = convert(context, data, showRepoBtn);
        if (intent != null) {
            if (context instanceof Service || context instanceof Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            ActivityHelper.forceOpenInBrowser(context, data);
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
            Logger.e(IGNORED_LIST.contains(data.getPath()), data.getPathSegments().get(0));
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
                return GistView.createIntent(context, gist);
            }
        } else if (HOST_GISTS_RAW.equalsIgnoreCase(data.getHost())) {
            return getGistFile(context, data);
        } else {
            String authority = data.getAuthority();
            if (TextUtils.equals(authority, HOST_DEFAULT) || TextUtils.equals(authority, RAW_AUTHORITY) ||
                    TextUtils.equals(authority, API_AUTHORITY)) {
                Intent userIntent = getUser(context, data);
                Intent pullRequestIntent = getPullRequestIntent(context, data, showRepoBtn);
                Intent createIssueIntent = getCreateIssueIntent(context, data);
                Intent issueIntent = getIssueIntent(context, data, showRepoBtn);
                Intent repoIntent = getRepo(context, data);
                Intent commit = getCommit(context, data, showRepoBtn);
                Intent commits = getCommits(context, data, showRepoBtn);
                Intent blob = getBlob(context, data);
                Optional<Intent> intentOptional = returnNonNull(userIntent, pullRequestIntent, commit, commits,
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
        return PullRequestPagerView.createIntent(context, repo, owner, issueNumber, showRepoBtn);
    }

    @Nullable private static Intent getIssueIntent(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
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
        return IssuePagerView.createIntent(context, repo, owner, issueNumber, showRepoBtn);
    }

    @Nullable private static Intent getRepo(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 2 || segments.size() > 2) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        return RepoPagerView.createIntent(context, repoName, owner);
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
                String owner = segments.get(0);
                String repoName = segments.get(1);
                return RepoPagerView.createIntent(context, repoName, owner);
            }
        }
        return null;
    }

    @Nullable private static Intent getCommits(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.isEmpty() || segments.size() < 3) return null;
        if (segments.get(3).equals("commits")) {
            String login = segments.get(1);
            String repoId = segments.get(2);
            String sha = segments.get(4);
            return CommitPagerView.createIntent(context, repoId, login, sha, showRepoBtn);
        }
        return null;
    }

    @Nullable private static Intent getCommit(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3 || !"commit".equals(segments.get(2))) return null;
        String login = segments.get(0);
        String repoId = segments.get(1);
        String sha = segments.get(3);
        return CommitPagerView.createIntent(context, repoId, login, sha, showRepoBtn);
    }

    @Nullable private static String getGistId(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        return segments != null && !segments.isEmpty() ? segments.get(0) : null;
    }

    @Nullable private static Intent getUser(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty() && segments.size() == 1) {
            return UserPagerView.createIntent(context, segments.get(0));
        } else if (segments != null && !segments.isEmpty() && segments.size() > 1 && segments.get(0).equalsIgnoreCase("orgs")) {
            return UserPagerView.createIntent(context, segments.get(1), true);
        }
        return null;
    }

    @Nullable private static Intent getBlob(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String segmentTwo = segments.get(2);
        if (segmentTwo.equals("blob") || segmentTwo.equals("tree")) {
            String fullUrl = uri.toString();
            if (InputHelper.isEmpty(MimeTypeMap.getFileExtensionFromUrl(fullUrl))) {
                return null;
            }
            if (uri.getAuthority().equalsIgnoreCase(HOST_DEFAULT)) {
                String owner = segments.get(0);
                String repo = segments.get(1);
                String branch = segments.get(3);
                fullUrl = "https://" + RAW_AUTHORITY + "/" + owner + "/" + repo + "/" + branch;
                for (int i = 4; i < segments.size(); i++) {
                    fullUrl += "/" + segments.get(i);
                }
            }
            if (fullUrl != null) return CodeViewerView.createIntent(context, fullUrl);
        } else {
            String authority = uri.getAuthority();
            if (TextUtils.equals(authority, RAW_AUTHORITY)) {
                return CodeViewerView.createIntent(context, uri.toString());
            }
        }
        return null;
    }

    /**
     * https://github.com/owner/repo/issues/new
     */
    @Nullable private static Intent getCreateIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        Logger.e(segments);
        if (uri.getLastPathSegment() == null) return null;
        if (segments == null || segments.size() < 3 || !uri.getLastPathSegment().equalsIgnoreCase("new")) return null;
        if ("issues".equals(segments.get(2))) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            return CreateIssueView.getIntent(context, owner, repo);
        }
        return null;
    }

    @Nullable private static Intent getGistFile(@NonNull Context context, @NonNull Uri uri) {
        if (uri.getHost().equalsIgnoreCase(HOST_GISTS_RAW)) {
            return CodeViewerView.createIntent(context, uri.toString());
        }
        return null;
    }
}
