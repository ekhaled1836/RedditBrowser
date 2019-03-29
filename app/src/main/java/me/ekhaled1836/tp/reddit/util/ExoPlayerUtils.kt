package me.ekhaled1836.tp.reddit.util

import android.content.Context
import android.os.Handler
import com.google.android.exoplayer2.Renderer
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.extractor.Extractor
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.extractor.mkv.MatroskaExtractor
import com.google.android.exoplayer2.extractor.mp4.FragmentedMp4Extractor
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.metadata.MetadataOutput
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer
import com.google.android.exoplayer2.video.VideoRendererEventListener


// ExtractorMediaSource uses DefaultExtractorsFactory by default.
// DefaultExtractorsFactory depends on all of the Extractor implementations provided in the ExoPlayer library,
// and as a result none of them will be removed by ProGuard.
// We can specify our own ExtractorsFactory instead.
class ExoPlayerExtractorsFactory : ExtractorsFactory {
    override fun createExtractors(): Array<Extractor> {
        return arrayOf(Mp4Extractor(), MatroskaExtractor(), FragmentedMp4Extractor())
    }
}

// SimpleExoPlayer default player’s renderers will be created using DefaultRenderersFactory.
// DefaultRenderersFactory depends on all of the Renderer implementations provided in the ExoPlayer library,
// and as a result none of them will be removed by ProGuard.
// If you know that your app only needs a subset of renderers, we can specify your own RenderersFactory instead.
class ExoPlayerRenderersFactory(private val context: Context) : RenderersFactory {
    override fun createRenderers(
        eventHandler: Handler?,
        videoRendererEventListener: VideoRendererEventListener?,
        audioRendererEventListener: AudioRendererEventListener?,
        textRendererOutput: TextOutput?,
        metadataRendererOutput: MetadataOutput?,
        drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?
    ): Array<Renderer> {
        return arrayOf(
            /*MediaCodecVideoRenderer(
                context,
                MediaCodecSelector.DEFAULT_WITH_FALLBACK,
                5000,
                drmSessionManager,
                true,
                eventHandler,
                videoRendererEventListener,
                5
            )*/
            MediaCodecVideoRenderer(context, MediaCodecSelector.DEFAULT_WITH_FALLBACK)
        )
    }
}