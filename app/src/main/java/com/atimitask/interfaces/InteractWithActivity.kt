package com.atimitask.interfaces

import com.atimitask.model.JokeModel

interface InteractWithActivity {
    fun onRemoveFromFavourites(joke: JokeModel)
    fun onAddToFavourites(joke: JokeModel)
    fun onUpdateFavourites()
}