package me.ekhaled1836.tp.reddit.ui.posts.viewholder

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import me.ekhaled1836.tp.reddit.GlideRequests
import me.ekhaled1836.tp.reddit.ui.posts.PostsActivity.Companion.POST_WIDTH
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Post
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class ImageViewHolder(view: View, private val glide: GlideRequests) : BaseViewHolder(view) {
    private val image_thumbnail: SubsamplingScaleImageView = view.findViewById(R.id.view_post_image_thumbnail)
    private val progress_buffering: ProgressBar = view.findViewById(R.id.view_post_progress_buffering)

    init {
        image_thumbnail.setExecutor(Executors.newSingleThreadExecutor())
        image_thumbnail.setDoubleTapZoomDuration(200)
        image_thumbnail.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM)
        image_thumbnail.maxScale = 10f
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): ImageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_post_fling_image, parent, false)
            return ImageViewHolder(view, glide)
        }
    }

    private val target = object : CustomViewTarget<SubsamplingScaleImageView, File>(image_thumbnail) {
        override fun onResourceReady(resource: File, transition: Transition<in File>?) {
            image_thumbnail.setImage(ImageSource.uri(Uri.fromFile(resource)))
            progress_buffering.visibility = View.GONE
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            progress_buffering.visibility = View.GONE
        }

        override fun onResourceCleared(placeholder: Drawable?) {
            progress_buffering.visibility = View.GONE
            image_thumbnail.recycle()
        }
    }

    override fun changePost(post: Post?) {
        super.changePost(post)

        progress_buffering.visibility = View.VISIBLE
        image_thumbnail.layoutParams.height = (POST_WIDTH * (post?.scale ?: 9F / 16F)).roundToInt()
        if (post != null)
            glide.downloadOnly().load(post.images_urls[post.images_urls.size - 1]).into(target)
        else
            glide.clear(target)
    }
}