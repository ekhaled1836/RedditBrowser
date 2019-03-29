package me.ekhaled1836.tp.reddit.ui.posts.viewholder

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Post

class SelfViewHolder(view: View) : BaseViewHolder(view) {
    private val text_self: AppCompatTextView = view.findViewById(R.id.view_post_text_self)

    companion object {
        fun create(parent: ViewGroup): SelfViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_post_fling_self, parent, false)
            return SelfViewHolder(view)
        }
    }

    override fun changePost(post: Post?) {
        super.changePost(post)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val spanned =
                Html.fromHtml(post?.selftext_html ?: text_self.context.resources.getString(R.string.post_loading_self))
            if (spanned.length > 256) {
                text_self.setTextFuture(
                    PrecomputedTextCompat.getTextFuture(
                        spanned.substring(0, 256) + text_self.context.resources.getString(R.string.post_more),
                        TextViewCompat.getTextMetricsParams(text_self),
                        null
                    )
                )
            } else {
                text_self.setTextFuture(
                    PrecomputedTextCompat.getTextFuture(
                        spanned,
                        TextViewCompat.getTextMetricsParams(text_self),
                        null
                    )
                )
            }
        } else {
            val spanned = Html.fromHtml(
                post?.selftext_html ?: text_self.context.resources.getString(R.string.post_loading_self),
                Html.FROM_HTML_MODE_COMPACT
            )
            if (spanned.length > 256) {
                text_self.setTextFuture(
                    PrecomputedTextCompat.getTextFuture(
                        spanned.substring(0, 256) + text_self.context.resources.getString(R.string.post_more),
                        TextViewCompat.getTextMetricsParams(text_self),
                        null
                    )
                )
            } else {
                text_self.setTextFuture(
                    PrecomputedTextCompat.getTextFuture(
                        spanned,
                        TextViewCompat.getTextMetricsParams(text_self),
                        null
                    )
                )
            }
        }
    }
}