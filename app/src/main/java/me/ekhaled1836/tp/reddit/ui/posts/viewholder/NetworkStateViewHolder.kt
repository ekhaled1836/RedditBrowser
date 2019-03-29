package me.ekhaled1836.tp.reddit.ui.posts.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.NetworkState
import me.ekhaled1836.tp.reddit.model.Status

class NetworkStateViewHolder(view: View, private val retryCallback: () -> Unit): RecyclerView.ViewHolder(view) {
    private val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
    private val retry = view.findViewById<Button>(R.id.retry_button)
    private val errorMsg = view.findViewById<TextView>(R.id.error_msg)

    init {
        retry.setOnClickListener {
            retryCallback()
        }
    }
    fun bindTo(networkState: NetworkState?) {
        progressBar.visibility =
                toVisibility(networkState?.status == Status.RUNNING)
        retry.visibility =
                toVisibility(networkState?.status == Status.FAILED)
        errorMsg.visibility =
                toVisibility(networkState?.msg != null)
        errorMsg.text = networkState?.msg
    }

    companion object {
        fun create(parent: ViewGroup, retryCallback: () -> Unit): NetworkStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_post_fling_network, parent, false)
            return NetworkStateViewHolder(view, retryCallback)
        }

        fun toVisibility(constraint : Boolean): Int {
            return if (constraint) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}
