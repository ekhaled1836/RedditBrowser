package me.ekhaled1836.tp.reddit.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import me.ekhaled1836.tp.reddit.api.RedditApi
import me.ekhaled1836.tp.reddit.db.RedditDatabase
import me.ekhaled1836.tp.reddit.model.Comment
import me.ekhaled1836.tp.reddit.model.CommentsListing
import me.ekhaled1836.tp.reddit.model.NetworkState
import me.ekhaled1836.tp.reddit.model.StreamedListing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class CommentsRepository(
    val db: RedditDatabase,
    private val redditApi: RedditApi,
    private val ioExecutor: Executor
) {
//    private var isRequestInProgress = false

    private fun insertResultIntoDb(body: CommentsListing?) {
        body!!.data.children.dropLast(1).map {
            it.data as Comment
        }.also { comments ->
            db.getCommentsDAO().insertComments(comments)
        }
    }

    @MainThread
    private fun refresh(subreddit: String, parent_id: String): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        redditApi.getComments(subreddit, parent_id)
            .enqueue(
                object : Callback<List<CommentsListing>> {
                    override fun onFailure(call: Call<List<CommentsListing>>, t: Throwable) {
                        networkState.value = NetworkState.error(t.message)
                    }

                    override fun onResponse(
                        call: Call<List<CommentsListing>>,
                        response: Response<List<CommentsListing>>
                    ) {
                        ioExecutor.execute {
                            db.runInTransaction {
                                db.getCommentsDAO().deleteComments(subreddit, parent_id)
                                insertResultIntoDb(response.body()!![1])
                            }
                            networkState.postValue(NetworkState.LOADED)
                        }
                    }
                }
            )
        return networkState
    }

    @MainThread
    fun getComments(subreddit: String, parent_id: String): StreamedListing<List<Comment>> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        redditApi.getComments(
            subreddit = subreddit,
            parent_id = parent_id
        ).enqueue(object : Callback<List<CommentsListing>> {
            override fun onResponse(call: Call<List<CommentsListing>>, response: Response<List<CommentsListing>>) {
                ioExecutor.execute {
                    if(response.isSuccessful) {
                        insertResultIntoDb(response.body()!![1])
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        networkState.value = NetworkState.error(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<List<CommentsListing>>, t: Throwable) {
                networkState.value = NetworkState.error(t.message)
            }
        })

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(subreddit, parent_id)
        }

        val liveStreamedList = db.getCommentsDAO().getComments(subreddit, "t3_$parent_id")

        return StreamedListing(
            streamedList = liveStreamedList,
            networkState = networkState,
            retry = {
                getComments(subreddit, parent_id)
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}

