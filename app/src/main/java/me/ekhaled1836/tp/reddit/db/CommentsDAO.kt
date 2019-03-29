package me.ekhaled1836.tp.reddit.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.ekhaled1836.tp.reddit.model.Comment

@Dao
interface CommentsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertComments(comments: List<Comment>)

    @Query("SELECT * FROM comments WHERE subreddit = :subreddit AND parent_id = :parentId")
    fun getComments(subreddit: String, parentId: String): LiveData<List<Comment>>

    @Query("DELETE FROM comments WHERE subreddit = :subreddit AND parent_id = :parent_id")
    fun deleteComments(subreddit: String, parent_id: String)
}