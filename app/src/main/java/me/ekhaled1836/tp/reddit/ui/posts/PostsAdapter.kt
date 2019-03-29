package me.ekhaled1836.tp.reddit.ui.posts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import me.ekhaled1836.tp.reddit.GlideRequests
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.model.NetworkState
import me.ekhaled1836.tp.reddit.ui.posts.viewholder.*

class PostsAdapter(
    private val glide: GlideRequests,
    private val lifecycle: Lifecycle,
    private val videoCache: SimpleCache,
    private val retryCallback: () -> Unit
) : PagedListAdapter<Post, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (hasExtraRow() && position == itemCount - 1) {
            return R.layout.view_post_fling_network
        } else {
            return when (getItem(position)?.type) {
                'c' -> R.layout.view_post_fling_self
                'd' -> R.layout.view_post_fling_video
                'e' -> R.layout.view_post_fling_link
                'f' -> R.layout.view_post_fling_link
                'g' -> R.layout.view_post_fling_link
                'h' -> R.layout.view_post_fling_image
                'i' -> R.layout.view_post_fling_link
                else -> R.layout.view_post_fling_link
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.view_post_fling_link -> LinkViewHolder.create(parent, glide)
            R.layout.view_post_fling_video -> VideoViewHolder.create(parent, lifecycle, glide, videoCache)
            R.layout.view_post_fling_image -> ImageViewHolder.create(parent, glide)
            R.layout.view_post_fling_self -> SelfViewHolder.create(parent)
            R.layout.view_post_fling_network -> NetworkStateViewHolder.create(parent, retryCallback)
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            (holder as BaseViewHolder).updateScore(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.view_post_fling_link -> (holder as LinkViewHolder).changePost(getItem(position))
            R.layout.view_post_fling_video -> (holder as VideoViewHolder).changePost(getItem(position))
            R.layout.view_post_fling_image -> (holder as ImageViewHolder).changePost(getItem(position))
            R.layout.view_post_fling_self -> (holder as SelfViewHolder).changePost(getItem(position))
            R.layout.view_post_fling_network -> (holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if(holder is VideoViewHolder) holder.onDetach()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if(holder is VideoViewHolder) holder.onAttach()
    }

    // TODO: Ids: Convert every char to a Long.

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.name == newItem.name

            override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: Post, newItem: Post): Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return oldItem.copy(score = newItem.score) == newItem
        }
    }
}
