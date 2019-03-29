package me.ekhaled1836.tp.reddit.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.*

data class PostsListing(@SerializedName("data") val data: PostsListingData)

data class PostsListingData(
    @SerializedName("children") val children: List<PostDataHolder>,
    @SerializedName("after") val after: String?
)

data class PostDataHolder(@SerializedName("data") val data: Post)

/*@Entity(primaryKeys = arrayOf("firstName", "lastName"), ignoredColumns = arrayOf("picture")) @Fts4*/
@Entity(
    tableName = "posts",
    indices = [Index(value = ["subreddit"], unique = false), Index(value = ["sort"], unique = false)]
)
data class Post(
    @PrimaryKey/*(autoGenerate = true)*/ @SerializedName("name") @ColumnInfo(name = "name") val name: String,
    @SerializedName("title") @ColumnInfo(name = "title") val title: String,
    @SerializedName("author") @ColumnInfo(name = "author") val author: String,
    @SerializedName("subreddit") /* this seems mutable */ @ColumnInfo(
        name = "subreddit",
        collate = ColumnInfo.NOCASE
    ) val subreddit: String,
    @SerializedName("thumbnail") @ColumnInfo(name = "thumbnail") val thumbnail: String,
    @SerializedName("url") @ColumnInfo(name = "url") val url: String,
    @SerializedName("domain") @ColumnInfo(name = "domain") val domain: String,
    @SerializedName("selftext_html") @ColumnInfo(name = "selftext_html") val selftext_html: String?,
    @SerializedName("link_flair_text") @ColumnInfo(name = "link_flair_text") val link_flair_text: String?,
    @SerializedName("author_flair_text") @ColumnInfo(name = "author_flair_text") val author_flair_text: String?,
    @SerializedName("distinguished") @ColumnInfo(name = "distinguished") val distinguished: String?,
    @SerializedName("score") @ColumnInfo(name = "score") val score: Int,
    @SerializedName("num_comments") @ColumnInfo(name = "num_comments") val num_comments: Int,
    @SerializedName("created_utc") @ColumnInfo(name = "created_utc") val created_utc: Long,
    @SerializedName("gildings") @ColumnInfo(name = "gildings") val gildings: Gildings,
    @SerializedName("likes") @ColumnInfo(name = "likes") val likes: Boolean?,
    @SerializedName("saved") @ColumnInfo(name = "saved") val saved: Boolean,
    @SerializedName("is_video") @ColumnInfo(name = "is_video") val is_video: Boolean,
    @SerializedName("archived") @ColumnInfo(name = "archived") val archived: Boolean,
    @SerializedName("over_18") @ColumnInfo(name = "over_18") val over_18: Boolean,
    @SerializedName("hidden") @ColumnInfo(name = "hidden") val hidden: Boolean,
    @SerializedName("locked") @ColumnInfo(name = "locked") val locked: Boolean,
    @SerializedName("stickied") @ColumnInfo(name = "stickied") val stickied: Boolean,
    @SerializedName("spoiler") @ColumnInfo(name = "spoiler") val spoiler: Boolean,
    @SerializedName("preview") @ColumnInfo(name = "preview") val preview: RedditMediaPreview?,
    @SerializedName("secure_media") @ColumnInfo(name = "secure_media") val secure_media: RedditSecureMedia?,
    @ColumnInfo(name = "sort") var sort: String,
    @ColumnInfo(name = "period") var period: String?,
    /*@Ignore */@ColumnInfo(name = "type") var type: Char = '0',
    /*@Ignore */@ColumnInfo(name = "images_urls") var images_urls: LinkedList<String>,
    /*@Ignore */@ColumnInfo(name = "video_urls") var video_urls: LinkedList<String>,
    /*@Ignore */@ColumnInfo(name = "scale") var scale: Float?
) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}

data class RedditMediaPreview(
    @SerializedName("images") val images: List<RedditImagesPreview>,
    @SerializedName("reddit_video_preview") val reddit_video_preview: RedditVideo?
)

data class RedditImagesPreview(
    @SerializedName("source") val source: RedditMediaSource,
    @SerializedName("resolutions") val resolutions: List<RedditMediaSource>?
    /*@SerializedName("variants") val variants: RedditMediaVariant?*/
)

/*data class RedditMediaVariant(
    @SerializedName("gif") val gif: RedditVariantPreview?,
    @SerializedName("mp4") val mp4: RedditVariantPreview?
)*/

/*data class RedditVariantPreview(
    @SerializedName("source") val source: RedditMediaSource,
    @SerializedName("resolutions") val resolutions: List<RedditMediaSource>?
)*/

data class RedditMediaSource(
    @SerializedName("url") val url: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class RedditSecureMedia(
    @SerializedName("reddit_video") val reddit_video: RedditVideo?
)

data class RedditVideo(
    @SerializedName("dash_url") val dash_url: String,
    @SerializedName("scrubber_media_url") val scrubber_media_url: String,
    @SerializedName("fallback_url") val fallback_url: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int
)

/*data class SearchSuggestions(
        @SerializedName("subreddits") val subreddits: List<SearchSuggestion>
)

data class SearchSuggestion(
        @SerializedName("name") val name: String
)*/