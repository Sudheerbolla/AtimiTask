package com.atimitask.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.atimitask.BaseApplication
import com.atimitask.R
import com.atimitask.model.JokeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object StaticUtils {

    const val CONTENT_TYPE = "Content-Type"
    const val CONTENT_TYPE_JSON = "application/json"
    const val CONTENT_TYPE_TEXT_PLAIN = "text/plain"
    const val CONTENT_TYPE_EMAIL = "message/rfc822"

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

    fun getFavouritesFromLocalStorage(): ArrayList<JokeModel> {
        val storageData: String? = BaseApplication.appLocalStorage?.getValue(
            AppLocalStorage.SP_FAVOURITES,
            Gson().toJson(ArrayList<JokeModel>())
        )
        val favouriteJokes: ArrayList<JokeModel> =
            Gson().fromJson(storageData, object : TypeToken<ArrayList<JokeModel>>() {}.type)
        return favouriteJokes
    }

    fun shareJoke(context: Context, message: String, isEmail: Boolean) {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = if (isEmail) CONTENT_TYPE_EMAIL else CONTENT_TYPE_TEXT_PLAIN
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_TITLE, R.string.atimi_share_your_joke)
        }, context.getString(R.string.atimi_share_your_joke))
        context.startActivity(share)
        /* ShareCompat.IntentBuilder.from(mainActivity)
          .setType("message/rfc822")
          .setText(jokeModel?.joke)
          .setChooserTitle(getString(R.string.atimi_share_your_joke))
          .startChooser()*/
    }

    fun checkForAppPackageAndShare(appName: String, context: Context, message: String) {
        try {
            var packageName = ""
            var resolved = false
            val shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                type = CONTENT_TYPE_TEXT_PLAIN
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_TITLE, R.string.atimi_share_your_joke)
            }, context.getString(R.string.atimi_share_your_joke))
            val pm = context.getPackageManager()
            val activityList = pm.queryIntentActivities(shareIntent, 0)
            if (appName.equals("Twitter")) {
                packageName = "com.twitter.android.PostActivity"
            } else if (appName.equals("Facebook")) {
                packageName = "facebook"
            }
            for (app in activityList) {
                if (packageName.equals(app.activityInfo.name) || packageName.contains(app.activityInfo.name)) {
                    val activity = app.activityInfo
                    val name =
                        ComponentName(activity.applicationInfo.packageName, activity.name)
                    shareIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    shareIntent.setComponent(name)
                    resolved = true
                    break
                }
            }
            if (resolved) {
                context.startActivity(shareIntent)
            } else {
                showToast(context, "$appName App is not installed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showDeleteFromFavDialog(context: Context, onClickListener: View.OnClickListener) {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
        builder1.setMessage(context.getString(R.string.delete_from_fav_alert))
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Delete", { dialog, id ->
                run {
                    onClickListener.onClick(null)
                    dialog.cancel()
                }
            })

        builder1.setNeutralButton(
            "Cancel", { dialog, id ->
                dialog.cancel()
            })

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }
}