/*
 * Copyright (C) 2017 Vasily Styagov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.octo.bear.pago;

import android.content.Context;

import io.octo.bear.pago.model.entity.ResponseCode;
import io.octo.bear.pago.model.exception.BillingException;
import rx.Completable;

import static io.octo.bear.pago.BillingServiceUtils.checkResponseAndThrowIfError;

/**
 * Created by shc on 18.07.16.
 */

class ConsumePurchaseCompletable extends Completable {
    ConsumePurchaseCompletable(final Context context, final String purchaseToken) {
        super(subscriber -> new BillingServiceConnection(context, service -> {
            try {
                final int codeNumber = service.consumePurchase(Pago.BILLING_API_VERSION, context.getPackageName(), purchaseToken);
                final ResponseCode code = ResponseCode.getByCode(codeNumber);
                checkResponseAndThrowIfError(code);
                subscriber.onCompleted();
            } catch (BillingException e) {
                subscriber.onError(e);
            }
        }).bindService());
    }

}
