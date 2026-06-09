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

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import no.nordicsemi.kotlin.id.Identifiable

/**
 * This method should return a default log sink for the platform.
 *
 * @param filter A filter that determines which logs should be logged. By default,
 * all log messages from [INFO][Log.Level.INFO] and above are passed through.
 */
internal expect fun <C : Log.Category> defaultSink(
    filter: (C, Log.Level) -> Boolean,
): Log.Sink<C>

/**
 * The `Log` component provides a simple logging API.
 *
 * Usually, the log [Emitter] and [Sink] are separate units.
 *
 * ## Library / Log producer
 *
 * The library modules produce (emit) logs, that can be consumed by the application and sent to
 * a console, or to a remote server by the implementation (sink).
 *
 * The emitter may produce logs from different components - [Category]s - and with different
 * severity levels - [Level]s.
 *
 * ### Category
 *
 * Library modules define *Categories* - the types of logs they emit. Those can be layers,
 * components, or anything else. Implementation may later filter logs by category.
 *
 * #### Example
 * ```kotlin
 * enum class LibraryComponent : Log.Category {
 *    COMPONENT_A,
 *    COMPONENT_B,
 * }
 * ```
 *
 * ### Emitter
 *
 * The [Emitter] interface define an object that produces (emits) logs. The type adds useful
 * shorthand methods for logging.
 *
 * Emitters may be [generic][Emitter] and [identifiable][IdentifiableEmitter]. Identifiable
 * emitters return their [identifier][Identifiable.identifier] as the `source` of the log entry.
 *
 * #### Example
 * ```kotlin
 * class LibraryManager: Log.Emitter {
 *    var logger: Log.Sink<LibraryComponent>? = null
 *
 *    fun event() {
 *       // ...
 *       logger?.i(LibraryComponent.COMPONENT_A) { "Some event" }
 *    }
 * }
 * ```
 *
 * ## Application / Log consumer
 *
 * The application consumes logs by implementing the [Sink] interface.
 *
 * ### Sink
 *
 * A sink is an object that can receive log events.
 *
 * #### Example
 *
 * ```kotlin
 * val m = LibraryManager()
 * m.logger = Log.Sink { category, level, source, throwable, message ->
 *    if (level >= Log.Level.DEBUG) {
 *        // Evaluate lazy message:
 *        val text = message()
 *        println("Event (cat: $category.name): $text")
 *    }
 * }
 * m.event()
 * ```
 *
 * ### Standard sink
 *
 * To log on the standard platform console (or Logcat on Android) use:
 * ```kotlin
 * m.logger = Log.Sink.Default()
 * ```
 * or customize the filter with:
 * ```kotlin
 * m.logger = Log.Sink.Default { category, level ->
 *    return level >= Log.Level.TRACE
 * }
 * ```
 *
 * ### Flow as Sink
 *
 * It is possible to direct logs into a [SharedFlow] of [Event]s using [Sink.Flow].
 * ```kotlin
 * val flowSink = Log.Sink.Flow<LibraryComponent>().also { flow ->
 *     flow
 *        .filter { it.category == LibraryComponent.COMPONENT_A }
 *        .onEach {
 *           // Note: Event.message is evaluated at-most once. The message builder is
 *           //       called under-the-hood when message is first accessed.
 *           println("Message: ${it.message}")
 *        }
 *        .launchIn(scope)
 * }
 * m.logger = flowSink
 * ```
 */
object Log {

    /**
     * Sink for log entries.
     *
     * Apps should implement the [log] method from interface to receives from [Emitter]s.
     *
     * ## Example
     * ```kotlin
     * val m = LibraryManager()
     * m.logger = Log.Sink { category, level, source, throwable, message ->
     *    if (level >= Log.Level.DEBUG) {
     *        println("Event (cat: $category): ${message()}")
     *    }
     * }
     * ```
     *
     * ### Standard sink
     *
     * To log on the standard platform console (or Logcat on Android) use:
     * ```kotlin
     * m.logger = Log.Sink.Default()
     * ```
     * or customize the filter with:
     * ```kotlin
     * m.logger = Log.Sink.Default { category, level ->
     *    return level >= Log.Level.TRACE
     * }
     * ```
     *
     * @see Implementation.Default
     */
    fun interface Sink<in C : Category> {

        /**
         * Implementation of a log sink.
         */
        companion object Implementation {

            /**
             * A default log sink.
             *
             * Logs are printed in the standard output for each platform. For Android this is the Logcat.
             *
             * @param filter A filter that determines which logs should be logged. By default,
             * all log messages from [INFO][Level.INFO] and above are passed through.
             *
             * @see Level
             * @see Category
             */
            @Suppress("FunctionName")
            fun <C : Category> Default(
                filter: (C, Level) -> Boolean = { _, level -> level >= Level.INFO }
            ): Sink<C> = defaultSink(filter)

            /**
             * A sink that does nothing.
             *
             * Note: This method returns `null`. Setting a `null` value as a nullable sink
             * property is more efficient than filtering out all logs. In this case not even the
             * message producing lambdas are created.
             */
            @Suppress("FunctionName")
            fun <C : Category> Disabled(): Sink<C>? = null

            /**
             * A sink that emits log [Event]s.
             *
             * ## Example
             * ```kotlin
             * val flow = MutableSharedFlow<Event<Log.Category>>()
             *     .also { flow ->
             *        flow
             *           .onEach { db.save(it.category.name, it.message) }
             *           .launchIn(scope)
             *     }
             *
             * // Forward logs from both libraries to the database.
             * // Note, that the libraries log with different Categories.
             * // They are flatten to the base `Log.Category`.
             * val m = LibraryManager() // logger: Log.Sink<LibraryComponent>
             * m.logger = Log.Sink.Flow(flow)
             *
             * val m2 = AnotherLibraryManager() // logger: Log.Sink<AnotherLibraryComponent>
             * m2.logger = Log.Sink.Flow(flow)
             * ```
             *
             * @param flow An optional [MutableSharedFlow] to emit [Event]s into. If not provided,
             * a new [MutableSharedFlow] is created. The flow has a default buffer capacity of 64,
             * and drops the oldest events on overflow.
             * @return A [SharedLogFlow] that emits [Event]s.
             */
            @Suppress("FunctionName")
            fun <C: Category> Flow(
                flow: MutableSharedFlow<Event<C>> = MutableSharedFlow(
                    extraBufferCapacity = 64,
                    onBufferOverflow = BufferOverflow.DROP_OLDEST,
                )
            ): SharedLogFlow<C> = SharedLogFlowImpl(flow)
        }

        /**
         * Log a message with the given [category] and [level].
         *
         * The [message] is lazy-evaluated, and should only be invoked if the entry passes the
         * log filters.
         *
         * @param category The category of the source producing the log entry.
         * @param level The severity level of the log entry.
         * @param source The source of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun log(
            category: C,
            level: Level,
            source: String?,
            throwable: Throwable?,
            message: () -> String,
        )
    }

    /**
     * A [Sink] that forwards logs to another [Sink].
     *
     * This can be used for internal implementations in [Emitter]s to forward logs to the main
     * sink.
     *
     * ## Example
     *
     * ### Library
     *
     * ```kotlin
     * enum class Component : Log.Category {
     *    A, B. C
     * }
     *
     * class Manager {
     *    var logger: Log.Sink<Component>? = null
     *    private val impl = Impl(
     *       // We can't pass `logger` here, as it's still null and can change over time.
     *       // Instead, drain the logs into a new sink using a Pipe.
     *       logger = Log.Pipe { logger }
     *    )
     *
     *    fun event() {
     *       logger?.i(Component.A) { "Some event" }
     *       impl.execute()
     *    }
     *
     *    private class Impl(
     *       private val logger: Log.Sink<Component>?
     *.   ) {
     *       fun execute() {
     *          logger?.d(Component.B) { "Executing" }
     *       }
     *    }
     * }
     * ```
     *
     * ### Application
     *
     * ```kotlin
     * val m = Manager()
     * m.logger = Log.Flow.Default()
     * m.event()
     * ```
     *
     * #### Output
     * ```
     * I [A] Some event
     * D [B] Executing
     * ```
     *
     * @param sink The sink to forward logs to.
     * @return A [Sink] that forwards logs to the given [sink].
     */
    @Suppress("FunctionName")
    fun <C: Category> Pipe(
        sink: () -> Sink<C>?
    ): Sink<C> = Sink { c, l, s, t, m ->
        sink()?.log(c, l, s, t, m)
    }

    /**
     * Severity of a log entry.
     *
     * The levels are ordered in order of severity.
     *
     * Think of them as:
     * ```
     * TRACE  -> "Show me everything."
     * DEBUG  -> "Show me useful debugging details."
     * INFO   -> "Show me normal operations."
     * WARN   -> "Something unexpected happened."
     * ERROR  -> "Something failed."
     * ASSERT -> "Something really bad happened."
     * ```
     */
    enum class Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT;

        /**
         * The short, one-character identifier of the level.
         */
        val n: String = name.first().toString()
    }

    /**
     * A typed label identifying the layer or component that produced a log entry.
     *
     * Consumers are expected to declare their own enum implementing this interface,
     * for example:
     *
     * ```kotlin
     * enum class TransportLayer : Category {
     *     HCI, L2CAP, GATT, APPLICATION,
     * }
     * ```
     * This property is satisfied automatically by Kotlin `enum`,
     * so no extra boilerplate is required on the consumer side.
     *
     * It is also possible to implement using a `value class` or `object`:
     * ```kotlin
     * @JvmInline
     * value class TransportLayer(override val name: String) : Category
     *
     * object Layer : Category {
     *     val HCI   = TransportLayer("HCI")
     *     val L2CAP = TransportLayer("L2CAP")
     *     val GATT  = TransportLayer("GATT")
     *     val APP   = TransportLayer("APPLICATION")
     * }
     * ```
     *
     * @property name The property name.
     */
    interface Category {
        val name: String
    }

    /**
     * A log event.
     *
     * The log events are emitted by [Sink.Flow].
     */
    class Event<C: Category>(val category: C, val level: Level, val source: String?, private val messageBuilder: () -> String) {
        /**
         * The log message.
         *
         * This message is evaluated at most once.
         */
        val message: String by lazy { messageBuilder() }

        override fun toString(): String {
            if (source == null) {
                return "${level.n} [$category] $message)"
            }
            return "${level.n} [$category] ($source) $message)"
        }
    }

    /**
     * A shared flow of log events, which can be assigned as a [Sink].
     *
     * It will emit [Event]s into the flow when [log] is called.
     *
     * ## Example
     * ```kotlin
     * class Manager {
     *    enum class Component : Log.Category {
     *       A, B, C
     *    }
     *    var logger: Log.Sink<Component>? = null
     * }
     *
     * val m = Manager()
     * m.logger = Log.Sink.Flow().also { flow ->
     *    flow
     *       .onEach { db.save(it) }
     *       .launchIn(scope)
     * }
     * ```
     */
    @OptIn(ExperimentalForInheritanceCoroutinesApi::class)
    interface SharedLogFlow<C: Category>: SharedFlow<Event<C>>, Sink<C>

    @OptIn(ExperimentalForInheritanceCoroutinesApi::class)
    private class SharedLogFlowImpl<C: Category>(
        private val flow: MutableSharedFlow<Event<C>>
    ): SharedLogFlow<C>, SharedFlow<Event<C>> by flow {

        override fun log(
            category: C,
            level: Level,
            source: String?,
            throwable: Throwable?,
            message: () -> String
        ) {
            flow.tryEmit(Event(category, level, source, message))
        }
    }

    /**
     * An instance of this interface can produce logs.
     *
     * Note, that this interface does not define a [Sink]. The library is free to choose a name
     * for a log sink, or even have multiple sinks if necessary. Usually, however, it is
     * recommended to have a single [Sink] emitting logs with some set of categories.
     *
     * ## Example
     *
     * ### Library
     *
     * ```kotlin
     * enum class LibraryComponent : Log.Category {
     *    A, B, C
     * }
     *
     * class LibraryManager: Log.Emitter {
     *    /** A log sink to emit logs to. */
     *    var logger: Log.Sink<LibraryComponent>? = null
     *
     *    fun event() {
     *       logger?.w(LibraryComponent.A) { "Event!!!" }
     *    }
     * }
     * ```
     *
     * ### Application
     *
     * ```kotlin
     * val m = LibraryManager()
     * m.logger = Log.Sink { c, l, s, t, messageBuilder ->
     *    if (l >= Log.Level.INFO) {
     *       val message = messageBuilder()
     *       Napier.d("[$c] ${m()}")
     *    }
     * }
     * ```
     */
    interface Emitter {

        /**
         * Log a message with the given [category] and [level].
         *
         * The [message] is lazy-evaluated, and should only be invoked if the entry passes the
         * log filters.
         *
         * @param category The category of the log entry.
         * @param level The severity level of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.log(
            category: C,
            level: Level,
            throwable: Throwable?,
            message: () -> String,
        ) = log(category, level, null, throwable, message)

        /**
         * A shortcut for logging only a [throwable]. The `message` is taken from the [throwable].
         *
         * @param category The category of the log entry.
         * @param level The severity level of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.log(
            category: C,
            level: Level,
            throwable: Throwable,
        ) = log(category, level, null, throwable) { throwable.message ?: throwable.toString() }

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.trace(category: C, throwable: Throwable? = null, message: () -> String) =
            log(category, Level.TRACE, throwable, message)

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.trace(category: C, throwable: Throwable) =
            log(category, Level.TRACE, throwable)

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * This is the same as [trace].
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.verbose(category: C, throwable: Throwable? = null, message: () -> String) =
            trace(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * This is the same as [trace].
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.verbose(category: C, throwable: Throwable) =
            trace(category, throwable)

        /**
         * Convenience methods for logging at [Level.DEBUG] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.debug(category: C, throwable: Throwable? = null, message: () -> String) =
            log(category, Level.DEBUG, throwable, message)

        /**
         * Convenience methods for logging at [Level.DEBUG] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.debug(category: C, throwable: Throwable) =
            log(category, Level.DEBUG, throwable)

        /**
         * Convenience methods for logging at [Level.INFO] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.info(category: C, throwable: Throwable? = null, message: () -> String) =
            log(category, Level.INFO, throwable, message)

        /**
         * Convenience methods for logging at [Level.INFO] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.info(category: C, throwable: Throwable) =
            log(category, Level.INFO, throwable)

        /**
         * Convenience methods for logging at [Level.WARN] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.warn(category: C, throwable: Throwable? = null, message: () -> String) =
            log(category, Level.WARN, throwable, message)

        /**
         * Convenience methods for logging at [Level.WARN] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.warn(category: C, throwable: Throwable) =
            log(category, Level.WARN, throwable)

        /**
         * Convenience methods for logging at [Level.ERROR] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.error(category: C, throwable: Throwable? = null, message: () -> String) =
            log(category, Level.ERROR, throwable, message)

        /**
         * Convenience methods for logging at [Level.ERROR] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.error(category: C, throwable: Throwable) =
            log(category, Level.ERROR, throwable)

        // Handy 1-letter overloads for the above methods:

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.t(category: C, throwable: Throwable? = null, message: () -> String) =
            trace(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.t(category: C, throwable: Throwable) =
            trace(category, throwable)

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.v(category: C, throwable: Throwable? = null, message: () -> String) =
            trace(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.TRACE] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.v(category: C, throwable: Throwable) =
            trace(category, throwable)

        /**
         * Convenience methods for logging at [Level.DEBUG] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.d(category: C, throwable: Throwable? = null, message: () -> String) =
            debug(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.DEBUG] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.d(category: C, throwable: Throwable) =
            debug(category, throwable)

        /**
         * Convenience methods for logging at [Level.INFO] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.i(category: C, throwable: Throwable? = null, message: () -> String) =
            info(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.INFO] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.i(category: C, throwable: Throwable) =
            info(category, throwable)

        /**
         * Convenience methods for logging at [Level.WARN] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.w(category: C, throwable: Throwable? = null, message: () -> String) =
            warn(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.WARN] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.w(category: C, throwable: Throwable) =
            warn(category, throwable)

        /**
         * Convenience methods for logging at [Level.ERROR] level.
         *
         * @param category The category of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        fun <C : Category> Sink<C>.e(category: C, throwable: Throwable? = null, message: () -> String) =
            error(category, throwable, message)

        /**
         * Convenience methods for logging at [Level.ERROR] level.
         *
         * @param category The category of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        fun <C : Category> Sink<C>.e(category: C, throwable: Throwable) =
            error(category, throwable)
    }

    /**
     * This interface should be implemented by an identifiable object that can produce logs.
     */
    interface IdentifiableEmitter<ID : Any>: Identifiable<ID>, Emitter {

        /**
         * Log a message with the given [category] and [level].
         *
         * The [message] is lazy-evaluated, and should only be invoked if the entry passes the
         * log filters.
         *
         * @param category The category of the log entry.
         * @param level The severity level of the log entry.
         * @param throwable An optional [Throwable] associated with the log entry,
         * such as an exception being logged.
         * @param message A lambda that produces the log message.
         */
        override fun <C : Category> Sink<C>.log(
            category: C,
            level: Level,
            throwable: Throwable?,
            message: () -> String,
        ) = log(category, level, identifier.toString(), throwable, message)

        /**
         * A shortcut for logging only a [throwable]. The `message` is taken from the [throwable].
         *
         * @param category The category of the log entry.
         * @param level The severity level of the log entry.
         * @param throwable A [Throwable] to be logged.
         */
        override fun <C : Category> Sink<C>.log(
            category: C,
            level: Level,
            throwable: Throwable,
        ) = log(category, level, identifier.toString(), throwable) { throwable.message ?: throwable.toString() }
    }
}
