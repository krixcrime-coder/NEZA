package com.kaizen.ai.di

import com.kaizen.ai.data.remote.GeminiApi
import com.kaizen.ai.data.remote.OpenAiApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("openai")
    fun provideOpenAiRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OpenAiApi.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("gemini")
    fun provideGeminiRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(GeminiApi.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideOpenAiApi(@Named("openai") retrofit: Retrofit): OpenAiApi =
        retrofit.create(OpenAiApi::class.java)

    @Provides
    @Singleton
    fun provideGeminiApi(@Named("gemini") retrofit: Retrofit): GeminiApi =
        retrofit.create(GeminiApi::class.java)
}
