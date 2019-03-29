package me.ekhaled1836.tp.reddit.ui.posts

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activty_posts.*
import me.ekhaled1836.tp.reddit.GlideApp
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.ServiceLocator
import me.ekhaled1836.tp.reddit.db.RedditDatabase
import me.ekhaled1836.tp.reddit.model.AutoCompleteEntry
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.repository.SearchRepository
import me.ekhaled1836.tp.reddit.model.NetworkState
import me.ekhaled1836.tp.reddit.ui.posts.viewholder.VideoViewHolder
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class PostsActivity : AppCompatActivity() {
    private var listLayoutState: Parcelable? = null

    companion object {
        private const val KEY_SUBREDDIT = "subreddit"
        private const val KEY_SORT = "sort"
        private const val KEY_PERIOD = "period"
        private const val KEY_LIST_LAYOUT_STATE = "list_layout_state"

        const val DEFAULT_SUBREDDIT = "gifs"
        const val DEFAULT_SORT = "hot"

        var POST_WIDTH: Int = // TODO: Make non-static and change onConfigurationChange
            (Resources.getSystem().displayMetrics.widthPixels - (20f * Resources.getSystem().displayMetrics.density)).roundToInt()
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@PostsActivity)
                    .getPostsRepository()
                @Suppress("UNCHECKED_CAST")
                return PostsViewModel(application, repo) as T
            }
        })[PostsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_posts)

        setupPostsListAdapter()

        viewModel.refreshState.observe(this, Observer {
            swipeRefresh_posts.isRefreshing = it == NetworkState.LOADING
        })
        swipeRefresh_posts.setOnRefreshListener {
            viewModel.refresh()
        }

        val subreddit = savedInstanceState?.getString(KEY_SUBREDDIT) ?: DEFAULT_SUBREDDIT
        val sort = savedInstanceState?.getString(KEY_SORT) ?: DEFAULT_SORT
        val period = savedInstanceState?.getString(KEY_PERIOD)

        if (savedInstanceState != null) {
            listLayoutState = savedInstanceState.getParcelable(KEY_LIST_LAYOUT_STATE)
        }

        when(intent?.action) {
            Intent.ACTION_SEARCH -> {
//                intent.getStringExtra(SearchManager.QUERY)?.also { query ->
//                    viewModel.changePosts(query, sort, period)
//                }
            }
            Intent.ACTION_VIEW -> {
//                viewModel.changePosts(intent.dataString ?: DEFAULT_SUBREDDIT, sort, period)
            }
            else -> {
                viewModel.changePosts(subreddit, sort, period)
            }
        }

        bottomBar_posts.inflateMenu(R.menu.posts_bottom)
        val bottomMenu = bottomBar_posts.menu

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (bottomMenu.findItem(R.id.menu_posts_search).actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        when(intent?.action) {
            Intent.ACTION_SEARCH -> {
                intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                    when(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY)) {
                        "a" -> {}
                        "b" -> {}
                        "c" -> {}
                    }
                }
            }
            Intent.ACTION_VIEW -> {
                viewModel.changePosts(intent.dataString ?: DEFAULT_SUBREDDIT,
                    DEFAULT_SORT, null)
                SearchRepository(Executors.newSingleThreadExecutor(), RedditDatabase.getDatabase(this)).insertResultIntoDb(AutoCompleteEntry(0, name = intent.dataString ?: ""))
            }
        }
    }

    private fun setupPostsListAdapter() {
        val glide = GlideApp.with(this)

        val adapter =
            PostsAdapter(glide, lifecycle, viewModel.videoCache) {
                viewModel.retry()
            }
        list_posts.adapter = adapter

        viewModel.posts.observe(this, Observer<PagedList<Post>> {
            adapter.submitList(it)
        })
        viewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

        list_posts.layoutManager?.onRestoreInstanceState(listLayoutState)

//        val listLayoutManager: LinearLayoutManager = object : LinearLayoutManager(activity, VERTICAL, false) {
//            override fun getExtraLayoutSpace(state: RecyclerView.State?): Int {
//                return Resources.getSystem().displayMetrics.heightPixels
//            }
//        }

        list_posts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var currentFocusedItem = -1
            private var oldViewHolder: VideoViewHolder? = null

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                bottomBar_posts.translationY = MathUtils.clamp(bottomBar_posts.translationY + dy.div(1f), 0f, bottomBar_posts.measuredHeight.toFloat())
//                fab_posts.alpha = 1f - (bottomBar_posts.translationY.div(bottomBar_posts.measuredHeight))

                if (dy > 0) {
                    fab_posts.hide()
                } else {
                    fab_posts.show()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    var focusedItem =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                    if (focusedItem == RecyclerView.NO_POSITION) {
//                        if (view.getGlobalVisibleRect(outRect) && outRect.bottom > 0 && ((outRect.height().toFloat() / view.height.toFloat()) > itsRatio)) {
                        focusedItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    }

                    if (focusedItem != currentFocusedItem) {
//                        val oldHolder =
//                            recyclerView.findViewHolderForAdapterPosition(currentFocusedItem) as VideoViewHolder?
                        oldViewHolder?.hasFocus = false

                        val viewHolder = recyclerView.findViewHolderForAdapterPosition(focusedItem)

                        //::class == ::class
                        if (viewHolder is VideoViewHolder) {

                            viewHolder.hasFocus = true

                            currentFocusedItem = focusedItem
                            oldViewHolder = viewHolder
                        }
                    }
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SUBREDDIT, viewModel.currentSubreddit())
        outState.putString(KEY_SORT, viewModel.currentSort())
        outState.putString(KEY_PERIOD, viewModel.currentPeriod())
        outState.putParcelable(KEY_LIST_LAYOUT_STATE, list_posts.layoutManager?.onSaveInstanceState())
    }

//    public override fun onNewIntent(intent: Intent) {
//        pauseVideo()
//        clearVideoPlayerState()
//        setIntent(intent)
//    }

    private fun changePosts(
        subreddit: String = viewModel.currentSubreddit(),
        sort: String = viewModel.currentSort(),
        period: String? = viewModel.currentPeriod()
    ) {
        if (viewModel.changePosts(subreddit, sort, period)) {
            list_posts.scrollToPosition(0)
            (list_posts.adapter as? PostsAdapter)?.submitList(null)
        }
    }

//    private fun initSearch() {
//        input.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_GO) {
//                updatedSubredditFromInput()
//                true
//            } else {
//                false
//            }
//        }
//        input.setOnKeyListener { _, keyCode, event ->
//            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                updatedSubredditFromInput()
//                true
//            } else {
//                false
//            }
//        }
//    }
//
//    private fun updatedSubredditFromInput() {
//        input.text.trim().toString().let {
//            if (it.isNotEmpty()) {
//                if (viewModel.changePosts(it)) {
//                    list.scrollToPosition(0)
//                    (list.adapter as? PostsAdapter)?.submitList(null)
//                }
//            }
//        }
//    }
}
