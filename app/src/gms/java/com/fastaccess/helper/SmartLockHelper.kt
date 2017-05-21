package com.fastaccess.helper

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.fastaccess.App
import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.modules.login.LoginActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.CredentialRequest
import com.google.android.gms.common.api.GoogleApiClient

/**
 * Created by JediB on 5/21/2017.
 */

public class SmartLockHelper {

    companion object {

        private val RESOLUTION_CODE = 100
        private val RESOLUTION_CHOOSER_CODE = 101

        fun credential(loginActivity: LoginActivity) {
            if (App.getInstance().googleApiClient.isConnecting && !App.getInstance().googleApiClient.isConnected) {
                App.getInstance().googleApiClient.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {
                        doCredentialRequest(loginActivity)
                    }

                    override fun onConnectionSuspended(i: Int) {}
                })
            } else {
                doCredentialRequest(loginActivity)
            }
        }

        fun handleSuccessfulLogin(context: LoginActivity, userModel: Login, pass: String) {
            val credential = Credential.Builder(userModel.getLogin())
                    .setPassword(pass)
                    .setProfilePictureUri(Uri.parse(userModel.getAvatarUrl()))
                    .build()
            Auth.CredentialsApi.save(App.getInstance().googleApiClient, credential).setResultCallback { status ->
                if (status.isSuccess) {
                    context.onSuccessfullyLoggedIn()
                } else if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(context, RESOLUTION_CODE)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }

                } else {
                    Log.e(context.loggingTag, status.toString() + "")
                    context.onSuccessfullyLoggedIn()
                }
            }
        }

        fun handleSuccess(data: Intent, loginActivity: LoginActivity) {
            val credential = data.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
            loginActivity.doLogin(credential.id, credential.password)
        }

        private fun doCredentialRequest(loginActivity: LoginActivity) {
            val credentialRequest = CredentialRequest.Builder()
                    .setPasswordLoginSupported(true)
                    .build()
            Auth.CredentialsApi.request(App.getInstance().googleApiClient, credentialRequest).setResultCallback { credentialRequestResult ->
                if (credentialRequestResult.status.isSuccess) {
                    loginActivity.doLogin(credentialRequestResult.credential.id,
                            credentialRequestResult.credential.password)
                } else if (credentialRequestResult.status.hasResolution())
                    try {
                        credentialRequestResult.status.startResolutionForResult(loginActivity, RESOLUTION_CHOOSER_CODE)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                else {
                    Log.e(loginActivity.loggingTag, credentialRequestResult.status.toString() + "")
                }
            }
        }
    }

}