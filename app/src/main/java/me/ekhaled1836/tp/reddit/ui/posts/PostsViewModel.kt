package me.ekhaled1836.tp.reddit.ui.posts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import me.ekhaled1836.tp.reddit.ui.posts.PostsActivity.Companion.DEFAULT_SORT
import me.ekhaled1836.tp.reddit.ui.posts.PostsActivity.Companion.DEFAULT_SUBREDDIT
import me.ekhaled1836.tp.reddit.repository.PostsRepository

class PostsViewModel(app: Application, private val postsRepository: PostsRepository) : AndroidViewModel(app) {
    val videoCache by lazy {
        SimpleCache(app.cacheDir, LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024))
    }

    private val subreddit = MutableLiveData<String>()
    private val sort = MutableLiveData<String>()
    private val period = MutableLiveData<String?>()
    private val repoResult = map(subreddit) {
        postsRepository.getPosts(currentSubreddit(), currentSort(), currentPeriod(), 25)
    }
    //    private val repoResult = MediatorLiveData<PostsListingData<Post>>()
    val posts = switchMap(repoResult) { it.pagedList }!!
    val networkState = switchMap(repoResult) { it.networkState }!!
    val refreshState = switchMap(repoResult) { it.refreshState }!!

    /*init {
        repoResult.addSource(subreddit) { subreddit ->
            postsRepository.getPosts(subreddit, currentSort() ?: "hot", currentPeriod(), 25)
        }
        repoResult.addSource(sort) { sort ->
            postsRepository.getPosts(currentSubreddit(), sort, currentPeriod(), 25)
        }
        repoResult.addSource(period) { period ->
            postsRepository.getPosts(currentSubreddit(), currentSubreddit(), period, 25)
        }
    }*/

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun changePosts(subreddit: String, sort: String, period: String?): Boolean {
        if (this.subreddit.value == subreddit && this.sort.value == sort && this.period.value == period) {
            return false
        }
        this.period.value = period
        this.sort.value = sort
        this.subreddit.value = subreddit
        return true
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

    fun currentSubreddit(): String = subreddit.value ?: DEFAULT_SUBREDDIT
    fun currentSort(): String = sort.value ?: DEFAULT_SORT
    fun currentPeriod(): String? = period.value
}
