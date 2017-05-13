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
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import io.octo.bear.pago.model.entity.Inventory;
import io.octo.bear.pago.model.entity.PurchaseType;
import io.octo.bear.pago.model.entity.ResponseCode;
import io.octo.bear.pago.model.entity.Sku;
import io.octo.bear.pago.model.exception.BillingException;
import rx.Single;

import static io.octo.bear.pago.BillingServiceUtils.GSON;
import static io.octo.bear.pago.BillingServiceUtils.checkResponseAndThrowIfError;
import static io.octo.bear.pago.BillingServiceUtils.retrieveResponseCode;

/**
 * Created by shc on 14.07.16.
 */

class ProductDetailsSingle extends Single<Inventory> {

    static final String RESPONSE_DETAILS_LIST = "DETAILS_LIST";
    static final String EXTRA_ITEM_ID_LIST = "ITEM_ID_LIST";

    ProductDetailsSingle(final Context context, final PurchaseType type, final List<String> purchaseIds) {
        super((OnSubscribe<Inventory>) subscriber -> new BillingServiceConnection(context, service -> {
                    try {
                        final Bundle querySku = new Bundle();
                        querySku.putStringArrayList(EXTRA_ITEM_ID_LIST, new ArrayList<>(purchaseIds));

                        final Bundle details = service.getSkuDetails(Pago.BILLING_API_VERSION, context.getPackageName(), type.value, querySku);
                        final ResponseCode responseCode = retrieveResponseCode(details);

                        checkResponseAndThrowIfError(responseCode);

                        final ArrayList<String> skus = details.getStringArrayList(RESPONSE_DETAILS_LIST);
                        if (skus == null) throw new RuntimeException("skus list is not supplied");

                        final Inventory inventory = new Inventory();
                        for (String serializedSku : skus) {
                            inventory.addItem(GSON.fromJson(serializedSku, Sku.class));
                        }

                        subscriber.onSuccess(inventory);
                    } catch (RemoteException | BillingException e) {
                        subscriber.onError(e);
                    }
                }).bindService()
        );
    }

}
