package com.streambox.core.di

import android.util.Log
import com.streambox.core.BuildConfig

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.bearerAuth
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class TmdbClient
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class TvMazeClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun json(): Json = Json { ignoreUnknownKeys = true; explicitNulls = false }

    /** One OkHttp = one connection pool + thread pool, shared by every Ktor client. */
    @Provides @Singleton
    fun okHttp(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides @Singleton @TmdbClient
    fun tmdbClient(okHttp: OkHttpClient, json: Json): HttpClient = HttpClient(OkHttp) {
        engine { preconfigured = okHttp }
        commonConfig(json)
        defaultRequest {
            url("https://api.themoviedb.org/3/")      // trailing slash matters
            bearerAuth(BuildConfig.TMDB_TOKEN)
        }
    }

    @Provides @Singleton @TvMazeClient
    fun tvMazeClient(okHttp: OkHttpClient, json: Json): HttpClient = HttpClient(OkHttp) {
        engine { preconfigured = okHttp }
        commonConfig(json)
        defaultRequest { url("https://api.tvmaze.com/") }
    }

    private fun HttpClientConfig<*>.commonConfig(json: Json) {
        expectSuccess = true                           // non-2xx → exception (like Retrofit suspend funs)
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            connectTimeoutMillis = 10_000
            requestTimeoutMillis = 15_000
        }
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 2)
            retryIf(maxRetries = 2) { _, response -> response.status == HttpStatusCode.TooManyRequests }
            exponentialDelay()
        }
        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.NONE
            logger = object : Logger {
                override fun log(message: String) { Log.d("Ktor", message) }
            }
            sanitizeHeader { it == "Authorization" }   // never log the token
        }
    }
}