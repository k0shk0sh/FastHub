package com.fastaccess.fasthub.deeplink;

import com.airbnb.deeplinkdispatch.DeepLinkEntry;
import com.airbnb.deeplinkdispatch.Parser;
import com.fastaccess.fasthub.commit.list.CommitsListActivity;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CommitDeepLinkModuleLoader implements Parser {
  public static final List<DeepLinkEntry> REGISTRY = Collections.unmodifiableList(Arrays.asList(
    new DeepLinkEntry("http://api.github.com/{login}/{repo}/pull/{number}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://github.com/{login}/{repo}/pull/{number}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://api.github.com/{login}/{repo}/pull/{number}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://github.com/{login}/{repo}/pull/{number}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://api.github.com/repos/{login}/{repo}/commits/", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://github.com/repos/{login}/{repo}/commits/", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://api.github.com/repos/{login}/{repo}/commits/", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://github.com/repos/{login}/{repo}/commits/", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://api.github.com/repos/{login}/{repo}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://api.github.com/{login}/{repo}/pull/{number}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://github.com/repos/{login}/{repo}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://github.com/{login}/{repo}/pull/{number}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://api.github.com/repos/{login}/{repo}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://api.github.com/{login}/{repo}/pull/{number}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://github.com/repos/{login}/{repo}/commits/{oid}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://github.com/{login}/{repo}/pull/{number}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://api.github.com/{login}/{repo}/commits/{branch}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://github.com/{login}/{repo}/commits/{branch}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://api.github.com/{login}/{repo}/commits/{branch}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://github.com/{login}/{repo}/commits/{branch}", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://api.github.com/{login}/{repo}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("http://github.com/{login}/{repo}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://api.github.com/{login}/{repo}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null),
    new DeepLinkEntry("https://github.com/{login}/{repo}/commits", DeepLinkEntry.Type.CLASS, CommitsListActivity.class, null)
  ));

  @Override
  public DeepLinkEntry parseUri(String uri) {
    for (DeepLinkEntry entry : REGISTRY) {
      if (entry.matches(uri)) {
        return entry;
      }
    }
    return null;
  }
}
