package me.ekhaled1836.tp.reddit.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class CommentsListing(@SerializedName("data") val data: CommentsListingData)

data class CommentsListingData(@SerializedName("children") val children: List<CommentDataHolder>)

data class CommentDataHolder(
    @SerializedName("kind") val kind: String,
    @SerializedName("data") val data: MoreComment
)

@Entity(
    tableName = "comments",
    indices = [Index(value = ["subreddit"], unique = false), Index(value = ["parent_id"], unique = true)]
)
data class Comment(
    @PrimaryKey @SerializedName("name") @ColumnInfo(name = "name") val name: String,
    @SerializedName("parent_id") @ColumnInfo(name = "parent_id") val parent_id: String,
    @SerializedName("link_id") @ColumnInfo(name = "link_id") val link_id: String,
    @SerializedName("subreddit") @ColumnInfo(name = "subreddit") val subreddit: String,
    @SerializedName("body_html") @ColumnInfo(name = "body_html") val body_html: String,
    @SerializedName("author") @ColumnInfo(name = "author") val author: String,
    @SerializedName("author_flair_text") @ColumnInfo(name = "author_flair_text") val author_flair_text: String?,
    @SerializedName("distinguished") @ColumnInfo(name = "distinguished") val distinguished: String?,
    @SerializedName("score") @ColumnInfo(name = "score") val score: Int,
    @SerializedName("depth") @ColumnInfo(name = "depth") val depth: Int,
    @SerializedName("created_utc") @ColumnInfo(name = "created_utc") val created_utc: Long,
    @SerializedName("gildings") @ColumnInfo(name = "gildings") val gildings: Gildings,
    @SerializedName("likes") @ColumnInfo(name = "likes") val likes: Boolean?,
    @SerializedName("saved") @ColumnInfo(name = "saved") val saved: Boolean,
    @SerializedName("is_submitter") @ColumnInfo(name = "is_submitter") val is_submitter: Boolean,
    @SerializedName("archived") @ColumnInfo(name = "archived") val archived: Boolean,
    @SerializedName("score_hidden") @ColumnInfo(name = "score_hidden") val score_hidden: Boolean,
    @SerializedName("stickied") @ColumnInfo(name = "stickied") val stickied: Boolean
    /*@ColumnInfo(name = "sort") var sort: String*/
) : MoreComment

data class More(
    @SerializedName("name") val name: String,
    @SerializedName("count") val count: Int,
    @SerializedName("depth") val depth: Int,
    @SerializedName("children") val children: List<String>
) : MoreComment