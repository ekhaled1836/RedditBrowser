package me.ekhaled1836.tp.reddit.model

import android.app.SearchManager
import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class AutoCompleteRoot(@SerializedName("subreddits") val subreddits: List<AutoCompleteEntry>)

@Entity(tableName = "searches")
data class AutoCompleteEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = BaseColumns._ID) val _id: Int,
    @SerializedName("name") @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = SearchManager.SUGGEST_COLUMN_TEXT_2) val text: String? = null,
    @ColumnInfo(name = SearchManager.SUGGEST_COLUMN_ICON_1) val icon: String? = null,
    @ColumnInfo(name = SearchManager.SUGGEST_COLUMN_INTENT_ACTION) val intent_action: String? = null,
    @ColumnInfo(name = SearchManager.SUGGEST_COLUMN_INTENT_DATA) val intent_data: String? = null,
    @ColumnInfo(name = SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA) val intent_extra_data: String? = null,
    @ColumnInfo(name = SearchManager.SUGGEST_COLUMN_QUERY) val query: String? = null
)