package com.fastaccess.provider.rest

import com.apollographql.apollo.ApolloClient
import com.fastaccess.BuildConfig
import com.fastaccess.helper.PrefGetter
import com.fastaccess.provider.scheme.LinkParserHelper

/**
 * Created by Hashemsergani on 12.09.17.
 */

object ApolloProdivder {

    fun getApollo(enterprise: Boolean) = ApolloClient.builder()
            .serverUrl("${if (enterprise && PrefGetter.isEnterprise()) {
                "${LinkParserHelper.getEndpoint(PrefGetter.getEnterpriseUrl())}/"
            } else {
                BuildConfig.REST_URL
            }}graphql")
            .okHttpClient(RestProvider.provideOkHttpClient())
            .build()

}