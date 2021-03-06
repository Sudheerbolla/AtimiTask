package com.atimitask.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.atimitask.MainActivity
import com.atimitask.R
import com.atimitask.adapter.FavouritesAdapter
import com.atimitask.databinding.FragmentFavListBinding
import com.atimitask.interfaces.IClickListener
import com.atimitask.interfaces.InteractWithActivity
import com.atimitask.model.JokeModel
import com.atimitask.utils.DividerItemDecoration
import com.atimitask.utils.StaticUtils
import java.util.*

class FavouritesFragment : Fragment(), IClickListener {

    private lateinit var favouritesAdapter: FavouritesAdapter
    private lateinit var fragmentFavListBinding: FragmentFavListBinding
    private lateinit var mainActivity: MainActivity
    lateinit var favouriteJokes: ArrayList<JokeModel>
    internal lateinit var onInteractWithActivity: InteractWithActivity

    fun setOnInteractWithActivityListener(onInteractWithActivity: InteractWithActivity) {
        this.onInteractWithActivity = onInteractWithActivity
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

    override fun onResume() {
        super.onResume()
        mainActivity.showTopBar(getString(R.string.fav_list_heading), true, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favouriteJokes = mainActivity.favouriteJokes
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentFavListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_fav_list, container, false)
        intiComponents()
        return fragmentFavListBinding.root
    }

    private fun intiComponents() {
        checkViewVisibility()
        setFavouritesAdapter()
    }

    private fun checkViewVisibility() {
        if (favouriteJokes.isNullOrEmpty()) {
            fragmentFavListBinding.txtNoData.visibility = View.VISIBLE
            fragmentFavListBinding.recyclerView.visibility = View.GONE
        } else {
            fragmentFavListBinding.txtNoData.visibility = View.GONE
            fragmentFavListBinding.recyclerView.visibility = View.VISIBLE
            fragmentFavListBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
            fragmentFavListBinding.recyclerView.addItemDecoration(
                DividerItemDecoration(mainActivity, DividerItemDecoration.HORIZONTAL_LIST)
            )
        }
    }

    private fun setFavouritesAdapter() {
        favouritesAdapter = FavouritesAdapter(mainActivity.favouriteJokes, this)
        fragmentFavListBinding.recyclerView.adapter = favouritesAdapter
    }

    override fun onClick(view: View, position: Int) {
        when (view.id) {
            R.id.txtUnFavourite -> {
                StaticUtils.showDeleteFromFavDialog(mainActivity) {
                    removeFromFav(position)
                }
            }
            /*
            R.id.txtOpen -> {
                mainActivity.replaceFragment(
                    JokeFragment.newInstance(favouriteJokes.get(position), true),
                    true
                )
            }
            */
            else -> {
                mainActivity.replaceFragment(
                    JokeFragment.newInstance(favouriteJokes.get(position), true),
                    true
                )
            }
        }
    }

    private fun removeFromFav(position: Int) {
        onInteractWithActivity.onRemoveFromFavourites(favouriteJokes.get(position))
        favouritesAdapter.notifyItemRemoved(position)
        if (favouriteJokes.size < 1) checkViewVisibility()
    }

    override fun onLongClick(view: View, position: Int) {
        StaticUtils.showDeleteFromFavDialog(mainActivity) {
            removeFromFav(position)
        }
    }

}