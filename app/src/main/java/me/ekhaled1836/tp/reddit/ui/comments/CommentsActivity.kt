package me.ekhaled1836.tp.reddit.ui.comments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activty_comments.*
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.ServiceLocator
import me.ekhaled1836.tp.reddit.model.Comment
import me.ekhaled1836.tp.reddit.model.NetworkState

class CommentsActivity : AppCompatActivity() {
    private var listLayoutState: Parcelable? = null

    companion object {
        private const val KEY_SUBREDDIT = "subreddit"
        private const val KEY_PARENT_ID = "parent_id"
        private const val KEY_LIST_LAYOUT_STATE = "list_layout_state"

        const val DEFAULT_SUBREDDIT = "AskReddit"
        const val DEFAULT_PARENT_ID = "aocyrl"
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repo = ServiceLocator.instance(this@CommentsActivity)
                    .getCommentsRepository()
                @Suppress("UNCHECKED_CAST")
                return CommentsViewModel(repo) as T
            }
        })[CommentsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_comments)

        setupListAdapter()

        viewModel.refreshState.observe(this, Observer {
            swipeRefresh_comments.isRefreshing = it == NetworkState.LOADING
        })
        swipeRefresh_comments.setOnRefreshListener {
            viewModel.refresh()
        }

        val subreddit = savedInstanceState?.getString(KEY_SUBREDDIT) ?: DEFAULT_SUBREDDIT
        val parentId = savedInstanceState?.getString(KEY_PARENT_ID) ?: DEFAULT_PARENT_ID

        if (savedInstanceState != null) {
            listLayoutState = savedInstanceState.getParcelable(KEY_LIST_LAYOUT_STATE)
        }

        viewModel.showComments(subreddit, parentId)

        bottomBar_comments.inflateMenu(R.menu.posts_bottom)
    }

    private fun setupListAdapter() {
        val adapter =
            CommentsAdapter {
                viewModel.retry()
            }
        list_comments.adapter = adapter

        viewModel.comments.observe(this, Observer<List<Comment>> {
            Log.e("UI", "${it.size}")
            adapter.submitList(it)
        })
        viewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

        list_comments.layoutManager?.onRestoreInstanceState(listLayoutState)

        list_comments.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    fab_comments.hide()
                } else {
                    fab_comments.show()
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SUBREDDIT, viewModel.currentSubreddit())
        outState.putString(KEY_PARENT_ID, viewModel.currentParentId())
        outState.putParcelable(KEY_LIST_LAYOUT_STATE, list_comments.layoutManager?.onSaveInstanceState())
    }
}
