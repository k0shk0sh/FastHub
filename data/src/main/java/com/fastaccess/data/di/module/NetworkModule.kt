package com.fastaccess.data.di.module

import android.content.Context
import android.net.Uri
import com.fastaccess.data.BuildConfig
import com.fastaccess.data.di.annotations.ForApplication
import com.fastaccess.data.di.annotations.ForAuth
import com.fastaccess.data.repository.services.LoginService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module
class NetworkModule(private val url: String = "") {

    @Singleton @Provides fun provideGson(): Gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

    @Singleton @Provides fun provideAuthInterceptor() = AuthenticationInterceptor() // TODO(add auth)

    @Singleton @Provides @ForAuth fun provideForAuthInterceptor() = AuthenticationInterceptor()

    @Singleton @Provides fun provideHttpClient(auth: AuthenticationInterceptor, @ForApplication context: Context): OkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(ContentTypeInterceptor())
            .addInterceptor(auth)
            .addInterceptor(PaginationInterceptor())
            .addInterceptor(ChuckInterceptor(context))
            .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }))
            .build()

    @Singleton @Provides fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GithubResponseConverter(gson))
            .client(okHttpClient)
            .baseUrl(url)
            .build()

    @Singleton @Provides fun provideLoginService(retrofit: Retrofit): LoginService = retrofit.create(LoginService::class.java)
}

class AuthenticationInterceptor(var otp: String? = null,
                                var token: String? = null,
                                var isScrapping: Boolean = false) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        token?.let { builder.header("Authorization", if (it.startsWith("Basic")) it else "token $it") }
        otp?.let { builder.addHeader("X-GitHub-OTP", it.trim()) }
        if (!isScrapping) builder.addHeader("User-Agent", "FastHub")
        val request = builder.build()
        return chain.proceed(request)
    }
}

private class ContentTypeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Content-type", "application/vnd.github.v3+json")
                .method(request.method(), request.body())
                .build())
    }
}

private class PaginationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val headers = chain.request().headers()
        headers?.let {
            if (it.values("Accept").contains("application/vnd.github.html") ||
                    it.values("Accept").contains("application/vnd.github.VERSION" + ".raw")) {
                return response//return them as they are.
            }
        }
        if (response.isSuccessful) {
            if (response.peekBody(1).string() == "[") {
                var json = "{"
                val link = response.header("link")
                if (link != null) {
                    val links = link.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    for (link1 in links) {
                        val pageLink = link1.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val page = Uri.parse(pageLink[0].replace("[<>]".toRegex(), "")).getQueryParameter("page")
                        val rel = pageLink[1].replace("\"".toRegex(), "").replace("rel=", "")
                        if (page != null) json += String.format("\"%s\":\"%s\",", rel.trim { it <= ' ' }, page)
                    }
                }
                json += String.format("\"items\":%s}", response.body()!!.string())
                return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(), json)).build()
            } else if (response.header("link") != null) {
                val link = response.header("link")
                var pagination = ""
                val links = link!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (link1 in links) {
                    val pageLink = link1.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val page = Uri.parse(pageLink[0].replace("[<>]".toRegex(), "")).getQueryParameter("page")
                    val rel = pageLink[1].replace("\"".toRegex(), "").replace("rel=", "")
                    if (page != null) pagination += String.format("\"%s\":\"%s\",", rel.trim { it <= ' ' }, page)
                }
                if (pagination.isNotEmpty()) {
                    val body = response.body()!!.string()
                    return response.newBuilder().body(ResponseBody.create(response.body()!!.contentType(),
                            "{" + pagination + body.substring(1, body.length))).build()
                }
            }
        }
        return response
    }
}

private class GithubResponseConverter(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return try {
            if (type === String::class.java) {
                StringResponseConverter()
            } else GsonConverterFactory.create(gson).responseBodyConverter(type, annotations, retrofit)
        } catch (ignored: OutOfMemoryError) {
            null
        }

    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {
        return GsonConverterFactory.create(gson).requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    private class StringResponseConverter : Converter<ResponseBody, String> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): String {
            return value.string()
        }
    }
}