package com.atimitask.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//Id:String, joke:String, status: String

@Parcelize
data class JokeModel(
    @SerializedName("Id") val jokeId: String = "",
    val joke: String = "",
    var status: String = "",
) : Parcelable {
    override fun toString(): String {
        return "Joke: $joke,\nStatus: $status"
    }

}