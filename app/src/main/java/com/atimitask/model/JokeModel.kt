package com.atimitask.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//Id:String, joke:String, status: String

@Parcelize
data class JokeModel(
    @SerializedName("id") val jokeId: String = "",
    val joke: String = "",
    var status: String = "",
) : Parcelable {
    override fun toString(): String {
        return "Joke: $joke,\nStatus: $status"
    }

    override fun equals(other: Any?): Boolean {
        if (other is JokeModel)
            return other.jokeId.equals(jokeId)
        else
            return super.equals(other)
    }
}