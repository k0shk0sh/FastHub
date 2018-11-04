package com.fastaccess.data.persistence.models

/**
 * Created by Kosh on 03.06.18.
 */

data class ValidationError(val fieldType: FieldType,
                           val isValid: Boolean = false) {
    enum class FieldType {
        USERNAME, PASSWORD, TWO_FACTOR, URL
    }
}

data class FastHubErrors(val errorType: ErrorType = ErrorType.CONNECTION,
                         val message: String? = null,
                         val resId: Int? = null) {
    enum class ErrorType {
        TWO_FACTOR, CONNECTION, TIME_OUT, OTHER
    }
}