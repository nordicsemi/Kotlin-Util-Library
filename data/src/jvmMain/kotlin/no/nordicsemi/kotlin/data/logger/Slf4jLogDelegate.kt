package no.nordicsemi.kotlin.data.logger

import org.slf4j.LoggerFactory

/**
 * Default JVM [LogDelegate] that forwards entries to SLF4J.
 *
 * - [LogCategory.name] becomes the underlying SLF4J logger name, so backends
 *   like Logback can route or filter per-category through their own config.
 * - The `source` argument is prepended to the message as `"[source] ..."`.
 *   It lands inside the message body deliberately: `slf4j-simple` (the
 *   default backend wired in `data/build.gradle.kts`) has no MDC support.
 *   If you swap in Logback and want a structured slot instead, write a
 *   bespoke delegate that pushes `source` into MDC.
 * - SLF4J's own per-logger threshold is consulted before [message] is
 *   evaluated, so a category disabled at the SLF4J layer also pays no
 *   string-construction cost.
 */
class Slf4jLogDelegate : LogDelegate {

    override fun log(
        category: LogCategory,
        level: LogLevel,
        source: String?,
        message: () -> String,
    ) {
        val slf4j = LoggerFactory.getLogger(category.name)
        val enabled = when (level) {
            LogLevel.TRACE -> slf4j.isTraceEnabled
            LogLevel.DEBUG -> slf4j.isDebugEnabled
            LogLevel.INFO -> slf4j.isInfoEnabled
            LogLevel.WARN -> slf4j.isWarnEnabled
            LogLevel.ERROR -> slf4j.isErrorEnabled
        }
        if (!enabled) return

        val text = if (source != null) "[$source] ${message()}" else message()
        when (level) {
            LogLevel.TRACE -> slf4j.trace(text)
            LogLevel.DEBUG -> slf4j.debug(text)
            LogLevel.INFO -> slf4j.info(text)
            LogLevel.WARN -> slf4j.warn(text)
            LogLevel.ERROR -> slf4j.error(text)
        }
    }
}
