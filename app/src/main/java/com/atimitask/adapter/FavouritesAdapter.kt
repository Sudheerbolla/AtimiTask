package com.atimitask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.atimitask.R
import com.atimitask.databinding.ItemFavBinding
import com.atimitask.interfaces.IClickListener
import com.atimitask.model.JokeModel
import com.atimitask.utils.swipe.ViewBinderHelper

class FavouritesAdapter(
    itemsData: ArrayList<JokeModel>,
    private var iClickListener: IClickListener?
) : RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    private var mItemManger: ViewBinderHelper = ViewBinderHelper()

    val favouritesArrayList: ArrayList<JokeModel> = itemsData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_fav, parent, false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        mItemManger.setOpenOnlyOne(true)
        viewHolder.bind(favouritesArrayList[position], iClickListener, mItemManger)
    }

    override fun getItemCount(): Int {
        return favouritesArrayList.size
    }

    class ViewHolder(var binding: ItemFavBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(joke: JokeModel, iClickListener: IClickListener?, mItemManger: ViewBinderHelper) {
            mItemManger.bind(binding.swipeLayout, adapterPosition.toString())
            mItemManger.closeAll()

            binding.txtId.text = "Joke ID: ${joke.jokeId}"
            binding.txtJoke.text = joke.joke
            binding.txtUnFavourite.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
            binding.txtOpen.setOnClickListener { v ->
                if (iClickListener != null) iClickListener.onClick(v, adapterPosition)
            }
        }

    }

}