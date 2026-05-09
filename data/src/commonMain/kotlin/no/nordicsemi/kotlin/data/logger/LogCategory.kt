package no.nordicsemi.kotlin.data.logger

/**
 * A typed label identifying the layer or component that produced a log entry.
 *
 * Consumers are expected to declare their own enum implementing this interface,
 * for example:
 *
 * ```
 * enum class TransportLayer : LogCategory {
 *     HCI, L2CAP, GATT, APPLICATION,
 * }
 * ```
 *
 * The [name] property is satisfied automatically by any Kotlin enum, so no
 * extra boilerplate is required on the consumer side.
 */
interface LogCategory {
    val name: String
}
