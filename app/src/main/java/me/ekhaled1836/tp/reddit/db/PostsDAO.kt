package me.ekhaled1836.tp.reddit.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.ekhaled1836.tp.reddit.model.Post

@Dao
interface PostsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<Post>) /*vararg*/

    @Query("SELECT * FROM posts WHERE subreddit = :subreddit AND sort = :sort ORDER BY indexInResponse ASC")
    fun getPosts(subreddit: String, sort: String): DataSource.Factory<Int, Post>

    @Query("SELECT * FROM posts WHERE subreddit = :subreddit AND sort = :sort AND period = :period ORDER BY indexInResponse ASC")
    fun getPosts(subreddit: String, sort: String, period: String): DataSource.Factory<Int, Post>

    @Query("DELETE FROM posts WHERE subreddit = :subreddit AND sort = :sort")
    fun deletePosts(subreddit: String, sort: String)

    @Query("DELETE FROM posts WHERE subreddit = :subreddit AND sort = :sort AND period = :period")
    fun deletePosts(subreddit: String, sort: String, period: String)

    @Query("SELECT MAX(indexInResponse) + 1 FROM posts WHERE subreddit = :subreddit AND sort = :sort")
    fun getNextIndexInSubreddit(subreddit: String, sort: String): Int

    @Query("SELECT MAX(indexInResponse) + 1 FROM posts WHERE subreddit = :subreddit AND sort = :sort AND period = :period")
    fun getNextIndexInSubreddit(subreddit: String, sort: String, period:String): Int
}