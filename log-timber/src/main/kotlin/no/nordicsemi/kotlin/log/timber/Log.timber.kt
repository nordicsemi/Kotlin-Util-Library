/*
 * Copyright (c) 2026, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

@file:Suppress("unused")

package no.nordicsemi.kotlin.log.timber

import no.nordicsemi.kotlin.log.Log
import timber.log.Timber

/**
 * Timber log sink.
 *
 * Logs are redirected to [Timber](https://github.com/JakeWharton/timber).
 *
 * The Timber tag is composed of: `"${category.name}"` or `"${category.name} (${source})"`, if
 * `source` is not `null`.
 *
 * Remember to [plant a `Timber.Tree`](https://github.com/JakeWharton/timber/blob/17cfcbff764c510371f64d5118c235dbe91f75c3/timber-sample/src/main/java/com/example/timber/ExampleApp.java#L16-L20)
 * to get logs.
 *
 * *There are no Tree implementations installed by default because every time you log
 * in production, a puppy dies.* -J. W.
 *
 * @param filter Log level filter.
 */
@Suppress("FunctionName")
fun <C : Log.Category>Log.Sink.Implementation.Timber(
    filter: (C, Log.Level) -> Boolean = { _, level -> level >= Log.Level.INFO }
): Log.Sink<C> = TimberSink(filter)

private class TimberSink<C : Log.Category>(
    private val filter: (C, Log.Level) -> Boolean
) : Log.Sink<C> {

    override fun log(
        category: C,
        level: Log.Level,
        source: String?,
        throwable: Throwable?,
        message: () -> String
    ) {
        // Apply the filter.
        if (!filter(category, level)) return

        // Build the tag as category name and source.
        val tag = source?.let { "${category.name} ($it)" } ?: category.name
        val tree = Timber.tag(tag)
        when (level) {
            Log.Level.TRACE -> tree.v(throwable, message())
            Log.Level.DEBUG -> tree.d(throwable, message())
            Log.Level.INFO  -> tree.i(throwable, message())
            Log.Level.WARN  -> tree.w(throwable, message())
            Log.Level.ERROR -> tree.e(throwable, message())
            Log.Level.ASSERT-> tree.wtf(throwable, message())
        }
    }
}