package no.nordicsemi.kotlin.data.logger

import platform.Foundation.NSLog

/**
 * Default Apple-platforms [LogDelegate] that forwards entries to `NSLog`.
 *
 * Output format:
 *
 * ```
 * [LEVEL] [Category] [source] message
 * ```
 *
 * `NSLog` prefixes every line with a timestamp and process name on its own,
 * so we only contribute the structured fields above. Any `%` characters in
 * the resulting line are doubled before the call, since `NSLog` interprets
 * them as `printf`-style format specifiers and would otherwise crash or
 * print garbage on user input that contains a literal percent sign.
 */
class NSLogDelegate : LogDelegate {

    override fun log(
        category: LogCategory,
        level: LogLevel,
        source: String?,
        message: () -> String,
    ) {
        val sourcePart = if (source != null) " [$source]" else ""
        val line = "[${level.name}] [${category.name}]$sourcePart ${message()}"
        NSLog(line.replace("%", "%%"))
    }
}
