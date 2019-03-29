/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ekhaled1836.tp.reddit

import android.app.Application
import android.content.Context
import androidx.annotation.VisibleForTesting
import me.ekhaled1836.tp.reddit.api.RedditApi
import me.ekhaled1836.tp.reddit.db.RedditDatabase
import me.ekhaled1836.tp.reddit.repository.CommentsRepository
import me.ekhaled1836.tp.reddit.repository.PostsRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Super simplified service locator implementation to allow us to replace default implementations
 * for testing.
 */
interface ServiceLocator {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        fun instance(context: Context): ServiceLocator {
            synchronized(LOCK) {
                if (instance == null) {
                    instance = DefaultServiceLocator(
                            app = context.applicationContext as Application)
                }
                return instance!!
            }
        }

        /**
         * Allows tests to replace the default implementations.
         */
        @VisibleForTesting
        fun swap(locator: ServiceLocator) {
            instance = locator
        }
    }

    fun getPostsRepository(): PostsRepository

    fun getCommentsRepository(): CommentsRepository

    fun getNetworkExecutor(): Executor

    fun getDiskIOExecutor(): Executor

    fun getRedditApi(): RedditApi
}

/**
 * default implementation of ServiceLocator that uses production endpoints.
 */
open class DefaultServiceLocator(val app: Application) : ServiceLocator {
    // thread pool used for disk access
    @Suppress("PrivatePropertyName")
    private val DISK_IO = Executors.newSingleThreadExecutor()

    // thread pool used for network requests
    @Suppress("PrivatePropertyName")
    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    private val db by lazy {
        RedditDatabase.getDatabase(app)
    }

    private val api by lazy {
        RedditApi.create()
    }

    override fun getPostsRepository(): PostsRepository {
            return PostsRepository(
                    db = db,
                    redditApi = getRedditApi(),
                    ioExecutor = getDiskIOExecutor())
    }

    override fun getCommentsRepository(): CommentsRepository {
        return CommentsRepository(
            db = db,
            redditApi = getRedditApi(),
            ioExecutor = getDiskIOExecutor())
    }

    override fun getNetworkExecutor(): Executor = NETWORK_IO

    override fun getDiskIOExecutor(): Executor = DISK_IO

    override fun getRedditApi(): RedditApi = api
}