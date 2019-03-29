package me.ekhaled1836.tp.reddit.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import me.ekhaled1836.tp.reddit.api.RedditApi
import me.ekhaled1836.tp.reddit.db.RedditDatabase
import me.ekhaled1836.tp.reddit.model.NetworkState
import me.ekhaled1836.tp.reddit.model.PagedListing
import me.ekhaled1836.tp.reddit.model.PostsListing
import me.ekhaled1836.tp.reddit.model.Post
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executor

class PostsRepository(
    val db: RedditDatabase,
    private val redditApi: RedditApi,
    private val ioExecutor: Executor,
    private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE
) {
    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 25
    }

    private fun insertResultIntoDb(subreddit: String, sort: String, period: String?, body: PostsListing?) {
        body!!.data.children.let { posts ->
            db.runInTransaction {
                val start = if (period == null) db.getPostsDAO().getNextIndexInSubreddit(subreddit, sort)
                else db.getPostsDAO().getNextIndexInSubreddit(subreddit, sort, period)
                val items = posts.mapIndexed { index, child ->
                    child.data.indexInResponse = start + index
                    child.data.sort = sort
                    child.data.period = period
                    preprocessPost(child.data)
                }
                db.getPostsDAO().insert(items)
            }
        }
    }

    private fun preprocessPost(post: Post): Post {
        post.images_urls = LinkedList()
        post.video_urls = LinkedList()
        if (!post.selftext_html.isNullOrBlank()) {
            if (!post.preview?.images.isNullOrEmpty()) {
                if (!post.preview!!.images[0].resolutions.isNullOrEmpty()) { // TODO: If you abandon SSIV make sure to compare POST_WIDTH to the images width before loading it, to avoid loading big images.
                    post.preview.images[0].resolutions!!.forEach { redditMediaSource ->
                        post.images_urls.add(redditMediaSource.url)
                    }
                }
                post.images_urls.add(post.preview.images[0].source.url)
                post.scale = post.preview.images[0].source.height.toFloat() /
                        post.preview.images[0].source.width.toFloat()
                post.type = 'a'
            } else {
                val doc = Jsoup.parseBodyFragment(post.selftext_html)
                val links = doc.getElementsByTag("a")
                links.forEach { link ->
                    val linkHref = link.attr("href")
                    if (linkHref.endsWith(".jpg") || linkHref.endsWith(".png") || linkHref.endsWith(".jpeg")) {
                        post.images_urls.add(linkHref)
                    }
                }
                if (!post.images_urls.isEmpty()) post.type = 'b' else post.type = 'c'
            }
        } else if (post.is_video) {
            if (post.secure_media?.reddit_video != null) {
                post.video_urls.add(post.secure_media.reddit_video.scrubber_media_url)
                post.video_urls.add(post.secure_media.reddit_video.dash_url.dropLast(16) + "DASH_1_2_M")
                post.video_urls.add(post.secure_media.reddit_video.fallback_url)
            }
            if (!post.preview?.images.isNullOrEmpty()) {
                if (!post.preview!!.images[0].resolutions.isNullOrEmpty()) { // TODO: If you discard SSIV make sure to compare POST_WIDTH to the images width before loading it.
                    post.preview.images[0].resolutions!!.forEach { redditMediaSource ->
                        post.images_urls.add(redditMediaSource.url)
                    }
                }
                post.images_urls.add(post.preview.images[0].source.url)
                post.scale = post.preview.images[0].source.height.toFloat() /
                        post.preview.images[0].source.width.toFloat()
            }
            post.type = 'd'
        } else if (post.url.startsWith("https://gfycat.com")) {
            post.video_urls.add("https://thumbs." + post.url.drop(8) + "-mobile.mp4")
            post.video_urls.add("https://giant." + post.url.drop(8) + ".webm")
            if (!post.preview?.images.isNullOrEmpty()) {
                if (!post.preview!!.images[0].resolutions.isNullOrEmpty()) { // TODO: If you discard SSIV make sure to compare POST_WIDTH to the images width before loading it.
                    post.preview.images[0].resolutions!!.forEach { redditMediaSource ->
                        post.images_urls.add(redditMediaSource.url)
                    }
                }
                post.images_urls.add(post.preview.images[0].source.url)
                post.scale = post.preview.images[0].source.height.toFloat() /
                        post.preview.images[0].source.width.toFloat()
            }
            post.type = 'd'
        } else if (post.url.startsWith("https://i.imgur.com") && post.url.endsWith(".gifv")) {
            post.video_urls.add(post.url.dropLast(4) + "mp4")
            if (!post.preview?.images.isNullOrEmpty()) {
                if (!post.preview!!.images[0].resolutions.isNullOrEmpty()) { // TODO: If you discard SSIV make sure to compare POST_WIDTH to the images width before loading it.
                    post.preview.images[0].resolutions!!.forEach { redditMediaSource ->
                        post.images_urls.add(redditMediaSource.url)
                    }
                }
                post.images_urls.add(post.preview.images[0].source.url)
                post.scale = post.preview.images[0].source.height.toFloat() /
                        post.preview.images[0].source.width.toFloat()
            }
            post.type = 'd'
        } else if (post.url.endsWith(".mp4") || post.url.endsWith(".webm")) {
            post.video_urls.add(post.url)
            if (!post.preview?.images.isNullOrEmpty()) {
                if (!post.preview!!.images[0].resolutions.isNullOrEmpty()) { // TODO: If you discard SSIV make sure to compare POST_WIDTH to the images width before loading it.
                    post.preview.images[0].resolutions!!.forEach { redditMediaSource ->
                        post.images_urls.add(redditMediaSource.url)
                    }
                }
                post.images_urls.add(post.preview.images[0].source.url)
                post.scale = post.preview.images[0].source.height.toFloat() /
                        post.preview.images[0].source.width.toFloat()
            }
            post.type = 'd'
        } else if (post.url.startsWith("https://imgur.com/a/")) {
            post.type = 'e'
        } else if (post.url.startsWith("https://imgur.com/gallery/")) {
            post.type = 'f'
        } else if (post.url.startsWith("https://imgur.com/t/")) {
            post.type = 'g'
        } else if (!post.preview?.images.isNullOrEmpty()) {
            if (!post.preview!!.images[0].resolutions.isNullOrEmpty()) { // TODO: If you discard SSIV make sure to compare POST_WIDTH to the images width before loading it.
                post.preview.images[0].resolutions!!.forEach { redditMediaSource ->
                    post.images_urls.add(redditMediaSource.url)
                }
            }
            post.images_urls.add(post.preview.images[0].source.url)
            post.scale = post.preview.images[0].source.height.toFloat() / post.preview.images[0].source.width.toFloat()
            post.type = 'h'
        } else {
            post.type = 'i'
        }
        return post
    }

    @MainThread
    private fun refresh(subreddit: String, sort: String, period: String?): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        redditApi.getPosts(subreddit, sort, period, null, networkPageSize, null)
            .enqueue(
                object : Callback<PostsListing> {
                    override fun onFailure(call: Call<PostsListing>, t: Throwable) {
                        // retrofit calls this on main thread so safe to call set value
                        networkState.value = NetworkState.error(t.message)
                    }

                    override fun onResponse(
                        call: Call<PostsListing>,
                        response: Response<PostsListing>
                    ) {
                        ioExecutor.execute {
                            db.runInTransaction {
                                if (period == null)
                                    db.getPostsDAO().deletePosts(subreddit, sort)
                                else
                                    db.getPostsDAO().deletePosts(subreddit, sort, period)
                                insertResultIntoDb(subreddit, sort, period, response.body())
                            }
                            // since we are in bg thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)
                        }
                    }
                }
            )
        return networkState
    }

    @MainThread
    fun getPosts(subreddit: String, sort: String, period: String?, pageSize: Int): PagedListing<Post> {
        // getDatabase a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = PostsBoundaryCallback(
            webservice = redditApi,
            subreddit = subreddit,
            sort = sort,
            period = period,
            handleResponse = this::insertResultIntoDb,
            ioExecutor = ioExecutor,
            networkPageSize = networkPageSize
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(subreddit, sort, period)
        }

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = if (period == null)
            db.getPostsDAO().getPosts(subreddit, sort).toLiveData(
                pageSize = pageSize,
                boundaryCallback = boundaryCallback
            )
        else
            db.getPostsDAO().getPosts(subreddit, sort, period).toLiveData(
                pageSize = pageSize,
                boundaryCallback = boundaryCallback
            )


        return PagedListing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            retry = {
                boundaryCallback.helper.retryAllFailed()
            },
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}

