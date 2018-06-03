package com.fastaccess.github.ui.modules.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastaccess.data.persistence.models.ValidationError
import com.fastaccess.github.ui.modules.auth.usecase.GetAccessTokenUseCase
import com.fastaccess.github.ui.modules.auth.usecase.LoginUseCase
import com.fastaccess.github.ui.modules.auth.usecase.LoginWithAccessTokenUseCase
import javax.inject.Inject

/**
 * Created by Kosh on 21.05.18.
 */
class LoginViewModel @Inject constructor(private val loginUserCase: LoginUseCase,
                                         private val accessTokenUseCase: GetAccessTokenUseCase,
                                         private val loginWithAccessTokenUseCase: LoginWithAccessTokenUseCase) : ViewModel() {

    val validationLiveData = MutableLiveData<ValidationError>()

    fun login(userName: String? = null,
              password: String? = null,
              twoFactorCode: String? = null,
              endPoint: String? = null,
              isBasicAuth: Boolean = true) {
        validationLiveData.value = ValidationError(ValidationError.FieldType.TWO_FACTOR, !twoFactorCode.isNullOrBlank())
        validationLiveData.value = ValidationError(ValidationError.FieldType.URL, !endPoint.isNullOrBlank())
        validationLiveData.value = ValidationError(ValidationError.FieldType.PASSWORD, !password.isNullOrBlank())
        validationLiveData.value = ValidationError(ValidationError.FieldType.USERNAME, !userName.isNullOrBlank())
    }
}