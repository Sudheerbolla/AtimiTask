package com.atimitask

import android.app.Application
import com.atimitask.utils.AppLocalStorage
import com.atimitask.utils.StaticUtils
import com.atimitask.wsutils.WSInterface
import com.atimitask.wsutils.WSUtils
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BaseApplication : Application() {

    private var okHttpClient: OkHttpClient? = null

    override fun onCreate() {
        super.onCreate()
        appLocalStorage = AppLocalStorage().getInstance(this)
        initRetrofit()
    }

    /**
     * getting api interface accessible everywhere
     */
    val wsClientListener: WSInterface? get() = wsInterface

    /**
     * Initialises retrofit base object, which will be reused everywhere
     */
    fun initRetrofit() {
        val headerInterceptor = Interceptor { chain ->
            val requestBuilder: Request.Builder = chain.request().newBuilder()
            requestBuilder.header(StaticUtils.CONTENT_TYPE, StaticUtils.CONTENT_TYPE_JSON)
            requestBuilder.header(StaticUtils.ACCEPT, StaticUtils.CONTENT_TYPE_JSON)
            chain.proceed(requestBuilder.build())
        }
        okHttpClient = OkHttpClient().newBuilder().addInterceptor(headerInterceptor).addInterceptor(
            HttpLoggingInterceptor().setLevel(
                HttpLoggingInterceptor.Level.BODY
            )
        ).readTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WSUtils.CONNECTION_TIMEOUT, TimeUnit.SECONDS).build()
        wsInterface = buildRetrofitClient().create(WSInterface::class.java)
    }

    /**
     * Builds base retrofit builder with base URL
     */
    private fun buildRetrofitClient(): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(WSUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        return retrofit
    }

    companion object {
        private var baseApplication: BaseApplication? = null
        private var wsInterface: WSInterface? = null
        var appLocalStorage: AppLocalStorage? = null

        @get:Synchronized
        val instance: BaseApplication?
            get() {
                if (baseApplication == null) baseApplication = BaseApplication()
                return baseApplication
            }
    }
}