package com.atimitask.wsutils

import android.content.Context
import android.util.Log
import com.atimitask.utils.StaticUtils
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

class WSCallBacksListener {

    /**
     * This function is used if we are expecting JsonObject as the output of API
     */
    fun requestForJsonObject(
        context: Context?,
        requestCode: Int,
        call: Call<JsonElement>,
        iParserListener: IParseListener<JsonElement?>
    ) {
        try {
//            CoroutineScope(Dispatchers.IO).launch {
            if (!StaticUtils.checkInternetConnection(context!!)) {
                iParserListener.onNoNetwork(requestCode)
            } else {
                val callback: Callback<JsonElement?> = object : Callback<JsonElement?> {
                    override fun onResponse(
                        call: Call<JsonElement?>,
                        response: Response<JsonElement?>
                    ) {
                        if (response.body() != null && (response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED)) iParserListener.onSuccess(
                            requestCode,
                            response.body()
                        ) else iParserListener.onError(requestCode, response.message())
                    }

                    override fun onFailure(call: Call<JsonElement?>, throwable: Throwable) {
                        iParserListener.onError(requestCode, throwable.toString())
                    }
                }
                call.enqueue(callback)
            }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * This function is used if we are expecting JsonArray as the output of API
     */
    fun requestForJsonArray(
        context: Context?,
        requestCode: Int,
        call: Call<JsonElement?>,
        iParserListener: IParseListener<JsonElement?>
    ) {
        try {
            if (!StaticUtils.checkInternetConnection(context!!)) {
                iParserListener.onNoNetwork(requestCode)
            } else {
                val callback: Callback<JsonElement?> = object : Callback<JsonElement?> {
                    override fun onResponse(
                        call: Call<JsonElement?>,
                        response: Response<JsonElement?>
                    ) {
                        if (response.body() != null && (response.code() == HttpURLConnection.HTTP_OK || response.code() == HttpURLConnection.HTTP_CREATED)) {
                            if (response.body() is JsonObject) iParserListener.onSuccess(
                                requestCode,
                                response.body()?.getAsJsonObject()
                            ) else if (response.body() is JsonArray) iParserListener.onSuccess(
                                requestCode,
                                response.body()?.getAsJsonArray()
                            ) else iParserListener.onSuccess(requestCode, response.body())
                        } else if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                            Log.e("response : ", response.message())
                            iParserListener.onSuccess(requestCode, null)
                        } else {
                            var errorText: String? = null
                            try {
                                errorText =
                                    if (response.errorBody() != null && response.errorBody()!!
                                            .string() != null
                                    ) response.errorBody()!!
                                        .string() else response.message()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            iParserListener.onError(requestCode, errorText)
                        }
                    }

                    override fun onFailure(call: Call<JsonElement?>, throwable: Throwable) {
                        iParserListener.onError(requestCode, throwable.toString())
                    }
                }
                call.enqueue(callback)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}