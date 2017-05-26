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

import android.os.Bundle;

import com.google.gson.Gson;

import io.octo.bear.pago.model.entity.ResponseCode;
import io.octo.bear.pago.model.exception.BillingException;

import static io.octo.bear.pago.model.entity.ResponseCode.ERROR;

/**
 * Created by shc on 15.07.16.
 */
final class BillingServiceUtils {
    static final String RESPONSE_CODE = "RESPONSE_CODE";
    static final Gson GSON = new Gson();

    static ResponseCode retrieveResponseCode(final Bundle result) {
        return result == null ? ERROR : ResponseCode.getByCode(result.getInt(RESPONSE_CODE));
    }

    static void checkResponseAndThrowIfError(ResponseCode code) throws BillingException {
        if (code != ResponseCode.OK) throw new BillingException(code);
    }

}
