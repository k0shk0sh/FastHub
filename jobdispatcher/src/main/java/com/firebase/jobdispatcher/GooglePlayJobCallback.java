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

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Wraps the GooglePlay-specific callback class in a JobCallback-compatible interface.
 */
/* package */ final class GooglePlayJobCallback implements JobCallback {

    private static final String DESCRIPTOR = "com.google.android.gms.gcm.INetworkTaskCallback";
    /** The only supported transaction ID. */
    private static final int TRANSACTION_TASK_FINISHED = IBinder.FIRST_CALL_TRANSACTION + 1;

    private final IBinder mRemote;

    public GooglePlayJobCallback(IBinder binder) {
        mRemote = binder;
    }

    @Override
    public void jobFinished(@JobService.JobResult int status) {
        Parcel request = Parcel.obtain();
        Parcel response = Parcel.obtain();
        try {
            request.writeInterfaceToken(DESCRIPTOR);
            request.writeInt(status);

            mRemote.transact(TRANSACTION_TASK_FINISHED, request, response, 0);

            response.readException();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } finally {
            request.recycle();
            response.recycle();
        }
    }
}
