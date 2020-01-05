package com.fastaccess.domain.di

import android.net.Uri
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import com.fastaccess.domain.BuildConfig
import com.fastaccess.domain.services.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import github.type.CustomType
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import tech.linjiang.pandora.Pandora
import java.io.IOException
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Kosh on 11.05.18.
 */
@Module
class NetworkModule {

    @Singleton @Provides fun provideGson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()

    @Singleton @Provides fun provideInterceptor() = AuthenticationInterceptor()

    @Singleton @Provides fun provideHttpLogging() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Singleton @Provides fun provideHttpClient(
        auth: AuthenticationInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(ContentTypeInterceptor())
        .addInterceptor(auth)
        .addInterceptor(PaginationInterceptor())
        .addInterceptor(Pandora.get().interceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @Named("imgurClient") @Singleton @Provides fun provideHttpClientForImgur(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(Pandora.get().interceptor)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                requestBuilder.header("Authorization", "Client-ID " + BuildConfig.IMGUR_CLIENT_ID)
                requestBuilder.method(original.method, original.body)
                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        })
        .build()

    @Named("apolloClient") @Singleton @Provides fun provideHttpClientForApollo(
        auth: AuthenticationInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(auth)
        .addInterceptor(Pandora.get().interceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()


    @Singleton @Provides fun provideRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.REST_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GithubResponseConverter(gson))
        .client(okHttpClient)
        .build()

    @Singleton @Provides fun provideRetrofitBuilder(
        gson: Gson,
        okHttpClient: OkHttpClient
    ): Retrofit.Builder = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GithubResponseConverter(gson))
        .client(okHttpClient)

    @Singleton @Provides fun provideApollo(@Named("apolloClient") okHttpClient: OkHttpClient): ApolloClient = ApolloClient.builder()
        .serverUrl(BuildConfig.GRAPHQL_REST_URL)
        .okHttpClient(okHttpClient)
        .addCustomTypeAdapter(CustomType.URI, UriApolloAdapter())
        .addCustomTypeAdapter(CustomType.DATETIME, DateApolloAdapter())
        .addCustomTypeAdapter(CustomType.HTML, ObjectApolloAdapter())
        .addCustomTypeAdapter(CustomType.ID, ObjectApolloAdapter())
        .addCustomTypeAdapter(CustomType.GITOBJECTID, ObjectApolloAdapter())
        .build()

    @Singleton @Provides fun provideLoginService(retrofit: Retrofit): LoginService = retrofit.create(LoginService::class.java)
    @Singleton @Provides fun provideUserService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)
    @Singleton @Provides fun provideNotificationService(retrofit: Retrofit): NotificationService = retrofit.create(NotificationService::class.java)
    @Singleton @Provides fun provideOrganizationService(retrofit: Retrofit): OrganizationService = retrofit.create(OrganizationService::class.java)
    @Singleton @Provides fun provideIssueService(retrofit: Retrofit): IssuePrService = retrofit.create(IssuePrService::class.java)
    @Singleton @Provides fun provideRepoService(retrofit: Retrofit): RepoService = retrofit.create(RepoService::class.java)
    @Singleton @Provides fun provideReviewService(retrofit: Retrofit): ReviewService = retrofit.create(ReviewService::class.java)
    @Singleton @Provides fun provideCommitService(retrofit: Retrofit): CommitService = retrofit.create(CommitService::class.java)
    @Singleton @Provides fun providePullRequestService(retrofit: Retrofit): PullRequestService = retrofit.create(PullRequestService::class.java)
    @Singleton @Provides fun provideScrappingService(retrofit: Retrofit): ScrapService = retrofit.create(ScrapService::class.java)

    @Singleton @Provides fun provideImgurService(
        retrofit: Retrofit.Builder,
        @Named("imgurClient") okHttpClient: OkHttpClient
    ): ImgurService = retrofit
        .baseUrl(BuildConfig.IMGUR_URL)
        .client(okHttpClient)
        .build()
        .create(ImgurService::class.java)
}

class AuthenticationInterceptor(
    var otp: String? = null,
    var token: String? = null,
    var isScrapping: Boolean = false
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        token?.let { builder.header("Authorization", if (it.startsWith("Basic")) it else "token $it") }
        otp?.let {
            if (!it.isEmpty()) builder.addHeader("X-GitHub-OTP", it.trim())
        }
        if (!isScrapping) builder.addHeader("User-Agent", "FastHub")
        val request = builder.build()
        return chain.proceed(request)
    }
}

private class ContentTypeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            request.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Content-type", "application/vnd.github.v3+json")
                .method(request.method, request.body)
                .build()
        )
    }
}

private class PaginationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val headers = chain.request().headers
        headers.let {
            if (it.values("Accept").contains("application/vnd.github.html") ||
                it.values("Accept").contains("application/vnd.github.VERSION" + ".raw")
            ) {
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
                json += String.format("\"items\":%s}", response.body?.string())
                return response.newBuilder().body(json.toResponseBody(response.body?.contentType())).build()
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
                    val body = response.body?.string()
                    return response.newBuilder().body(
                        ("{" + pagination + body?.substring(1, body.length)).toResponseBody(response.body?.contentType())
                    ).build()
                }
            }
        }
        return response
    }
}

private class GithubResponseConverter(
    private val gson: Gson,
    private val creator: GsonConverterFactory = GsonConverterFactory.create(gson)
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return try {
            if (type === String::class.java) {
                StringResponseConverter()
            } else {
                creator.responseBodyConverter(type, annotations, retrofit)
            }
        } catch (ignored: OutOfMemoryError) {
            null
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return creator.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }

    private class StringResponseConverter : Converter<ResponseBody, String> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): String {
            return value.string()
        }
    }
}

private class UriApolloAdapter : CustomTypeAdapter<URI> {
    override fun encode(value: URI): CustomTypeValue<String> = CustomTypeValue.GraphQLString(value.toString())
    override fun decode(value: CustomTypeValue<*>): URI = URI.create(value.value.toString())
}

private class ObjectApolloAdapter : CustomTypeAdapter<Any> {
    override fun encode(value: Any): CustomTypeValue<String> = CustomTypeValue.GraphQLString(value.toString())
    override fun decode(value: CustomTypeValue<*>): Any = value.value
}

private class DateApolloAdapter : CustomTypeAdapter<Date> {
    override fun encode(value: Date): CustomTypeValue<*> = CustomTypeValue.fromRawValue(value)
    override fun decode(value: CustomTypeValue<*>): Date {
        return kotlin.runCatching {
            val date = value.value as String
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).parse(date) // because Github API is the best of all. /shrug
        }.getOrDefault(Date())
    }
}