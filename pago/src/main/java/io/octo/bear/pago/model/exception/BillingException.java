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

package io.octo.bear.pago.model.exception;

import io.octo.bear.pago.model.entity.ResponseCode;

/**
 * Created by shc on 14.07.16.
 */

public class BillingException extends Throwable {

    private final ResponseCode code;

    public BillingException(int code) {
        this(ResponseCode.getByCode(code));
    }

    public BillingException(ResponseCode code) {
        super(getErrorMessage(code));
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }

    private static String getErrorMessage(ResponseCode code) {
        switch (code) {
            case USER_CANCELED:
                return "User pressed back or canceled a dialog";
            case SERVICE_UNAVAILABLE:
                return "Network connection is down";
            case BILLING_UNAVAILABLE:
                return "Billing API version is not supported for the type requested";
            case ITEM_UNAVAILABLE:
                return "Requested product is not available for purchase";
            case DEVELOPER_ERROR:
                return "Invalid arguments provided to the API. This error can also indicate " +
                        "that the application was not correctly signed or properly set up for In-app Billing in Google Play, " +
                        "or does not have the necessary permissions in its manifest";
            case ERROR:
                return "Fatal error during the API action";
            case ITEM_ALREADY_OWNED:
                return "Failure to purchase since item is already owned";
            case ITEM_NOT_OWNED:
                return "Failure to consume since item is not owned";
            default:
                return "Unknown error";
        }
    }

}
