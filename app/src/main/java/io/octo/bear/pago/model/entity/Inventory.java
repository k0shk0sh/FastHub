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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shc on 20.07.16.
 */

public class Inventory {

    private final Map<String, Sku> items;

    public Inventory() {
        items = new HashMap<>();
    }

    public void addItem(final Sku sku) {
        items.put(sku.productId, sku);
    }

    public Sku getSku(final String productId) {
        return items.get(productId);
    }

}
