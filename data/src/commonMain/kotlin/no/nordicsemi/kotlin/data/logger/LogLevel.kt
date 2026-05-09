package no.nordicsemi.kotlin.data.logger

/**
 * Severity of a log entry.
 *
 * Declared in ascending order of importance, so [Logger] can compare values
 * directly (`if (level >= minLevel)`) using the natural enum ordering.
 */
enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
}
