package me.ekhaled1836.tp.reddit.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import me.ekhaled1836.tp.reddit.api.RedditApi
import me.ekhaled1836.tp.reddit.model.PostsListing
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.util.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class PostsBoundaryCallback(
    private val subreddit: String,
    private val sort: String,
    private val period: String?,
    private val webservice: RedditApi,
    private val handleResponse: (String, String, String?, PostsListing?) -> Unit,
    private val ioExecutor: Executor,
    private val networkPageSize: Int
) : PagedList.BoundaryCallback<Post>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getPosts(
                subreddit = subreddit,
                sortedby = sort,
                period = period,
                query = null,
                limit = networkPageSize,
                after = null
            )
                .enqueue(createWebserviceCallback(it))
        }
    }

    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.getPosts(
                subreddit = subreddit,
                sortedby = sort,
                period = period,
                query = null,
                limit = networkPageSize,
                after = itemAtEnd.name
            )
                .enqueue(createWebserviceCallback(it))
        }
    }

    private fun insertItemsIntoDb(
        response: Response<PostsListing>,
        it: PagingRequestHelper.Request.Callback
    ) {
        ioExecutor.execute {
            handleResponse(subreddit, sort, period, response.body())
            it.recordSuccess()
        }
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<PostsListing> {
        return object : Callback<PostsListing> {
            override fun onFailure(
                call: Call<PostsListing>,
                t: Throwable
            ) {
                it.recordFailure(t)
            }

            override fun onResponse(
                call: Call<PostsListing>,
                response: Response<PostsListing>
            ) {
                insertItemsIntoDb(response, it)
            }
        }
    }
}