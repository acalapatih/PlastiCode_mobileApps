package com.vicryfahreza.storyapp.service

import com.dicoding.plasticode.BuildConfig
import com.dicoding.plasticode.service.ApiService
import com.dicoding.plasticode.utils.Constant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private var token: String = ""
    private var finalToken: String = ""

    fun setToken(value: String) {
        token = value
        finalToken = "Bearer $token"
    }

    private fun getHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            if(request.header("No-Authentication") == null) {
                if(token.isNotEmpty()) {
                    request = request.newBuilder()
                        .addHeader(Constant.TYPE_HEADERS, finalToken)
                        .build()
                }
            }
            chain.proceed(request)
        }
    }

    fun getApiService(): ApiService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(getHeaderInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}