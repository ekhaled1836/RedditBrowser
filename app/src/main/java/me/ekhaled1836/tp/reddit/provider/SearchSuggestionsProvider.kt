package me.ekhaled1836.tp.reddit.provider

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import android.util.Log
import me.ekhaled1836.tp.reddit.api.RedditApi
import me.ekhaled1836.tp.reddit.db.RedditDatabase
import me.ekhaled1836.tp.reddit.model.AutoCompleteRoot
import me.ekhaled1836.tp.reddit.repository.SearchRepository
import retrofit2.Call
import java.net.URLDecoder
import java.util.concurrent.Executors

class SearchSuggestionsProvider : ContentProvider() {
    private val webService by lazy {
        RedditApi.create()
    }
    private var lastRequest: Call<AutoCompleteRoot>? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Not yet implemented!")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented!")
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        throw UnsupportedOperationException("Not yet implemented!")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val query = URLDecoder.decode(uri.lastPathSegment, "UTF-8").toLowerCase()

        lastRequest?.cancel()

        val columns = arrayOf(
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_ICON_1,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
            SearchManager.SUGGEST_COLUMN_QUERY
        )
        val cursor = MatrixCursor(columns, 13)

        if (query != "search_suggest_query") {
            cursor.addRow(arrayOf(0, query, "Search Reddit", null, Intent.ACTION_SEARCH, null, "a", query))
            cursor.addRow(arrayOf(1, query, "Search Current Subreddit", null, Intent.ACTION_SEARCH, null, "b", query))
            cursor.addRow(arrayOf(2, query, "Search Subreddits", null, Intent.ACTION_SEARCH, null, "c", query))

            lastRequest = webService.subredditAutoComplete(query)

            (lastRequest as Call<AutoCompleteRoot>).execute().body()
                ?.subreddits?.forEachIndexed { index, autoCompleteEntry ->
                cursor.addRow(
                    arrayOf(
                        index + 3,
                        autoCompleteEntry.name,
                        null,
                        null,
                        null,
                        autoCompleteEntry.name,
                        null,
                        null
                    )
                )
            }
        } else {
            SearchRepository(Executors.newSingleThreadExecutor(), RedditDatabase.getDatabase(context!!))
                .getMostRecentSearches().forEach { autoCompleteEntry ->
                    Log.e("Entry", "$autoCompleteEntry")
                    cursor.addRow(
                        arrayOf(
                            autoCompleteEntry._id,
                            autoCompleteEntry.name,
                            autoCompleteEntry.text,
                            autoCompleteEntry.icon,
                            autoCompleteEntry.intent_action,
                            autoCompleteEntry.intent_data,
                            autoCompleteEntry.intent_extra_data,
                            autoCompleteEntry.query
                        )
                    )
                }
        }
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not yet implemented!")
    }
}
