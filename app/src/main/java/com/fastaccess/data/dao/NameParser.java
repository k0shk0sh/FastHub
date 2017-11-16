package com.fastaccess.data.dao;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.fastaccess.helper.InputHelper;
import com.fastaccess.provider.scheme.LinkParserHelper;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Kosh on 11 Feb 2017, 11:03 PM
 */

@Getter @Setter
public class NameParser {

    public String name;
    public String username;
    public boolean isEnterprise;

    public NameParser(@Nullable String url) {
        if (!InputHelper.isEmpty(url)) {
            boolean isEnterprise = LinkParserHelper.isEnterprise(url);
            if (isEnterprise) {
                url = url.replace("api/v3/", "");
            }
            Uri uri = Uri.parse(url);
            List<String> segments = uri.getPathSegments();
            if (segments == null || segments.size() < 2) {
                return;
            }
            boolean isFirstPathIsRepo = (segments.get(0).equalsIgnoreCase("repos") || segments.get(0).equalsIgnoreCase("repo"));
            this.username = isFirstPathIsRepo ? segments.get(1) : segments.get(0);
            this.name = isFirstPathIsRepo ? segments.get(2) : segments.get(1);
            this.isEnterprise = isEnterprise;
        }
    }

    @Override public String toString() {
        return "NameParser{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
