package me.ekhaled1836.tp.reddit.ui.posts.viewholder

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import me.ekhaled1836.tp.reddit.R
import me.ekhaled1836.tp.reddit.model.Post
import me.ekhaled1836.tp.reddit.util.PostUtils

abstract class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val text_title: AppCompatTextView = view.findViewById(R.id.view_post_text_title)
    private val flair_user: AppCompatTextView = view.findViewById(R.id.view_post_flair_user)
    private val text_user: AppCompatTextView = view.findViewById(R.id.view_post_text_user)
    private val text_subtitle: AppCompatTextView = view.findViewById(R.id.view_post_text_subtitle)
    private val text_subreddit: AppCompatTextView = view.findViewById(R.id.view_post_text_subreddit)
    private val flair_post: AppCompatTextView = view.findViewById(R.id.view_post_flair_post)
    private val flair_nsfw: View = view.findViewById(R.id.view_post_flair_nsfw)
    private val icon_silver: View = view.findViewById(R.id.view_post_icon_silver)
    private val text_silverCount: AppCompatTextView = view.findViewById(R.id.view_post_text_silverCount)
    private val icon_gold: View = view.findViewById(R.id.view_post_icon_gold)
    private val text_goldCount: AppCompatTextView = view.findViewById(R.id.view_post_text_goldCount)
    private val icon_platinum: View = view.findViewById(R.id.view_post_icon_platinum)
    private val text_platinumCount: AppCompatTextView = view.findViewById(R.id.view_post_text_platinumCount)
    private val flair_archived: View = view.findViewById(R.id.view_post_flair_archived)
    private val flair_locked: View = view.findViewById(R.id.view_post_flair_locked)
    private val flair_spoiler: View = view.findViewById(R.id.view_post_flair_spoiler)
    private val flair_stickied: View = view.findViewById(R.id.view_post_flair_stickied)
    private val button_more: AppCompatImageButton = view.findViewById(R.id.view_post_button_more)
    private val button_share: AppCompatImageButton = view.findViewById(R.id.view_post_button_share)
    private val button_save: AppCompatImageButton = view.findViewById(R.id.view_post_button_save)
    private val drawable_save = DrawableCompat.wrap(button_save.drawable).mutate()
    private val button_downvote: AppCompatImageButton = view.findViewById(R.id.view_post_button_downvote)
    private val drawable_downvote = DrawableCompat.wrap(button_downvote.drawable).mutate()
    private val button_upvote: AppCompatImageButton = view.findViewById(R.id.view_post_button_upvote)
    private val drawable_upvote = DrawableCompat.wrap(button_upvote.drawable).mutate()
    private val text_score: AppCompatTextView = view.findViewById(R.id.view_post_text_score)
    private val icon_score: AppCompatImageView = view.findViewById(R.id.view_post_icon_score)
    private val text_commentCount: AppCompatTextView = view.findViewById(R.id.view_post_text_commentCount)
    protected var post: Post? = null

    init {
        val drawable_more = DrawableCompat.wrap(button_more.drawable).mutate()
        DrawableCompat.setTint(drawable_more, ContextCompat.getColor(button_more.context, R.color.tint_post))
        val drawable_share = DrawableCompat.wrap(button_share.drawable).mutate()
        DrawableCompat.setTint(drawable_share, ContextCompat.getColor(button_share.context, R.color.tint_post))
        DrawableCompat.setTint(drawable_save, ContextCompat.getColor(button_save.context, R.color.tint_post))
        DrawableCompat.setTint(drawable_downvote, ContextCompat.getColor(button_downvote.context, R.color.tint_post))
        DrawableCompat.setTint(drawable_upvote, ContextCompat.getColor(button_upvote.context, R.color.tint_post))
    }

    open fun changePost(post: Post?) {
        this.post = post

        if (post?.distinguished == "moderator")
            text_title.setTextColor(ContextCompat.getColor(text_title.context, R.color.text_title_moderator))
        else
            text_title.setTextColor(ContextCompat.getColor(text_title.context, R.color.text_title_normal))
        text_title.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                post?.title ?: text_title.context.resources.getString(R.string.post_loading_title),
                TextViewCompat.getTextMetricsParams(text_title),
                null
            )
        )

        if (!post?.author_flair_text.isNullOrBlank()) {
            flair_user.visibility = View.VISIBLE
            flair_user.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    post!!.author_flair_text!!, TextViewCompat.getTextMetricsParams(flair_user), null
                )
            )
        } else {
            flair_user.visibility = View.GONE
        }

        text_user.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text_user.context.resources.getString(
                    R.string.post_user,
                    post?.author ?: text_user.context.resources.getString(R.string.post_loading_author)
                ),
                TextViewCompat.getTextMetricsParams(text_user), null
            )
        )

        text_subtitle.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text_subtitle.context.resources.getString(
                    R.string.post_subtitle,
                    PostUtils.getTime(post?.created_utc ?: System.currentTimeMillis())
                ),
                TextViewCompat.getTextMetricsParams(text_subtitle), null
            )
        )

        text_subreddit.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text_subreddit.context.resources.getString(
                    R.string.post_subreddit,
                    post?.subreddit ?: text_subreddit.context.resources.getString(R.string.post_loading_subreddit)
                ),
                TextViewCompat.getTextMetricsParams(text_subreddit), null
            )
        )

        if (!post?.link_flair_text.isNullOrBlank()) {
            flair_post.visibility = View.VISIBLE
            flair_post.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    post!!.link_flair_text!!, TextViewCompat.getTextMetricsParams(flair_post), null
                )
            )
        } else {
            flair_post.visibility = View.GONE
        }

        if (post?.over_18 == true)
            flair_nsfw.visibility = View.VISIBLE
        else
            flair_nsfw.visibility = View.GONE

        if (post?.gildings?.gid_1 ?: 0 > 0) {
            icon_silver.visibility = View.VISIBLE
            text_silverCount.visibility = View.VISIBLE
            text_silverCount.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    post!!.gildings.gid_1.toString(), TextViewCompat.getTextMetricsParams(text_silverCount), null
                )
            )
        } else {
            icon_silver.visibility = View.GONE
            text_silverCount.visibility = View.GONE
        }

        if (post?.gildings?.gid_2 ?: 0 > 0) {
            icon_gold.visibility = View.VISIBLE
            text_goldCount.visibility = View.VISIBLE
            text_goldCount.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    post!!.gildings.gid_2.toString(), TextViewCompat.getTextMetricsParams(text_goldCount), null
                )
            )
        } else {
            icon_gold.visibility = View.GONE
            text_goldCount.visibility = View.GONE
        }

        if (post?.gildings?.gid_3 ?: 0 > 0) {
            icon_platinum.visibility = View.VISIBLE
            text_platinumCount.visibility = View.VISIBLE
            text_platinumCount.setTextFuture(
                PrecomputedTextCompat.getTextFuture(
                    post!!.gildings.gid_3.toString(), TextViewCompat.getTextMetricsParams(text_platinumCount), null
                )
            )
        } else {
            icon_platinum.visibility = View.GONE
            text_platinumCount.visibility = View.GONE
        }

        if (post?.archived == true)
            flair_archived.visibility = View.VISIBLE
        else
            flair_archived.visibility = View.GONE

        if (post?.locked == true)
            flair_locked.visibility = View.VISIBLE
        else
            flair_locked.visibility = View.GONE

        if (post?.spoiler == true)
            flair_spoiler.visibility = View.VISIBLE
        else
            flair_spoiler.visibility = View.GONE

        if (post?.stickied == true)
            flair_stickied.visibility = View.VISIBLE
        else
            flair_stickied.visibility = View.GONE

        text_score.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                PostUtils.getShortNumber(post?.score ?: 0), TextViewCompat.getTextMetricsParams(text_score), null
            )
        )
        text_commentCount.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                PostUtils.getShortNumber(post?.num_comments ?: 0), TextViewCompat.getTextMetricsParams(text_commentCount), null
            )
        )
    }

    fun updateScore(item: Post?) {
        post = item
        text_score.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                "${post?.score ?: 0}", TextViewCompat.getTextMetricsParams(text_score), null
            )
        )
    }
}