// Copyright 2016 Google, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////

package com.firebase.jobdispatcher;

final class BundleProtocol {
    static final String PACKED_PARAM_BUNDLE_PREFIX = "com.firebase.jobdispatcher.";

    // PACKED_PARAM values are only read on the client side, so as long as the
    // extraction process gets the same changes then it's fine.
    static final String PACKED_PARAM_CONSTRAINTS = "constraints";
    static final String PACKED_PARAM_LIFETIME = "persistent";
    static final String PACKED_PARAM_RECURRING = "recurring";
    static final String PACKED_PARAM_SERVICE = "service";
    static final String PACKED_PARAM_TAG = "tag";
    static final String PACKED_PARAM_EXTRAS = "extras";
    static final String PACKED_PARAM_TRIGGER_TYPE = "trigger_type";
    static final String PACKED_PARAM_TRIGGER_WINDOW_END = "window_end";
    static final String PACKED_PARAM_TRIGGER_WINDOW_START = "window_start";
    static final int TRIGGER_TYPE_EXECUTION_WINDOW = 1;
    static final int TRIGGER_TYPE_IMMEDIATE = 2;
    static final int TRIGGER_TYPE_CONTENT_URI = 3;
    static final String PACKED_PARAM_RETRY_STRATEGY_INITIAL_BACKOFF_SECONDS =
        "initial_backoff_seconds";
    static final String PACKED_PARAM_RETRY_STRATEGY_MAXIMUM_BACKOFF_SECONDS =
        "maximum_backoff_seconds";
    static final String PACKED_PARAM_RETRY_STRATEGY_POLICY = "retry_policy";
    static final String PACKED_PARAM_REPLACE_CURRENT = "replace_current";
    static final String PACKED_PARAM_CONTENT_URI_FLAGS_ARRAY = "content_uri_flags_array";
    static final String PACKED_PARAM_CONTENT_URI_ARRAY = "content_uri_array";
    static final String PACKED_PARAM_TRIGGERED_URIS = "triggered_uris";
    static final String PACKED_PARAM_OBSERVED_URI = "observed_uris";

    BundleProtocol() {
    }
}
