package me.ekhaled1836.tp.reddit.repository

import androidx.annotation.MainThread
import me.ekhaled1836.tp.reddit.db.RedditDatabase
import me.ekhaled1836.tp.reddit.model.AutoCompleteEntry
import java.util.concurrent.Executor

class SearchRepository(
    private val ioExecutor: Executor,
    private val db: RedditDatabase
) {

    fun insertResultIntoDb(entry: AutoCompleteEntry) {
        ioExecutor.execute {
            db.runInTransaction {
                db.getSearchesDAO().insertSearch(entry)
            }
        }
    }

    @MainThread
    fun getMostRecentSearches(): List<AutoCompleteEntry> {
        return db.getSearchesDAO().getMostRecentSearches()
    }

    @MainThread
    fun deleteMostRecentSearches() {
        ioExecutor.execute {
            db.runInTransaction {
                db.getSearchesDAO().deleteMostRecentSearches()
            }
        }
    }
}