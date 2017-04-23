package com.fastaccess.provider.scheme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.annimon.stream.Optional;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.gists.gist.GistActivity;
import com.fastaccess.ui.modules.main.MainActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;
import com.fastaccess.ui.modules.repos.code.files.activity.RepoFilesActivity;
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
import static com.fastaccess.provider.scheme.LinkParserHelper.returnNonNull;

/**
 * Created by Kosh on 09 Dec 2016, 4:44 PM
 */

public class StackBuilderSchemeParser {

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
            ActivityHelper.forceOpenInBrowser(context, data);
        }
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
        if (!data.getPathSegments().isEmpty()) {
            if (IGNORED_LIST.contains(data.getPathSegments().get(0))) return null;
        } else {
            return null;
        }
        return getIntentForURI(context, data);
    }

    @Nullable private static TaskStackBuilder getIntentForURI(@NonNull Context context, @NonNull Uri data) {
        if (HOST_GISTS.equals(data.getHost())) {
            String gist = getGistId(data);
            if (gist != null) {
                return TaskStackBuilder.create(context)
                        .addParentStack(MainActivity.class)
                        .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                        .addNextIntent(GistActivity.createIntent(context, gist));
            }
        } else if (HOST_GISTS_RAW.equalsIgnoreCase(data.getHost())) {
            return getGistFile(context, data);
        } else {
            String authority = data.getAuthority();
            if (TextUtils.equals(authority, HOST_DEFAULT) || TextUtils.equals(authority, RAW_AUTHORITY) ||
                    TextUtils.equals(authority, API_AUTHORITY)) {
                TaskStackBuilder userIntent = getUser(context, data);
                TaskStackBuilder pullRequestIntent = getPullRequestIntent(context, data);
                TaskStackBuilder createIssueIntent = getCreateIssueIntent(context, data);
                TaskStackBuilder issueIntent = getIssueIntent(context, data);
                TaskStackBuilder repoIntent = getRepo(context, data);
                TaskStackBuilder commit = getCommit(context, data);
                TaskStackBuilder commits = getCommits(context, data);
                TaskStackBuilder blob = getBlob(context, data);
                Optional<TaskStackBuilder> intentOptional = returnNonNull(userIntent, pullRequestIntent, commit, commits,
                        createIssueIntent, issueIntent, repoIntent, blob);
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
        if (segments == null) return null;
        String owner;
        String repo;
        String number;
        if (segments.size() > 2 && "pull".equals(segments.get(2)) || "pulls".equals(segments.get(2))) {
            owner = segments.get(0);
            repo = segments.get(1);
            number = segments.get(3);
        } else if (segments.size() > 3 && "pull".equals(segments.get(3)) || "pulls".equals(segments.get(3))) {//notifications url.
            owner = segments.get(1);
            repo = segments.get(2);
            number = segments.get(4);
        } else {
            return null;
        }
        if (InputHelper.isEmpty(number)) {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntent(RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.PULL_REQUEST));
        }
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.PULL_REQUEST))
                .addNextIntent(PullRequestPagerActivity.createIntent(context, repo, owner, issueNumber));
    }

    @Nullable private static TaskStackBuilder getIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null) return null;
        String owner;
        String repo;
        String number;
        if (segments.size() > 2 && "issues".equals(segments.get(2))) {
            owner = segments.get(0);
            repo = segments.get(1);
            number = segments.get(3);
        } else if (segments.size() > 3 && "issues".equals(segments.get(3))) {//notifications url.
            owner = segments.get(1);
            repo = segments.get(2);
            number = segments.get(4);
        } else {
            return null;
        }
        if (InputHelper.isEmpty(number)) {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntent(RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.ISSUES));
        }
        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1) return null;
        return TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.ISSUES))
                .addNextIntent(IssuePagerActivity.createIntent(context, repo, owner, issueNumber));
    }

    @Nullable private static TaskStackBuilder getRepo(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 2 || segments.size() > 2) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        return TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                .addNextIntent(RepoPagerActivity.createIntent(context, repoName, owner));
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
                if (segments.get(0).equalsIgnoreCase("repos") && segments.size() >= 2) {
                    String owner = segments.get(1);
                    String repoName = segments.get(2);
                    return TaskStackBuilder.create(context)
                            .addParentStack(MainActivity.class)
                            .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                            .addNextIntent(RepoPagerActivity.createIntent(context, repoName, owner));
                } else {
                    String owner = segments.get(0);
                    String repoName = segments.get(1);
                    return TaskStackBuilder.create(context)
                            .addParentStack(MainActivity.class)
                            .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                            .addNextIntent(RepoPagerActivity.createIntent(context, repoName, owner));
                }
            }
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getCommits(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.isEmpty()) return null;
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
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repoId, login))
                    .addNextIntent(CommitPagerActivity.createIntent(context, repoId, login, sha));
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getCommit(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3 || !"commit".equals(segments.get(2))) return null;
        String login = segments.get(0);
        String repoId = segments.get(1);
        String sha = segments.get(3);
        return TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repoId, login))
                .addNextIntent(CommitPagerActivity.createIntent(context, repoId, login, sha));
    }

    @Nullable private static TaskStackBuilder getUser(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty() && segments.size() == 1) {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntent(UserPagerActivity.createIntent(context, segments.get(0)));
        } else if (segments != null && !segments.isEmpty() && segments.size() > 1 && segments.get(0).equalsIgnoreCase("orgs")) {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntent(UserPagerActivity.createIntent(context, segments.get(1), true));
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getBlob(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String segmentTwo = segments.get(2);
        if (segmentTwo.equals("blob") || segmentTwo.equals("tree")) {
            StringBuilder fullUrl = new StringBuilder(uri.toString());
            if (InputHelper.isEmpty(MimeTypeMap.getFileExtensionFromUrl(fullUrl.toString()))) {
                return null;
            }
            String owner = null;
            String repo = null;
            if (uri.getAuthority().equalsIgnoreCase(HOST_DEFAULT)) {
                owner = segments.get(0);
                repo = segments.get(1);
                String branch = segments.get(3);
                fullUrl = new StringBuilder("https://" + RAW_AUTHORITY + "/" + owner + "/" + repo + "/" + branch);
                for (int i = 4; i < segments.size(); i++) {
                    fullUrl.append("/").append(segments.get(i));
                }
            }
            if (fullUrl.length() > 0 && owner != null && repo != null) return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repo, owner))
                    .addNextIntentWithParentStack(RepoFilesActivity.getIntent(context, fullUrl.toString()))
                    .addNextIntent(CodeViewerActivity.createIntent(context, fullUrl.toString()));
        } else {
            String authority = uri.getAuthority();
            if (TextUtils.equals(authority, RAW_AUTHORITY)) {
                String owner = uri.getPathSegments().get(0);
                String repo = uri.getPathSegments().get(1);
                return TaskStackBuilder.create(context)
                        .addParentStack(MainActivity.class)
                        .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                        .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repo, owner))
                        .addNextIntentWithParentStack(RepoFilesActivity.getIntent(context, uri.toString()))
                        .addNextIntent(CodeViewerActivity.createIntent(context, uri.toString()));
            }
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getCreateIssueIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        Logger.e(segments);
        if (uri.getLastPathSegment() == null) return null;
        if (segments == null || segments.size() < 3 || !uri.getLastPathSegment().equalsIgnoreCase("new")) return null;
        if ("issues".equals(segments.get(2))) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntentWithParentStack(RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.ISSUES))
                    .addNextIntent(CreateIssueActivity.getIntent(context, owner, repo));
        }
        return null;
    }

    @Nullable private static TaskStackBuilder getGistFile(@NonNull Context context, @NonNull Uri uri) {
        if (uri.getHost().equalsIgnoreCase(HOST_GISTS_RAW)) {
            return TaskStackBuilder.create(context)
                    .addParentStack(MainActivity.class)
                    .addNextIntentWithParentStack(new Intent(context, MainActivity.class))
                    .addNextIntentWithParentStack(GistActivity.createIntent(context, uri.getPathSegments().get(1)))
                    .addNextIntent(CodeViewerActivity.createIntent(context, uri.toString()));
        }
        return null;
    }

    @Nullable private static String getGistId(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        return segments != null && !segments.isEmpty() ? uri.getLastPathSegment() : null;
    }
}
