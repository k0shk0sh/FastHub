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
 * Created by shc on 19.07.16.
 */
public class Order {

    public final Purchase purchase;
    public final String signature;
    public final String originalJson;

    public Order(Purchase purchase, String signature, String originalJson) {
        this.purchase = purchase;
        this.signature = signature;
        this.originalJson = originalJson;
    }

}
