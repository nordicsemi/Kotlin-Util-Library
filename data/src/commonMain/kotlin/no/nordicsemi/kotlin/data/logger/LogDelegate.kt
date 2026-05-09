package no.nordicsemi.kotlin.data.logger

/**
 * Sink for log entries produced by a [Logger].
 *
 * Implementations decide *how* and *whether* to render an entry — through
 * SLF4J, NSLog, OSLog, `println`, an in-memory ring buffer, a network sink,
 * a test spy, etc. The library ships ready-to-use defaults for the JVM
 * ([Slf4jLogDelegate]) and Apple ([NSLogDelegate]) targets; supplying your own
 * is a one-liner thanks to SAM conversion:
 *
 * ```
 * val logger = Logger(LogDelegate { category, level, source, message ->
 *     println("[$level] [${category.name}] ${source ?: "-"}: ${message()}")
 * })
 * ```
 *
 * The [message] block is evaluated only after [Logger] has decided to forward
 * the entry, so callers pay no string-construction cost for filtered-out
 * levels. Implementations that further gate by their own configuration (e.g.
 * SLF4J's per-logger thresholds) should also delay invoking [message] until
 * after those checks.
 */
fun interface LogDelegate {
    fun log(
        category: LogCategory,
        level: LogLevel,
        source: String?,
        message: () -> String,
    )
}
