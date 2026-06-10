# Module log

Generic logging facade for Kotlin Multiplatform libraries.

The `:log` module provides a flexible, lightweight logging subsystem designed specifically for 
KMP libraries and applications. It separates log production (Emitters) from log consumption (Sinks), 
allowing libraries to remain agnostic of the final logging implementation.

# Package no.nordicsemi.kotlin.log

Contains the core logging API, including the `Log` facade, `Sink`, and `Emitter` interfaces.

## Key Features

- **Lazy Evaluation**: Log messages are only computed if they pass the sink's filters.
- **KMP Support**: Works across Android, JVM, iOS, and macOS.
- **Strong Typing**: Uses categories defined by the library modules for better filtering.
- **Identifiable Emitters**: Automatically includes source identifiers for multi-instance components.
- **Coroutines Integration**: Built-in support for directing logs to a `SharedFlow`.

## Architecture Overview

The subsystem consists of three main components:

1.  **`Log.Category`**: Defines *what* is being logged (e.g., "GATT", "STORAGE").
2.  **`Log.Emitter`**: Defines *who* is producing logs.
3.  **`Log.Sink`**: Defines *where* the logs go (e.g., Logcat, console, database).

```text
[Log.Emitter] --(Log.Event)--> [Log.Sink]
```

## Library Guide (Log Producers)

Libraries should define their own categories and use the `Log.Emitter` interface.

### 1. Define Categories

Categories are typically defined as an `enum class` within the library module.

```kotlin
enum class MyLibraryCategory : Log.Category {
    NETWORK,
    DATABASE,
    UI
}
```

### 2. Implement an Emitter

Any class can implement `Log.Emitter`. This provides access to shorthand logging methods (`i()`, `d()`, `e()`, etc.).

```kotlin
class NetworkManager : Log.Emitter {
    var logger: Log.Sink<MyLibraryCategory>? = Log.Sink.Null // or null

    fun connect() {
        // Shorthand for Log.Level.INFO
        logger?.i(MyLibraryCategory.NETWORK) { "Connecting to server..." }
    }
}
```

### 3. Identifiable Emitters

For components where multiple instances exist (e.g., several connected devices), 
use `Log.IdentifiableEmitter`. This automatically includes the component's `identifier` in 
every log entry.

```kotlin
class DeviceManager(
    override val identifier: String // e.g., MAC address, e.g. "AA:BB:CC:DD:EE:FF"
) : Log.IdentifiableEmitter<String> {
    var logger: Log.Sink<MyLibraryCategory>? = Log.Sink.Null // or null

    fun update() {
        // The source field in Log.Sink will be "AA:BB:CC:DD:EE:FF"
        logger?.d(MyLibraryCategory.DATABASE) { "Updating record" }
    }
}
```

## Application Guide (Log Consumers)

Applications consume logs by assigning a `Log.Sink` to the library's emitter.

### 1. Using the Default Sink

The library provides a platform-specific default sink that logs to the standard console (e.g., Logcat on Android).

```kotlin
val manager = NetworkManager()

// Default logs INFO and above
manager.logger = Log.Sink.Default()

// Custom filter: log everything from DEBUG and above
manager.logger = Log.Sink.Default { category, level ->
    level >= Log.Level.DEBUG
}
```

### 2. Reactive Logging with Flow

Direct logs into a `SharedFlow` for reactive processing or UI display.

```kotlin
val logFlow = Log.Sink.Flow<MyLibraryCategory>()

manager.logger = logFlow

// Consume logs as a Flow
logFlow
    .onEach { event ->
        println("${event.level.n} [${event.category.name}]: ${event.message}")
    }
    .launchIn(lifecycleScope)
```

### 3. Custom Sink Implementation

Implement the `Log.Sink` interface to redirect logs to 3rd-party libraries like Napier or a custom database.

```kotlin
manager.logger = Log.Sink { category, level, source, throwable, messageBuilder ->
    if (level >= Log.Level.WARN) {
        val tag = source?.let { "${category.name} | $it" } ?: category.name
        val message = messageBuilder() // Lazy evaluation happens here
        
        Napier.log(level.toNapierPriority(), tag, throwable, message)
    }
}
```

## Utilities

### Relay

Use `Emitter.Relay` to forward logs from an internal component to a sink that might be updated later.

```kotlin
class Parent: Log.Emitter {
    var logger: Log.Sink<MyCategory>? = Log.Sink.Null
    private val child = Child(logger = Relay { logger })
}
```

### Disabling Logs

To completely disable logging, assign `null` (or use `Log.Sink.Null`). 
This is highly efficient as it prevents any message lambda creation or evaluation.

```kotlin
manager.logger = Log.Sink.Null // Same as manager.logger = null
```

## Multiple Emitters

In some cases an app uses multiple libraries, each emitting logs with their own categories.

Consider this example:

#### Library / Module A

```kotlin
enum class Layer: Log.Category {
    TRANSPORT, GATT
}

class Manager: Log.Emitter {
    var logger: Log.Sink<Module>? = Log.Sink.Null

    fun event() {
        logger?.t(Module.TRANSPORT) { "event started" }
        try {
           // [...]
        } catch (e: Exception) {
            logger?.i(Module.TRANSPORT, e) { "event failed" }
        }
    }
}
```

#### Library / Module B

```kotlin
enum class Module: Log.Category {
    HEART_RATE, BATTERY,  
}

// Some inner implementation
internal class BatteryManager(val logger: Log.Sink<YourCat>): Log.Emitter {
    // [...]
    
    fun checkLevel() {
        if (isLowLevel()) {
            logger.w(Module.BATTERY) { "Battery level is low!" }
        }
    }
}

class DeviceService(
    override val identifier: Int
): Log.IdentifiableEmitter<Int> {
    var logger: Log.Sink<YourCat>? = Log.Sink.Null
    
    val bm = BatteryManager(
        logger = Relay { logger }
    )
    
    fun connect() {
        logger?.i(Module.HEART_RATE) { "Connecting" }
        // [...]
        bm.checkLevel()
    }
}
```

#### Application

An app, that depends on both libraries, can define a common `Sink`:

```kotlin
val manager = Manager()                                // emits categories from `Layer`
val deviceService = DeviceService("AA:BB:CC:DD:EE:FF") // emits categories from `Module`

// Sink for logs with any category:
val sink = Log.Sink.Default<Log.Category> { category, level ->
    // `category` is a Log.Category
    // Get name using `category.name`
    level >= Log.Level.WARN // Log only WARN and above
}
manager.logger = sink
deviceService.logger = sink
```