package net.trejj.talk.placespicker.model

import androidx.annotation.Keep
import net.trejj.talk.placespicker.model.Venue
import com.google.gson.annotations.SerializedName

@Keep
data class Response(
        @SerializedName("confident")
        val confident: Boolean,
        @SerializedName("venues")
        val venues: List<Venue>
)