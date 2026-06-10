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

package no.nordicsemi.kotlin.log

import kotlin.test.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.*

class LogTest {

    private enum class TestCategory : Log.Category {
        TEST
    }

    private class TestSink(
        private val filter: (Log.Category, Log.Level) -> Boolean = { _, _ -> true },
    ) : Log.Sink<Log.Category> {
        var lastCategory: Log.Category? = null
        var lastLevel: Log.Level? = null
        var lastSource: String? = null
        var lastThrowable: Throwable? = null
        var lastMessage: String? = null
        var logCount = 0

        override fun log(
            category: Log.Category,
            level: Log.Level,
            source: String?,
            throwable: Throwable?,
            message: () -> String,
        ) {
            if (filter(category, level)) {
                lastCategory = category
                lastLevel = level
                lastSource = source
                lastThrowable = throwable
                lastMessage = message()
                logCount++
            }
        }
    }

    @Test
    fun `test emitter lazy evaluation`() {
        val sink = TestSink { _, level -> level >= Log.Level.INFO }
        val emitter = object : Log.Emitter {}
        var evaluatedCount = 0

        with(emitter) {
            sink.debug(TestCategory.TEST) {
                evaluatedCount++
                "Debug message"
            }
        }

        assertEquals(0, evaluatedCount, "Message should not be evaluated when filtered out")
        assertEquals(0, sink.logCount)

        with(emitter) {
            sink.info(TestCategory.TEST) {
                evaluatedCount++
                "Info message"
            }
        }

        assertEquals(1, evaluatedCount, "Message should be evaluated when not filtered out")
        assertEquals(1, sink.logCount)
        assertEquals("Info message", sink.lastMessage)
    }

    @Test
    fun `test identifiable emitter source`() {
        val sink = TestSink()
        val emitter = object : Log.IdentifiableEmitter<String> {
            override val identifier: String = "test-source"
        }

        with(emitter) {
            sink.i(TestCategory.TEST) { "Message" }
        }

        assertEquals("test-source", sink.lastSource, "IdentifiableEmitter should forward identifier as source")
    }

    @Test
    fun `test emitter shorthands`() {
        val sink = TestSink()
        val emitter = object : Log.Emitter {}

        with(emitter) {
            sink.trace(TestCategory.TEST) { "trace" }
            assertEquals(Log.Level.TRACE, sink.lastLevel)

            sink.debug(TestCategory.TEST) { "debug" }
            assertEquals(Log.Level.DEBUG, sink.lastLevel)

            sink.info(TestCategory.TEST) { "info" }
            assertEquals(Log.Level.INFO, sink.lastLevel)

            sink.warn(TestCategory.TEST) { "warn" }
            assertEquals(Log.Level.WARN, sink.lastLevel)

            sink.error(TestCategory.TEST) { "error" }
            assertEquals(Log.Level.ERROR, sink.lastLevel)
        }
    }

    @Test
    fun `test emitter one letter shorthands`() {
        val sink = TestSink()
        val emitter = object : Log.Emitter {}

        with(emitter) {
            sink.t(TestCategory.TEST) { "t" }
            assertEquals(Log.Level.TRACE, sink.lastLevel)

            sink.v(TestCategory.TEST) { "v" }
            assertEquals(Log.Level.TRACE, sink.lastLevel)

            sink.d(TestCategory.TEST) { "d" }
            assertEquals(Log.Level.DEBUG, sink.lastLevel)

            sink.i(TestCategory.TEST) { "i" }
            assertEquals(Log.Level.INFO, sink.lastLevel)

            sink.w(TestCategory.TEST) { "w" }
            assertEquals(Log.Level.WARN, sink.lastLevel)

            sink.e(TestCategory.TEST) { "e" }
            assertEquals(Log.Level.ERROR, sink.lastLevel)
        }
    }

    @Test
    fun `test throwable only logging`() {
        val sink = TestSink()
        val emitter = object : Log.Emitter {}
        val exception = RuntimeException("test error")

        with(emitter) {
            sink.error(TestCategory.TEST, exception)
            assertEquals(Log.Level.ERROR, sink.lastLevel)
            assertEquals(exception, sink.lastThrowable)
            assertEquals("test error", sink.lastMessage)
        }
    }

    @Test
    fun `test level order`() {
        assertTrue(Log.Level.TRACE < Log.Level.DEBUG)
        assertTrue(Log.Level.DEBUG < Log.Level.INFO)
        assertTrue(Log.Level.INFO < Log.Level.WARN)
        assertTrue(Log.Level.WARN < Log.Level.ERROR)
        assertTrue(Log.Level.ERROR < Log.Level.ASSERT)
    }

    @Test
    fun `test level identifier`() {
        assertEquals("T", Log.Level.TRACE.n)
        assertEquals("D", Log.Level.DEBUG.n)
        assertEquals("I", Log.Level.INFO.n)
        assertEquals("W", Log.Level.WARN.n)
        assertEquals("E", Log.Level.ERROR.n)
        assertEquals("A", Log.Level.ASSERT.n)
    }

    @Test
    fun `test disabled sink`() {
        val sink: Log.Sink<TestCategory>? = Log.Sink.Null
        assertNull(sink, "Disabled sink should be null")
    }

    @Test
    fun `test relay`() {
        var activeSink: TestSink? = null
        val emitter = object : Log.Emitter {}
        val relay = emitter.Relay { activeSink }

        // Log when relay destination is null
        with(emitter) {
            relay.info(TestCategory.TEST) { "Message 1" }
        }

        // Log when relay destination is set
        val testSink = TestSink()
        activeSink = testSink
        with(emitter) {
            relay.info(TestCategory.TEST) { "Message 2" }
        }

        assertEquals(1, testSink.logCount)
        assertEquals("Message 2", testSink.lastMessage)
    }

    @Test
    fun `test flow sink`() = runTest {
        val sink = Log.Sink.Flow<TestCategory>(MutableSharedFlow(replay = 1))
        val emitter = object : Log.Emitter {}

        with(emitter) {
            sink.info(TestCategory.TEST) { "Flow message" }
        }

        val event = sink.first()
        assertEquals(TestCategory.TEST, event.category)
        assertEquals(Log.Level.INFO, event.level)
        assertEquals("Flow message", event.message)
    }

    @Test
    fun `test flow event lazy evaluation`() = runTest {
        val sink = Log.Sink.Flow<TestCategory>(MutableSharedFlow(replay = 1))
        val emitter = object : Log.Emitter {}
        var evaluated = false

        with(emitter) {
            sink.info(TestCategory.TEST) {
                evaluated = true
                "Lazy message"
            }
        }

        assertFalse(evaluated, "Message should not be evaluated until accessed")
        val event = sink.first()
        assertEquals("Lazy message", event.message)
        assertTrue(evaluated, "Message should be evaluated after access")
    }
}
