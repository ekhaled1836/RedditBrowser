package me.ekhaled1836.tp.reddit.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.ekhaled1836.tp.reddit.model.AutoCompleteEntry

@Dao
interface SearchesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearch(search: AutoCompleteEntry)

    @Query("SELECT * FROM searches LIMIT 10")
    fun getMostRecentSearches(): List<AutoCompleteEntry>

    @Query("DELETE FROM searches")
    fun deleteMostRecentSearches()
}