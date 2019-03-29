package me.ekhaled1836.tp.reddit.ui.comments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import me.ekhaled1836.tp.reddit.repository.CommentsRepository
import me.ekhaled1836.tp.reddit.ui.comments.CommentsActivity.Companion.DEFAULT_PARENT_ID
import me.ekhaled1836.tp.reddit.ui.comments.CommentsActivity.Companion.DEFAULT_SUBREDDIT

class CommentsViewModel(private val commentsRepository: CommentsRepository) : ViewModel() {
    private val subreddit = MutableLiveData<String>()
    private val parentId = MutableLiveData<String>()
    private val repoResult = map(subreddit) {
        commentsRepository.getComments(it, currentParentId())
    }
    val comments = switchMap(repoResult) { Log.e("repoResultChanged", it.streamedList.value.toString()); it.streamedList }!!
    val networkState = switchMap(repoResult) { it.networkState }!!
    val refreshState = switchMap(repoResult) { it.refreshState }!!

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun showComments(subreddit: String, parent_id: String) {
        this.parentId.value = parent_id
        this.subreddit.value = subreddit
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun currentSubreddit(): String = subreddit.value ?: DEFAULT_SUBREDDIT
    fun currentParentId(): String = parentId.value ?: DEFAULT_PARENT_ID
}
