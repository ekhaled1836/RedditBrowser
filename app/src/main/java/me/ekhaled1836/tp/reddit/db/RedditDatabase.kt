package me.ekhaled1836.tp.reddit.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.ekhaled1836.tp.reddit.model.AutoCompleteEntry
import me.ekhaled1836.tp.reddit.model.Comment
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.model.RoomConverters

@Database(
    entities = [Post::class, Comment::class, AutoCompleteEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class RedditDatabase : RoomDatabase() {
    abstract fun getPostsDAO(): PostsDAO

    abstract fun getSearchesDAO(): SearchesDAO

    abstract fun getCommentsDAO(): CommentsDAO

    companion object {

        @Volatile
        private var INSTANCE: RedditDatabase? = null

        fun getDatabase(context: Context): RedditDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RedditDatabase::class.java, "reddit.db"
            ).fallbackToDestructiveMigration().build()
    }
}