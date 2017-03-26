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

package io.octo.bear.pago.model.entity;

/**
 * Created by shc on 14.07.16.
 */

public class Purchase {

    public final boolean autoRenewing;
    public final String orderId;
    public final String packageName;
    public final String productId;
    public final long purchaseTime;
    public final int purchaseState;
    public final String developerPayload;
    public final String purchaseToken;

    public Purchase(
            boolean autoRenewing,
            String orderId,
            String packageName,
            String productId,
            long purchaseTime,
            int purchaseState,
            String developerPayload,
            String purchaseToken
    ) {
        this.autoRenewing = autoRenewing;
        this.orderId = orderId;
        this.packageName = packageName;
        this.productId = productId;
        this.purchaseTime = purchaseTime;
        this.purchaseState = purchaseState;
        this.developerPayload = developerPayload;
        this.purchaseToken = purchaseToken;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "orderId='" + orderId + '\'' +
                ", packageName='" + packageName + '\'' +
                ", productId='" + productId + '\'' +
                ", purchaseTime=" + purchaseTime +
                ", purchaseState=" + purchaseState +
                ", developerPayload='" + developerPayload + '\'' +
                ", purchaseToken='" + purchaseToken + '\'' +
                '}';
    }
}
