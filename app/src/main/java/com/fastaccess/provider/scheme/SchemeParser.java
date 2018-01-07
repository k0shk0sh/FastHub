package com.fastaccess.provider.scheme;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefGetter;
import com.fastaccess.provider.markdown.MarkDownProvider;
import com.fastaccess.ui.modules.code.CodeViewerActivity;
import com.fastaccess.ui.modules.filter.issues.FilterIssuesActivity;
import com.fastaccess.ui.modules.gists.gist.GistActivity;
import com.fastaccess.ui.modules.repos.RepoPagerActivity;
import com.fastaccess.ui.modules.repos.RepoPagerMvp;
import com.fastaccess.ui.modules.repos.code.commit.details.CommitPagerActivity;
import com.fastaccess.ui.modules.repos.code.files.activity.RepoFilesActivity;
import com.fastaccess.ui.modules.repos.code.releases.ReleasesListActivity;
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity;
import com.fastaccess.ui.modules.repos.issues.issue.details.IssuePagerActivity;
import com.fastaccess.ui.modules.repos.projects.details.ProjectPagerActivity;
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.PullRequestPagerActivity;
import com.fastaccess.ui.modules.repos.wiki.WikiActivity;
import com.fastaccess.ui.modules.search.SearchActivity;
import com.fastaccess.ui.modules.trending.TrendingActivity;
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

    public static void launchUri(@NonNull Context context, @NonNull String url) {
        launchUri(context, Uri.parse(url), false);
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data) {
        launchUri(context, data, false);
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn) {
        launchUri(context, data, showRepoBtn, false);
    }

    public static void launchUri(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn, boolean newDocument) {
        Logger.e(data);
        Intent intent = convert(context, data, showRepoBtn);
        if (intent != null) {
            intent.putExtra(BundleConstant.SCHEME_URL, data.toString());
            if (newDocument) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            }
            if (context instanceof Service || context instanceof Application) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
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
        if (data.getPathSegments() != null && !data.getPathSegments().isEmpty()) {
            if (IGNORED_LIST.contains(data.getPathSegments().get(0))) return null;
            return getIntentForURI(context, data, showRepoBtn);
        }
        return null;
    }

    @Nullable private static Intent getIntentForURI(@NonNull Context context, @NonNull Uri data, boolean showRepoBtn) {
        String authority = data.getAuthority();
        boolean isEnterprise = PrefGetter.isEnterprise() && LinkParserHelper.isEnterprise(authority == null ? data.toString() : authority);
        if (HOST_GISTS.equals(data.getHost()) || "gist".equalsIgnoreCase(data.getPathSegments().get(0))) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(data.toString());
            if (!InputHelper.isEmpty(extension) && !MarkDownProvider.isArchive(data.getLastPathSegment())) {
                String url = data.toString();
                return CodeViewerActivity.createIntent(context, url, url);
            }
            String gist = getGistId(data);
            if (gist != null) {
                return GistActivity.createIntent(context, gist, isEnterprise);
            }
        } else if (HOST_GISTS_RAW.equalsIgnoreCase(data.getHost())) {
            return getGistFile(context, data);
        } else {
            if (MarkDownProvider.isArchive(data.toString())) return null;
            if (TextUtils.equals(authority, HOST_DEFAULT) || TextUtils.equals(authority, RAW_AUTHORITY) ||
                    TextUtils.equals(authority, API_AUTHORITY) || isEnterprise) {
                Intent trending = getTrending(context, data);
                Intent projects = getRepoProject(context, data);
                Intent userIntent = getUser(context, data);
                Intent repoIssues = getRepoIssueIntent(context, data);
                Intent repoPulls = getRepoPullRequestIntent(context, data);
                Intent createIssueIntent = getCreateIssueIntent(context, data);
                Intent pullRequestIntent = getPullRequestIntent(context, data, showRepoBtn);
                Intent issueIntent = getIssueIntent(context, data, showRepoBtn);
                Intent releasesIntent = getReleases(context, data, isEnterprise);
                Intent repoIntent = getRepo(context, data);
                Intent repoWikiIntent = getWiki(context, data);
                Intent commit = getCommit(context, data, showRepoBtn);
                Intent commits = getCommits(context, data, showRepoBtn);
                Intent blob = getBlob(context, data);
                Intent label = getLabel(context, data);
                Intent search = getSearchIntent(context, data);
                Optional<Intent> intentOptional = returnNonNull(trending, projects, search, userIntent, repoIssues, repoPulls,
                        pullRequestIntent, label, commit, commits, createIssueIntent, issueIntent, releasesIntent, repoIntent,
                        repoWikiIntent, blob);
                Optional<Intent> empty = Optional.empty();
                if (intentOptional != null && intentOptional.isPresent() && intentOptional != empty) {
                    Intent intent = intentOptional.get();
                    if (isEnterprise) {
                        if (intent.getExtras() != null) {
                            Bundle bundle = intent.getExtras();
                            bundle.putBoolean(BundleConstant.IS_ENTERPRISE, true);
                            intent.putExtras(bundle);
                        } else {
                            intent.putExtra(BundleConstant.IS_ENTERPRISE, true);
                        }
                    }
                    return intent;
                } else {
                    Intent intent = getGeneralRepo(context, data);
                    if (isEnterprise) {
                        if (intent != null && intent.getExtras() != null) {
                            Bundle bundle = intent.getExtras();
                            bundle.putBoolean(BundleConstant.IS_ENTERPRISE, true);
                            intent.putExtras(bundle);
                        } else if (intent != null) {
                            intent.putExtra(BundleConstant.IS_ENTERPRISE, true);
                        }
                    }
                    return intent;
                }
            }
        }
        return null;
    }

    private static boolean getInvitationIntent(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        return (segments != null && segments.size() == 3) && "invitations".equalsIgnoreCase(uri.getLastPathSegment());
    }

    @Nullable private static Intent getPullRequestIntent(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = null;
        String repo = null;
        String number = null;
        String fragment = uri.getEncodedFragment();//#issuecomment-332236665
        Long commentId = null;
        if (!InputHelper.isEmpty(fragment) && fragment.split("-").length > 1) {
            fragment = fragment.split("-")[1];
            if (!InputHelper.isEmpty(fragment)) {
                try {
                    commentId = Long.parseLong(fragment);
                } catch (Exception ignored) {}
            }
        }
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
        return PullRequestPagerActivity.createIntent(context, repo, owner, issueNumber, showRepoBtn,
                LinkParserHelper.isEnterprise(uri.toString()), commentId == null ? 0 : commentId);
    }

    @Nullable private static Intent getIssueIntent(@NonNull Context context, @NonNull Uri uri, boolean showRepoBtn) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = null;
        String repo = null;
        String number = null;
        String fragment = uri.getEncodedFragment();//#issuecomment-332236665
        Long commentId = null;
        if (!InputHelper.isEmpty(fragment) && fragment.split("-").length > 1) {
            fragment = fragment.split("-")[1];
            if (!InputHelper.isEmpty(fragment)) {
                try {
                    commentId = Long.parseLong(fragment);
                } catch (Exception ignored) {}
            }
        }
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
        return IssuePagerActivity.createIntent(context, repo, owner, issueNumber, showRepoBtn,
                LinkParserHelper.isEnterprise(uri.toString()), commentId == null ? 0 : commentId);
    }

    @Nullable private static Intent getLabel(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        String lastPath = segments.get(2);
        if ("labels".equalsIgnoreCase(lastPath)) {
            return FilterIssuesActivity.getIntent(context, owner, repoName, "label:\"" + segments.get(3) + "\"");
        }
        return null;
    }

    @Nullable private static Intent getRepo(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 2 || segments.size() > 3) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        if (!InputHelper.isEmpty(repoName)) {
            if (repoName.endsWith(".git")) repoName = repoName.replace(".git", "");
        }
        if (segments.size() == 3) {
            String lastPath = uri.getLastPathSegment();
            if ("milestones".equalsIgnoreCase(lastPath)) {
                return RepoPagerActivity.createIntent(context, repoName, owner, RepoPagerMvp.CODE, 4);
            } else if ("network".equalsIgnoreCase(lastPath)) {
                return RepoPagerActivity.createIntent(context, repoName, owner, RepoPagerMvp.CODE, 3);
            } else if ("stargazers".equalsIgnoreCase(lastPath)) {
                return RepoPagerActivity.createIntent(context, repoName, owner, RepoPagerMvp.CODE, 2);
            } else if ("watchers".equalsIgnoreCase(lastPath)) {
                return RepoPagerActivity.createIntent(context, repoName, owner, RepoPagerMvp.CODE, 1);
            } else if ("labels".equalsIgnoreCase(lastPath)) {
                return RepoPagerActivity.createIntent(context, repoName, owner, RepoPagerMvp.CODE, 5);
            } else {
                return null;
            }
        } else {
            return RepoPagerActivity.createIntent(context, repoName, owner);
        }
    }

    @Nullable private static Intent getRepoProject(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        String owner = segments.get(0);
        String repoName = segments.get(1);
        if (segments.size() == 3 && "projects".equalsIgnoreCase(segments.get(2))) {
            return RepoPagerActivity.createIntent(context, repoName, owner, RepoPagerMvp.PROJECTS);
        } else if (segments.size() == 4 && "projects".equalsIgnoreCase(segments.get(2))) {
            try {
                int projectId = Integer.parseInt(segments.get(segments.size() - 1));
                if (projectId > 0) {
                    return ProjectPagerActivity.Companion.getIntent(context, owner, repoName, projectId,
                            LinkParserHelper.isEnterprise(uri.toString()));
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    @Nullable private static Intent getWiki(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 3) return null;
        if ("wiki".equalsIgnoreCase(segments.get(2))) {
            String owner = segments.get(0);
            String repoName = segments.get(1);
            return WikiActivity.Companion.getWiki(context, repoName, owner,
                    "wiki".equalsIgnoreCase(uri.getLastPathSegment()) ? null : uri.getLastPathSegment());
        }
        return null;
    }

    /**
     * [[k0shk0sh, FastHub, issues], k0shk0sh/fastHub/(issues,pulls,commits, etc)]
     */
    @Nullable private static Intent getGeneralRepo(@NonNull Context context, @NonNull Uri uri) {
        //TODO parse deeper links to their associate views. meantime fallback to repoPage
        if (getInvitationIntent(uri)) {
            return null;
        }
        boolean isEnterprise = PrefGetter.isEnterprise() && Uri.parse(LinkParserHelper.getEndpoint(PrefGetter.getEnterpriseUrl())).getAuthority()
                .equalsIgnoreCase(uri.getAuthority());
        if (uri.getAuthority().equals(HOST_DEFAULT) || uri.getAuthority().equals(API_AUTHORITY) || isEnterprise) {
            List<String> segments = uri.getPathSegments();
            if (segments == null || segments.isEmpty()) return null;
            if (segments.size() == 1) {
                return getUser(context, uri);
            } else if (segments.size() > 1) {
                if (segments.get(0).equalsIgnoreCase("repos") && segments.size() >= 2) {
                    String owner = segments.get(1);
                    String repoName = segments.get(2);
                    return RepoPagerActivity.createIntent(context, repoName, owner);
                } else if ("orgs".equalsIgnoreCase(segments.get(0))) {
                    return null;
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
        List<String> segments = Stream.of(uri.getPathSegments())
                .filter(value -> !value.equalsIgnoreCase("api") || !value.equalsIgnoreCase("v3"))
                .toList();
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
        List<String> segments = Stream.of(uri.getPathSegments())
                .filter(value -> !value.equalsIgnoreCase("api") || !value.equalsIgnoreCase("v3"))
                .toList();
        if (segments.size() < 3 || !"commit".equals(segments.get(2))) return null;
        String login = segments.get(0);
        String repoId = segments.get(1);
        String sha = segments.get(3);
        return CommitPagerActivity.createIntent(context, repoId, login, sha, showRepoBtn);
    }

    @Nullable private static String getGistId(@NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments.size() != 1 && segments.size() != 2) return null;
        String gistId = segments.get(segments.size() - 1);
        if (InputHelper.isEmpty(gistId)) return null;
        if (TextUtils.isDigitsOnly(gistId)) return gistId;
        else if (gistId.matches("[a-fA-F0-9]+")) return gistId;
        else return null;
    }

    @Nullable private static Intent getUser(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty() && segments.size() == 1) {
            return UserPagerActivity.createIntent(context, segments.get(0));
        } else if (segments != null && !segments.isEmpty() && segments.size() > 1 && segments.get(0).equalsIgnoreCase("orgs")) {
            if ("invitation".equalsIgnoreCase(uri.getLastPathSegment())) {
                return null;
            } else if ("search".equalsIgnoreCase(uri.getLastPathSegment())) {
                String query = uri.getQueryParameter("q");
                return SearchActivity.getIntent(context, query);
            } else {
                return UserPagerActivity.createIntent(context, segments.get(1), true);
            }
        }
        return null;
    }

    @Nullable private static Intent getBlob(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() < 4) return null;
        String segmentTwo = segments.get(2);
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (InputHelper.isEmpty(extension) || TextUtils.isDigitsOnly(extension)) {
            Uri urlBuilder = LinkParserHelper.getBlobBuilder(uri);
            return RepoFilesActivity.getIntent(context, urlBuilder.toString());
        }
        if (segmentTwo.equals("blob") || segmentTwo.equals("tree")) {
            Uri urlBuilder = getBlobBuilder(uri);
            Logger.e(urlBuilder);
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
            Uri encoded = Uri.parse(uri.toString().replace("utf8=%E2%9C%93&amp;", ""));
            if (encoded.getQueryParameter("q") != null) {
                String query = encoded.getQueryParameter("q");
                return FilterIssuesActivity.getIntent(context, owner, repo, query);
            }
            return RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.ISSUES);
        }
        return null;
    }

    @Nullable private static Intent getRepoPullRequestIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && segments.size() == 3 && uri.getLastPathSegment().equalsIgnoreCase("pulls")) {
            String owner = segments.get(0);
            String repo = segments.get(1);
            Uri encoded = Uri.parse(uri.toString().replace("utf8=%E2%9C%93&amp;", ""));
            if (encoded.getQueryParameter("q") != null) {
                String query = encoded.getQueryParameter("q");
                return FilterIssuesActivity.getIntent(context, owner, repo, query);
            }
            return RepoPagerActivity.createIntent(context, repo, owner, RepoPagerMvp.PULL_REQUEST);
        }
        return null;
    }

    @Nullable private static Intent getReleases(@NonNull Context context, @NonNull Uri uri, boolean isEnterprise) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && segments.size() > 2) {
            if (uri.getPathSegments().get(2).equals("releases")) {
                String owner = segments.get(0);
                String repo = segments.get(1);
                String tag = uri.getLastPathSegment();
                if (tag != null && !repo.equalsIgnoreCase(tag)) {
                    if (TextUtils.isDigitsOnly(tag)) {
                        return ReleasesListActivity.getIntent(context, owner, repo, InputHelper.toLong(tag), isEnterprise);
                    } else {
                        return ReleasesListActivity.getIntent(context, owner, repo, tag, isEnterprise);
                    }
                }
                return ReleasesListActivity.getIntent(context, owner, repo);
            } else if (segments.size() > 3 && segments.get(3).equalsIgnoreCase("releases")) {
                String owner = segments.get(1);
                String repo = segments.get(2);
                String tag = uri.getLastPathSegment();
                if (tag != null && !repo.equalsIgnoreCase(tag)) {
                    if (TextUtils.isDigitsOnly(tag)) {
                        return ReleasesListActivity.getIntent(context, owner, repo, InputHelper.toLong(tag), isEnterprise);
                    } else {
                        return ReleasesListActivity.getIntent(context, owner, repo, tag, isEnterprise);
                    }
                }
                return ReleasesListActivity.getIntent(context, owner, repo);
            }
            return null;
        }
        return null;
    }

    @Nullable private static Intent getTrending(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments != null && !segments.isEmpty()) {
            if (uri.getPathSegments().get(0).equals("trending")) {
                String query = "";
                String lang = "";
                if (uri.getPathSegments().size() > 1) {
                    lang = uri.getPathSegments().get(1);
                }
                if (uri.getQueryParameterNames() != null && !uri.getQueryParameterNames().isEmpty()) {
                    query = uri.getQueryParameter("since");
                }
                return TrendingActivity.Companion.getTrendingIntent(context, lang, query);
            }
            return null;
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
            boolean isFeedback = "k0shk0sh/FastHub".equalsIgnoreCase(owner + "/" + repo);
            return CreateIssueActivity.getIntent(context, owner, repo, isFeedback);
        }
        return null;
    }

    @Nullable private static Intent getGistFile(@NonNull Context context, @NonNull Uri uri) {
        if (HOST_GISTS_RAW.equalsIgnoreCase(uri.getHost())) {
            return CodeViewerActivity.createIntent(context, uri.toString(), uri.toString());
        }
        return null;
    }

    @Nullable private static Intent getSearchIntent(@NonNull Context context, @NonNull Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null || segments.size() > 1) return null;
        String search = segments.get(0);
        if ("search".equalsIgnoreCase(search)) {
            Uri encoded = Uri.parse(uri.toString().replace("utf8=%E2%9C%93&amp;", ""));
            String query = encoded.getQueryParameter("q");
            Logger.e(encoded, query);
            return SearchActivity.getIntent(context, query);
        }
        return null;
    }
}
