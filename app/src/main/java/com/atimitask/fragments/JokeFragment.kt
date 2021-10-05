package com.atimitask.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.atimitask.BaseApplication
import com.atimitask.MainActivity
import com.atimitask.R
import com.atimitask.databinding.FragmentJokeBinding
import com.atimitask.interfaces.InteractWithActivity
import com.atimitask.model.JokeModel
import com.atimitask.utils.StaticUtils
import com.atimitask.wsutils.IParseListener
import com.atimitask.wsutils.WSCallBacksListener
import com.atimitask.wsutils.WSUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

private const val ARG_PARAM1 = "jokeModel"
private const val ARG_PARAM2 = "isFromFav"

class JokeFragment : Fragment(), IParseListener<JsonElement?>, View.OnClickListener {

    private lateinit var fragmentJokeBinding: FragmentJokeBinding
    private lateinit var mainActivity: MainActivity
    private var jokeModel: JokeModel? = null
    private var isFromFav: Boolean? = false

    internal lateinit var onInteractWithActivity: InteractWithActivity

    fun setOnInteractWithActivityListener(onInteractWithActivity: InteractWithActivity) {
        this.onInteractWithActivity = onInteractWithActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jokeModel = it.getParcelable(ARG_PARAM1)
            isFromFav = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is InteractWithActivity) setOnInteractWithActivityListener(context)
            mainActivity = context as MainActivity
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentJokeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_joke, container, false)
        intiComponents()
        return fragmentJokeBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (isFromFav!!)
            mainActivity.showTopBar("Favourite Joke", true, false)
        else
            mainActivity.showTopBar("Random Dad Jokes", false, true)
    }

    private fun intiComponents() {
        if (isFromFav!!) {
            showJoke()
            fragmentJokeBinding.txtJoke.text = jokeModel?.joke
            fragmentJokeBinding.btnFavUnFav.text = getString(R.string.unfavourite)
            fragmentJokeBinding.btnNewJoke.visibility = View.GONE
        } else {
            requestForNewJoke()
        }
        setListeners()
    }

    private fun setListeners() {
        fragmentJokeBinding.btnNewJoke.setOnClickListener(this)
        fragmentJokeBinding.btnFavUnFav.setOnClickListener(this)
        fragmentJokeBinding.txtShareViaEmail.setOnClickListener(this)
        fragmentJokeBinding.txtFBShare.setOnClickListener(this)
        fragmentJokeBinding.txtTwitterShare.setOnClickListener(this)
        fragmentJokeBinding.txtShare.setOnClickListener(this)
    }

    private fun requestForNewJoke() {
        showProgressBar()

        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_NEW_JOKE,
            BaseApplication.instance?.wsClientListener!!.getNewJoke(),
            this
        )
    }

    private fun showProgressBar() {
        fragmentJokeBinding.progressLoading.visibility = View.VISIBLE
        fragmentJokeBinding.txtJoke.visibility = View.GONE
        fragmentJokeBinding.btnNewJoke.visibility = View.GONE
        fragmentJokeBinding.btnFavUnFav.visibility = View.GONE
        fragmentJokeBinding.linControls.visibility = View.GONE

        fragmentJokeBinding.btnFavUnFav.text = getString(R.string.favourite)
    }

    private fun showJoke() {
        fragmentJokeBinding.progressLoading.visibility = View.GONE
        fragmentJokeBinding.txtJoke.visibility = View.VISIBLE
        fragmentJokeBinding.btnNewJoke.visibility = View.VISIBLE
        fragmentJokeBinding.btnFavUnFav.visibility = View.VISIBLE
        fragmentJokeBinding.linControls.visibility = View.VISIBLE
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
            jokeModel = GsonBuilder().create()
                .fromJson<JokeModel>(response, object : TypeToken<JokeModel?>() {}.type)
            fragmentJokeBinding.txtJoke.text = jokeModel?.joke
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onError(code: Int, error: String?) {
        showJoke()
        fragmentJokeBinding.txtJoke.text = "Error getting data: $error"
        fragmentJokeBinding.btnNewJoke.text = getString(R.string.retry)
        fragmentJokeBinding.btnFavUnFav.visibility = View.GONE
        StaticUtils.showToast(mainActivity, "Error getting data: $error")
    }

    override fun onNoNetwork(code: Int) {
        showJoke()
        fragmentJokeBinding.btnFavUnFav.visibility = View.GONE
        fragmentJokeBinding.txtJoke.text = getString(R.string.no_internet)
        fragmentJokeBinding.btnNewJoke.text = getString(R.string.retry)
        StaticUtils.showToast(mainActivity, getString(R.string.no_internet))
    }

    companion object {

        @JvmStatic
        fun newInstance(jokeModel: JokeModel, isFromFav: Boolean) =
            JokeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, jokeModel)
                    putBoolean(ARG_PARAM2, isFromFav)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtShare -> {
                StaticUtils.shareJoke(mainActivity, jokeModel?.joke!!, false)
            }
            R.id.txtShareViaEmail -> {
                StaticUtils.shareJoke(mainActivity, jokeModel?.joke!!, true)
            }
            R.id.txtFBShare -> {
                StaticUtils.checkForAppPackageAndShare("Facebook", mainActivity, jokeModel?.joke!!)
            }
            R.id.txtTwitterShare -> {
                StaticUtils.checkForAppPackageAndShare("Twitter", mainActivity, jokeModel?.joke!!)
            }
            R.id.btnFavUnFav -> {
                onFavouriteUnFavClicked()
            }
            R.id.btnNewJoke -> {
                requestForNewJoke()
            }
        }
    }

    private fun onFavouriteUnFavClicked() {
        if (fragmentJokeBinding.btnFavUnFav.text.equals(getString(R.string.favourite))) {
            fragmentJokeBinding.btnFavUnFav.text = getString(R.string.unfavourite)
            onInteractWithActivity.onAddToFavourites(jokeModel!!)
        } else {
            fragmentJokeBinding.btnFavUnFav.text = getString(R.string.favourite)
            onInteractWithActivity.onRemoveFromFavourites(jokeModel!!)
        }
    }

}