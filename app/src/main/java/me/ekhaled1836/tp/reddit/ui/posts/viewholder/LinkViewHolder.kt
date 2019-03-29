package me.ekhaled1836.tp.reddit.ui.posts.viewholder

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import me.ekhaled1836.tp.reddit.GlideRequests
import me.ekhaled1836.tp.reddit.ui.posts.PostsActivity
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Post
import kotlin.math.roundToInt

class LinkViewHolder(view: View, private val glide: GlideRequests) : BaseViewHolder(view) {
    private val image_thumbnail: AppCompatImageView = view.findViewById(R.id.view_post_image_thumbnail)
    private val progress_buffering: ProgressBar = view.findViewById(R.id.view_post_progress_buffering)

    init {

    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): LinkViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_post_fling_link, parent, false)
            return LinkViewHolder(view, glide)
        }
    }

    override fun changePost(post: Post?) {
        super.changePost(post)

        progress_buffering.visibility = View.VISIBLE
        image_thumbnail.layoutParams.height = (PostsActivity.POST_WIDTH * (post?.scale ?: 9F/16F)).roundToInt()
        if (!post?.images_urls.isNullOrEmpty()) {
            glide.load(post!!.images_urls[post.images_urls.size - 1]).addListener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress_buffering.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress_buffering.visibility = View.GONE
                    return false
                }
            }).into(image_thumbnail)
        } else {
            glide.clear(image_thumbnail)
        }
    }
}