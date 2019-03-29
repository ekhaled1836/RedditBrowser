package me.ekhaled1836.tp.reddit.model

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import com.google.gson.reflect.TypeToken
import java.util.*

interface MoreComment

class MoreCommentDeserializer: JsonDeserializer<MoreComment?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): MoreComment? {
        val jObject = json as JsonObject
        val countObj = jObject.get("count")

        return if(countObj != null) {
            context?.deserialize(json, More::class.java)
        } else {
            context?.deserialize(json, Comment::class.java)
        }
    }
}

data class Gildings(
    @SerializedName("gid_1") val gid_1: Short,
    @SerializedName("gid_2") val gid_2: Short,
    @SerializedName("gid_3") val gid_3: Short
)

class RoomConverters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toRedditImagesPreview(preview: String?): RedditMediaPreview? {
            return if (preview == null) null else Gson().fromJson(preview, RedditMediaPreview::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun fromRedditImagesPreview(preview: RedditMediaPreview?): String? {
            return if (preview == null) null else Gson().toJson(preview)
        }

        @TypeConverter
        @JvmStatic
        fun toRedditSecureMedia(secure_media: String?): RedditSecureMedia? {
            return if (secure_media == null) null else Gson().fromJson(secure_media, RedditSecureMedia::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun fromRedditSecureMedia(secure_media: RedditSecureMedia?): String? {
            return if (secure_media == null) null else Gson().toJson(secure_media)
        }

        @TypeConverter
        @JvmStatic
        fun toRedditGildings(glidings: String?): Gildings? {
            return if (glidings == null) null else Gson().fromJson(glidings, Gildings::class.java)
        }

        @TypeConverter
        @JvmStatic
        fun fromRedditGildings(glidings: Gildings?): String? {
            return if (glidings == null) null else Gson().toJson(glidings)
        }

        @TypeConverter
        @JvmStatic
        fun toStringLinkedList(list: String?): LinkedList<String>? {
            if (list == null) return null
            val collectionType = object : TypeToken<LinkedList<String>>() {}.type
            return Gson().fromJson(list, collectionType)
        }

        @TypeConverter
        @JvmStatic
        fun fromStringLinkedList(list: LinkedList<String>?): String? {
            return if (list == null) null else Gson().toJson(list)
        }
    }
}