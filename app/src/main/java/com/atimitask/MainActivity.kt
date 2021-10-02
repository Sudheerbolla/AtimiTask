package com.atimitask

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.atimitask.databinding.ActivityMainBinding
import com.atimitask.fragments.FavouritesFragment
import com.atimitask.fragments.JokeFragment
import com.atimitask.model.JokeModel
import com.atimitask.utils.AppLocalStorage
import com.atimitask.interfaces.InteractWithActivity
import com.atimitask.utils.StaticUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity(), InteractWithActivity {

    lateinit var activityMainBinding: ActivityMainBinding

    private var appLocalStorage: AppLocalStorage? = null
    lateinit var favouriteJokes: ArrayList<JokeModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initComponents()
    }

    private fun initComponents() {
        favouriteJokes = ArrayList()
        appLocalStorage = BaseApplication.appLocalStorage
        onUpdateFavourites()
        setListeners()
        replaceFragment(JokeFragment(), false)
    }

    private fun setListeners(){
        activityMainBinding.imgBack.setOnClickListener {
            onBackPressed()
        }
        activityMainBinding.imgFavourites.setOnClickListener {
            replaceFragment(FavouritesFragment(), true)
        }
    }

    private fun getFavouritesFromLocalStorage(): ArrayList<JokeModel> {
        val storageData: String? = appLocalStorage?.getValue(
            AppLocalStorage.SP_FAVOURITES,
            Gson().toJson(ArrayList<JokeModel>())
        )
        favouriteJokes =
            Gson().fromJson(storageData, object : TypeToken<ArrayList<JokeModel>>() {}.type)
        return favouriteJokes
    }

    fun showTopBar(heading: String?, showBack: Boolean, showFav: Boolean) {
        if (TextUtils.isEmpty(heading)) activityMainBinding.txtHeading.setText(R.string.app_name) else activityMainBinding.txtHeading.text =
            heading
        activityMainBinding.imgBack.visibility = if (showBack) View.VISIBLE else View.INVISIBLE
        activityMainBinding.imgFavourites.visibility = if (showFav) View.VISIBLE else View.INVISIBLE
    }

    fun replaceFragment(fragment: Fragment, needToAddToBackStack: Boolean) {
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (needToAddToBackStack) fragmentTransaction.replace(R.id.mainFrame, fragment, tag)
            .addToBackStack(tag).commitAllowingStateLoss() else fragmentTransaction.replace(
            R.id.mainFrame, fragment, tag
        ).commitAllowingStateLoss()
    }

    fun clearAndReplaceFragment(fragment: Fragment) {
        clearBackStack()
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment, tag).addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun clearBackStack() {
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun clearBackStackCompletely() {
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun popBackStack() {
        StaticUtils.hideSoftKeyboard(this)
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate()
    }

    override fun onRemoveFromFavourites(joke: JokeModel) {
        if (favouriteJokes.isNotEmpty() && favouriteJokes.contains(joke)) {
            favouriteJokes.remove(joke)
            appLocalStorage?.setValue(AppLocalStorage.SP_FAVOURITES, Gson().toJson(favouriteJokes))
            StaticUtils.showToast(this, "Removed from favourites")
        } else {
            StaticUtils.showToast(this, "Something went wrong while removing from favourites")
        }
    }

    override fun onAddToFavourites(joke: JokeModel) {
        try {
            if (!favouriteJokes.contains(joke)) {
                favouriteJokes.add(joke)
                StaticUtils.showToast(this, "Added to favourites")
                appLocalStorage?.setValue(AppLocalStorage.SP_FAVOURITES, Gson().toJson(favouriteJokes))
            } else {
                StaticUtils.showToast(this, "Something went wrong while adding to favourites")
            }
        } catch (e: Exception) {
        }
    }

    override fun onUpdateFavourites() {
        favouriteJokes = getFavouritesFromLocalStorage()
    }

}