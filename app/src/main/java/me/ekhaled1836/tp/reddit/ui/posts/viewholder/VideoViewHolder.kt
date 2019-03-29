package me.ekhaled1836.tp.reddit.ui.posts.viewholder

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import me.ekhaled1836.tp.reddit.GlideRequests
import me.ekhaled1836.tp.reddit.ui.posts.PostsActivity
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.util.ExoPlayerExtractorsFactory
import me.ekhaled1836.tp.reddit.util.ExoPlayerRenderersFactory
import kotlin.math.roundToInt


class VideoViewHolder(
    view: View,
    lifecycle: Lifecycle,
    private val glide: GlideRequests,
    private val cache: SimpleCache
) :
    BaseViewHolder(view), LifecycleObserver {
    private val surface_video: TextureView = view.findViewById(R.id.view_post_surface_video)
    private val image_thumbnail: AppCompatImageView = view.findViewById(R.id.view_post_image_thumbnail)
    private val progress_buffering: ProgressBar = view.findViewById(R.id.view_post_progress_buffering)

    var hasFocus: Boolean = false
        set(value) {
            field = value
            if (value)
                play()
            else
                stop(pause = true)
        }

    private var player: SimpleExoPlayer? = null

    private var autoPlay: Boolean = true
    private var startWindow: Int = C.INDEX_UNSET
    private var startPosition: Long = C.TIME_UNSET

    init {
        lifecycle.addObserver(this)
    }

    companion object {
        fun create(parent: ViewGroup, lifecycle: Lifecycle, glide: GlideRequests, cache: SimpleCache): VideoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_post_fling_video, parent, false)
            return VideoViewHolder(view, lifecycle, glide, cache)
        }
    }

    private val listener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//            super.onPlayerStateChanged(playWhenReady, playbackState)
            when (playbackState) {
//                Player.STATE_IDLE -> Log.d("PlayerState", "IDLE")
                Player.STATE_BUFFERING -> progress_buffering.visibility = View.VISIBLE
                Player.STATE_READY -> {
                    glide.clear(image_thumbnail)
                    image_thumbnail.visibility = View.GONE
                    progress_buffering.visibility = View.GONE
                }
//                Player.STATE_ENDED -> Log.d("PlayerState", "ENDED")
            }
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
//            super.onTracksChanged(trackGroups, trackSelections)
        }
    }

    private fun play() {
//        if (Util.checkCleartextTrafficPermitted(videoUrl))

        progress_buffering.visibility = View.VISIBLE

        player = ExoPlayerFactory.newSimpleInstance(
            surface_video.context, ExoPlayerRenderersFactory(surface_video.context), DefaultTrackSelector()
        )

        player?.playWhenReady = autoPlay
        player?.repeatMode = Player.REPEAT_MODE_ONE
        if (startWindow != C.INDEX_UNSET)
            player?.seekTo(startWindow, startPosition)

        player?.setVideoTextureView(surface_video)


        val cacheDataSourceFactory = CacheDataSourceFactory(
            cache,
            DefaultDataSourceFactory(
                surface_video.context,
                DefaultHttpDataSourceFactory(Util.getUserAgent(surface_video.context, "Reddit"))
            )
        )
        val mediaSource =
            ExtractorMediaSource.Factory(cacheDataSourceFactory).setExtractorsFactory(ExoPlayerExtractorsFactory())
                .createMediaSource(Uri.parse(post!!.video_urls[post!!.video_urls.size - 1]))

        player?.prepare(mediaSource, startWindow == C.INDEX_UNSET, false)

        player?.addListener(listener)
    }

    private fun stop(pause: Boolean) {
        if (player != null) {
            if (pause)
                updateVideoPlayerState()
            else
                clearVideoPlayerState()
            player!!.removeListener(listener)
//            player!!.clearVideoSurface()
            player!!.release()
            player = null
//            progress_buffering.visibility = View.GONE
        }
    }

    override fun changePost(post: Post?) {
        super.changePost(post)
        stop(pause = false)

        image_thumbnail.layoutParams.height = (PostsActivity.POST_WIDTH * (post?.scale ?: 9f / 16f)).roundToInt()
        surface_video.layoutParams.height = (PostsActivity.POST_WIDTH * (post?.scale ?: 9f / 16f)).roundToInt()

        if (!post?.images_urls.isNullOrEmpty()) {
            glide.load(post!!.images_urls[post.images_urls.size - 1]).into(image_thumbnail)
        } else {
            glide.clear(image_thumbnail)
        }
    }

    fun onDetach() {
        if (hasFocus) {
            stop(pause = true)
        }
    }

    fun onAttach() {
        if (hasFocus) {
            play()
        } else {
            if (startWindow != C.INDEX_UNSET) {
                image_thumbnail.visibility = View.VISIBLE
                if (!post?.images_urls.isNullOrEmpty()) {
                    glide.load(post!!.images_urls[post!!.images_urls.size - 1]).into(image_thumbnail)
                }
            }
        }
    }

    private fun updateVideoPlayerState() {
        if (player != null) {
            autoPlay = player!!.playWhenReady
            startWindow = player!!.currentWindowIndex
            startPosition = Math.max(0, player!!.contentPosition)
        }
    }

    private fun clearVideoPlayerState() {
        autoPlay = true
        startWindow = C.INDEX_UNSET
        startPosition = C.TIME_UNSET
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun activityStart() {
        if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M) && hasFocus) {
            play()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun activityResume() {
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M || player == null) && hasFocus) {
            play()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun activityPause() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            stop(pause = true)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun activityStop() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            stop(pause = true)
        }
    }
}