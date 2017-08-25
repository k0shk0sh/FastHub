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

import android.net.Uri;
import java.util.List;

/** The class contains a summary of the events which caused the job to be executed. */
public class TriggerReason {
    private final List<Uri> mTriggeredContentUris;

    TriggerReason(List<Uri> mTriggeredContentUris) {
        this.mTriggeredContentUris = mTriggeredContentUris;
    }

    public List<Uri> getTriggeredContentUris() {
        return mTriggeredContentUris;
    }
}
