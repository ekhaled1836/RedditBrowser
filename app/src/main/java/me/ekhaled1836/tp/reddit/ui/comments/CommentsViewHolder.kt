package me.ekhaled1836.tp.reddit.ui.comments

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Comment
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.util.PostUtils

class CommentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val flair_user: AppCompatTextView = view.findViewById(R.id.view_comment_flair_user)
    private val text_user: AppCompatTextView = view.findViewById(R.id.view_comment_text_user)
    private val text_subtitle: AppCompatTextView = view.findViewById(R.id.view_comment_text_subtitle)
    private val text_score: AppCompatTextView = view.findViewById(R.id.view_comment_text_score)
    private val text: AppCompatTextView = view.findViewById(R.id.view_comment_text)
    private var comment: Comment? = null

    fun changeComment(comment: Comment?) {
        this.comment = comment

        if (comment?.distinguished == "moderator")
            text_user.setTextColor(ContextCompat.getColor(text_user.context, R.color.text_title_moderator))
        else
            text_user.setTextColor(ContextCompat.getColor(text_user.context, R.color.text_title_normal))

        if (!comment?.author_flair_text.isNullOrBlank()) {
            flair_user.visibility = View.VISIBLE
            flair_user.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    comment!!.author_flair_text!!, TextViewCompat.getTextMetricsParams(flair_user), null
                )
            )
        } else {
            flair_user.visibility = View.GONE
        }

        text_user.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text_user.context.resources.getString(
                    R.string.post_user,
                    comment?.author ?: text_user.context.resources.getString(R.string.post_loading_author)
                ),
                TextViewCompat.getTextMetricsParams(text_user), null
            )
        )

        text_subtitle.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text_subtitle.context.resources.getString(
                    R.string.post_subtitle,
                    PostUtils.getTime(comment?.created_utc ?: System.currentTimeMillis())
                ),
                TextViewCompat.getTextMetricsParams(text_subtitle), null
            )
        )

        text_score.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                PostUtils.getShortNumber(comment?.score ?: 0), TextViewCompat.getTextMetricsParams(text_score), null
            )
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val spanned =
                Html.fromHtml(comment?.body_html ?: text.context.resources.getString(R.string.post_loading_self))
            text.setTextFuture(
                    PrecomputedTextCompat.getTextFuture(
                        spanned,
                        TextViewCompat.getTextMetricsParams(text),
                        null
                    )
                )
        } else {
            val spanned = Html.fromHtml(
                comment?.body_html ?: text.context.resources.getString(R.string.post_loading_self),
                Html.FROM_HTML_MODE_COMPACT
            )
            text.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    spanned,
                    TextViewCompat.getTextMetricsParams(text),
                    null
                )
            )
        }
    }

    fun updateScore(item: Comment?) {
        comment = item
        text_score.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                "${comment?.score ?: 0}", TextViewCompat.getTextMetricsParams(text_score), null
            )
        )
    }

    companion object {
        fun create(parent: ViewGroup): CommentsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_comment, parent, false)
            return CommentsViewHolder(view)
        }
    }
}