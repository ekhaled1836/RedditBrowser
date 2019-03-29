package me.ekhaled1836.tp.reddit.api

import android.util.Log
import com.google.gson.GsonBuilder
import me.ekhaled1836.tp.reddit.model.*
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

interface RedditApi {

    @GET("r/{subreddit}/{sort}.json?raw_json=1")
    fun getPosts(
        @Path("subreddit") subreddit: String,
        @Path("sort") sortedby: String,
        @Query("t") period: String?,
        @Query("q") query: String?,
        @Query("limit") limit: Int?,
        @Query("after") after: String?
    ): Call<PostsListing>

    @GET("api/subreddit_autocomplete.json?raw_json=1")
    fun subredditAutoComplete(@Query("query") query: String, @Query("include_over_18") nsfw: Boolean = false)
            : Call<AutoCompleteRoot>

    @GET("r/{subreddit}/comments/{parent_id}.json?raw_json=1")
    fun getComments(
        @Path("subreddit") subreddit: String,
        @Path("parent_id") parent_id: String
    ): Call<List<CommentsListing>>

    companion object {
        private const val BASE_URL = "https://www.reddit.com/"
        @Inject fun create(): RedditApi = create(HttpUrl.parse(BASE_URL)!!)
        fun create(httpUrl: HttpUrl): RedditApi {
            val logger = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("API", it)
            })
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val gson = GsonBuilder().registerTypeAdapter(MoreComment::class.java, MoreCommentDeserializer()).create()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(RedditApi::class.java)
        }
    }
}