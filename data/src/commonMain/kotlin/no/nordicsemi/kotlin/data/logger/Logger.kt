package no.nordicsemi.kotlin.data.logger

/**
 * Lazy, level-gated, source-aware logging facade.
 *
 * The logger itself is platform-agnostic; everything platform-specific lives
 * inside the supplied [LogDelegate]. A single [Logger] can therefore be wired
 * to any sink (SLF4J, NSLog, an in-memory buffer, a test spy, etc.).
 *
 * Three design pillars:
 *
 * - **Lazy.** Each `log(...)` call accepts `() -> String`; the message is
 *   built only after the [minLevel] gate passes.
 * - **Categorised.** Every entry carries a [LogCategory] (typically a
 *   consumer-defined enum) so logs can be grouped by layer/component instead
 *   of relying on free-form string tags.
 * - **Source-aware.** Multiple producers (e.g. several connected devices)
 *   may share one logger. The [source] argument is passed to the delegate as
 *   a structured field, not embedded in the message body.
 *
 * @param delegate Sink that receives every entry that passes the [minLevel] gate.
 * @param minLevel Minimum severity that will be forwarded. Entries below this
 *                 level are dropped before [delegate] is invoked and before
 *                 the message lambda is evaluated.
 */
class Logger(
    private val delegate: LogDelegate,
    private val minLevel: LogLevel = LogLevel.TRACE,
) {

    /** Forwards an entry to [delegate] iff [level] >= [minLevel]. */
    fun log(
        category: LogCategory,
        level: LogLevel,
        source: String? = null,
        message: () -> String,
    ) {
        if (level >= minLevel) {
            delegate.log(category, level, source, message)
        }
    }

    fun trace(category: LogCategory, source: String? = null, message: () -> String) {
        log(category, LogLevel.TRACE, source, message)
    }

    fun debug(category: LogCategory, source: String? = null, message: () -> String) {
        log(category, LogLevel.DEBUG, source, message)
    }

    fun info(category: LogCategory, source: String? = null, message: () -> String) {
        log(category, LogLevel.INFO, source, message)
    }

    fun warn(category: LogCategory, source: String? = null, message: () -> String) {
        log(category, LogLevel.WARN, source, message)
    }

    fun error(category: LogCategory, source: String? = null, message: () -> String) {
        log(category, LogLevel.ERROR, source, message)
    }

    /**
     * Convenience for error logging with an attached [Throwable]. The stack
     * trace is appended to the message so it survives transports (such as
     * [NSLogDelegate]) that have no separate slot for exceptions.
     */
    fun error(
        category: LogCategory,
        throwable: Throwable,
        source: String? = null,
        message: () -> String,
    ) {
        log(category, LogLevel.ERROR, source) {
            "${message()}\n${throwable.stackTraceToString()}"
        }
    }
}
