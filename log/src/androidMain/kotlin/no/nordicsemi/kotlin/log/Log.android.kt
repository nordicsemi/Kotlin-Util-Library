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

package no.nordicsemi.kotlin.log

import android.util.Log as Logcat

internal actual fun <C : Log.Category> defaultSink(
    filter: (C, Log.Level) -> Boolean,
): Log.Sink<C> = LogcatSink(filter)

private class LogcatSink<C : Log.Category>(
    private val filter: (C, Log.Level) -> Boolean,
): Log.Sink<C> {

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
        val safeTag = if (tag.length > 23 && android.os.Build.VERSION.SDK_INT < 26) tag.take(23) else tag
        val text = message()
        when (level) {
            Log.Level.TRACE -> Logcat.v(safeTag, text, throwable)
            Log.Level.DEBUG -> Logcat.d(safeTag, text, throwable)
            Log.Level.INFO  -> Logcat.i(safeTag, text, throwable)
            Log.Level.WARN  -> Logcat.w(safeTag, text, throwable)
            Log.Level.ERROR -> Logcat.e(safeTag, text, throwable)
            Log.Level.ASSERT -> Logcat.wtf(safeTag, text, throwable)
        }
    }

}