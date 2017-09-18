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

package com.google.android.gms.gcm;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

/**
 * Parcelable class to wrap the binder we send to the client over IPC. Only included for the benefit
 * of tests.
 */
@Keep
public final class PendingCallback implements Parcelable {
  public static final Creator<PendingCallback> CREATOR =
      new Creator<PendingCallback>() {
        @Override
        public PendingCallback createFromParcel(Parcel parcel) {
          return new PendingCallback(parcel);
        }

        @Override
        public PendingCallback[] newArray(int i) {
          return new PendingCallback[i];
        }
      };
  private final IBinder mBinder;

  public PendingCallback(Parcel in) {
    mBinder = in.readStrongBinder();
  }

  public IBinder getIBinder() {
    return mBinder;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeStrongBinder(mBinder);
  }
}
