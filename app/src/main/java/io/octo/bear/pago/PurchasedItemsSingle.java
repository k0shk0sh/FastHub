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
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import io.octo.bear.pago.model.entity.Order;
import io.octo.bear.pago.model.entity.Purchase;
import io.octo.bear.pago.model.entity.PurchaseType;
import io.octo.bear.pago.model.entity.ResponseCode;
import io.octo.bear.pago.model.exception.BillingException;
import rx.Single;

import static io.octo.bear.pago.BillingServiceUtils.GSON;
import static io.octo.bear.pago.BillingServiceUtils.checkResponseAndThrowIfError;
import static io.octo.bear.pago.BillingServiceUtils.retrieveResponseCode;

/**
 * Created by shc on 18.07.16.
 */

class PurchasedItemsSingle extends Single<List<Order>> {

    static final String RESPONSE_INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    static final String RESPONSE_INAPP_PURCHASE_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    static final String RESPONSE_INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

    PurchasedItemsSingle(final Context context, final PurchaseType type) {
        super((OnSubscribe<List<Order>>) subscriber ->
                new BillingServiceConnection(context, service -> {
                    try {
                        final Bundle purchases =
                                service.getPurchases(Pago.BILLING_API_VERSION, context.getPackageName(), type.value, null);

                        final ResponseCode code = retrieveResponseCode(purchases);

                        checkResponseAndThrowIfError(code);

                        final List<String> data = purchases.getStringArrayList(RESPONSE_INAPP_PURCHASE_DATA_LIST);
                        final List<String> signatures = purchases.getStringArrayList(RESPONSE_INAPP_PURCHASE_SIGNATURE_LIST);

                        if (data != null && signatures != null) {

                            final List<Order> result = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                String originalJson = data.get(i);
                                result.add(new Order(GSON.fromJson(originalJson, Purchase.class), signatures.get(i), originalJson));
                            }
                            subscriber.onSuccess(result);

                        } else {
                            subscriber.onError(new NullPointerException((data == null) ? "data is null" : "signatures is null"));
                        }

                    } catch (BillingException e) {
                        subscriber.onError(e);
                    }
                }).bindService()
        );
    }

}
