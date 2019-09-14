package com.fastaccess.github.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastaccess.data.model.FastHubErrors
import com.fastaccess.domain.response.GithubErrorResponse
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * Created by Kosh on 12.05.18.
 */
abstract class BaseViewModel : ViewModel() {
    @Inject lateinit var gson: Gson
    val error = MutableLiveData<FastHubErrors>()
    val progress = MutableLiveData<Boolean>()
    val logoutProcess = MutableLiveData<Boolean>()
    val counter = MutableLiveData<Int>()
    private val disposable = CompositeDisposable()

    protected fun add(disposable: Disposable) = this.disposable.add(disposable)

    protected fun disposeAll() = disposable.clear()

    protected fun handleError(throwable: Throwable) {
        hideProgress()
        if (throwable is HttpException) {
            val response = throwable.response()
            val errorBody: GithubErrorResponse? = gson.fromJson(response?.errorBody()?.string(), GithubErrorResponse::class.java)
            if (errorBody?.message?.equals("Validation Failed", true) == true) {
                errorBody.message = errorBody.errors?.firstOrNull()
            }
            val message = errorBody?.message ?: response?.message()
            Timber.e("$errorBody")
            val code = response?.code()
            if (code == 401) { // OTP
                val twoFactor = response.headers()["X-GitHub-OTP"]
                if (twoFactor != null) {
                    error.postValue(
                        FastHubErrors(
                            FastHubErrors.ErrorType.TWO_FACTOR, message = message
                        )
                    )
                } else {
                    error.postValue(FastHubErrors(FastHubErrors.ErrorType.OTHER, resId = R.string.failed_login, message = message))
                }
            } else {
                error.postValue(
                    FastHubErrors(
                        FastHubErrors.ErrorType.OTHER, resId = if (message.isNullOrEmpty()) {
                            R.string.network_error
                        } else {
                            null
                        }, message = message
                    )
                )
            }
            return
        }
        error.postValue(FastHubErrors(FastHubErrors.ErrorType.OTHER, resId = getErrorResId(throwable)))
    }

    protected fun showProgress() {
        progress.postValue(true)
    }

    protected fun hideProgress() {
        progress.postValue(false)
    }

    protected fun postCounter(count: Int) {
        counter.postValue(count)
    }

    protected fun <T> callApi(observable: Observable<T>): Observable<T> = observable
        .doOnSubscribe { showProgress() }
        .doOnNext { hideProgress() }
        .doOnError { handleError(it) }
        .doOnComplete { hideProgress() }

    protected fun callApi(completable: Completable): Completable = completable
        .doOnSubscribe { showProgress() }
        .doOnComplete { hideProgress() }
        .doOnError { handleError(it) }
        .doOnComplete { hideProgress() }

    protected fun <T> justSubscribe(observable: Observable<T>) = add(callApi(observable).subscribe({}, { it.printStackTrace() }))
    protected fun justSubscribe(completable: Completable) = add(callApi(completable).subscribe({}, { it.printStackTrace() }))

    override fun onCleared() {
        super.onCleared()
        disposeAll()
    }

    private fun getErrorResId(throwable: Throwable): Int = when (throwable) {
        is IOException -> R.string.request_error
        is TimeoutException -> R.string.unexpected_error
        else -> R.string.network_error
    }
}