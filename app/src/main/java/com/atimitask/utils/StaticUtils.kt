package com.atimitask.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

object StaticUtils {

    var SCREEN_HEIGHT = 0
    var SCREEN_WIDTH = 0

    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_TEXT_PLAIN = "text/plain"

    const val ACCEPT = "Accept"

    /**
     * Checks if there is internet connection
     */
    fun checkInternetConnection(context: Context): Boolean {
        val _activeNetwork =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return _activeNetwork != null && _activeNetwork.isConnectedOrConnecting
    }

    /**
     * Shows toast message
     */
    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows toast message
     */
    fun showToast(context: Context?, message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Hides keyboard
     */
    fun hideSoftKeyboard(act: Activity) {
        try {
            if (act.currentFocus != null) {
                val inputMethodManager =
                    act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(act.currentFocus!!.windowToken, 0)
            }
        } catch (ignored: Exception) {
        }
    }

    fun getRequestBody(jsonObject: JSONObject): RequestBody {
        val MEDIA_TYPE_TEXT: MediaType = CONTENT_TYPE_TEXT_PLAIN.toMediaTypeOrNull()!!
        return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString())
    }

    /**
     * Converts data as jsonobject acceptable by backend by retrofit
     */
    fun getRequestBodyJson(jsonObject: JSONObject): RequestBody {
        val MEDIA_TYPE_TEXT: MediaType = CONTENT_TYPE_JSON.toMediaTypeOrNull()!!
        return RequestBody.create(MEDIA_TYPE_TEXT, jsonObject.toString())
    }


}