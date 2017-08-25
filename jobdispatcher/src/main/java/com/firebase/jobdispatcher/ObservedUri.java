// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//            http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.firebase.jobdispatcher;

import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Represents a single observed URI and any associated flags. */
public final class ObservedUri {

    private final Uri uri;

    private final int flags;

    /** Flag enforcement. */
    @IntDef(flag = true, value = Flags.FLAG_NOTIFY_FOR_DESCENDANTS)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {

        /**
         * Triggers if any descendants of the given URI change. Corresponds to the {@code
         * notifyForDescendants} of {@link android.content.ContentResolver#registerContentObserver}.
         */
        int FLAG_NOTIFY_FOR_DESCENDANTS = 1 << 0;
    }

    /**
     * Create a new ObservedUri.
     *
     * @param uri The URI to observe.
     * @param flags Any {@link Flags} associated with the URI.
     */
    public ObservedUri(@NonNull Uri uri, @Flags int flags) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null.");
        }
        this.uri = uri;
        this.flags = flags;
    }

    public Uri getUri() {
        return uri;
    }

    public int getFlags() {
        return flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObservedUri)) {
            return false;
        }

        ObservedUri otherUri = (ObservedUri) o;
        return flags == otherUri.flags && uri.equals(otherUri.uri);
    }

    @Override
    public int hashCode() {
        return uri.hashCode() ^ flags;
    }
}
