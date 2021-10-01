package com.atimitask

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.atimitask.databinding.ActivityMainBinding
import com.atimitask.model.JokeModel
import com.atimitask.utils.StaticUtils
import com.atimitask.wsutils.IParseListener
import com.atimitask.wsutils.WSCallBacksListener
import com.atimitask.wsutils.WSUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity(), IParseListener<JsonElement?> {
    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initComponents()
    }

    private fun initComponents() {
        requestForNewJoke()
    }

    private fun requestForNewJoke() {
        showProgressBar()

        WSCallBacksListener().requestForJsonObject(
            this,
            WSUtils.REQ_FOR_NEW_JOKE,
            BaseApplication.instance?.wsClientListener!!.getNewJoke(),
            this
        )
    }

    private fun showProgressBar() {
        activityMainBinding.progressLoading.visibility = View.VISIBLE
        activityMainBinding.txtJoke.visibility = View.GONE
        activityMainBinding.btnNewJoke.visibility = View.GONE
    }

    private fun showJoke() {
        activityMainBinding.progressLoading.visibility = View.GONE
        activityMainBinding.txtJoke.visibility = View.VISIBLE
        activityMainBinding.btnNewJoke.visibility = View.VISIBLE
    }

    fun onNewJokeClicked(view: View) {
        requestForNewJoke()
    }

    override fun onSuccess(code: Int, response: JsonElement?) {
        when (code) {
            WSUtils.REQ_FOR_NEW_JOKE -> {
                displayJoke(response)
            }
        }
    }

    private fun displayJoke(response: JsonElement?) {
        try {
            showJoke()
            val jokeModel = GsonBuilder().create()
                .fromJson<JokeModel>(response, object : TypeToken<JokeModel?>() {}.type)
            activityMainBinding.txtJoke.text = jokeModel.joke
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onError(code: Int, error: String?) {
        showJoke()
        activityMainBinding.txtJoke.text = "Error getting data: $error"
        activityMainBinding.btnNewJoke.text = getString(R.string.retry)
        StaticUtils.showToast(this, "Error getting data: $error")
    }

    override fun onNoNetwork(code: Int) {
        showJoke()
        activityMainBinding.txtJoke.text = getString(R.string.no_internet)
        activityMainBinding.btnNewJoke.text = getString(R.string.retry)
        StaticUtils.showToast(this, getString(R.string.no_internet))
    }

}