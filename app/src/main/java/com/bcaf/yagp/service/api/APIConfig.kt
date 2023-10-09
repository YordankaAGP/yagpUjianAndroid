package com.bcaf.yagp.service.api

import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIConfig {
    private const val BASE_URL = "https://70a2-103-8-185-130.ngrok-free.app/cicool/cicool/api/"


    class TokenInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request : Request = chain.request().newBuilder()
                .header("X-Api-Key", "5023FF61BC81BE6383A7105E649D97AF")
                .build()
            return chain.proceed(request)
        }

    }

    fun getApiService(): APIService {

        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val tokenInterceptor = TokenInterceptor()

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(APIService::class.java)

    }
}