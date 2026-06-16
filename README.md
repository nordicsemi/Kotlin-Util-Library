# Kotlin Util Library

![Kotlin Multiplatform](https://img.shields.io/badge/kmp-ready-green)

This repository contains a set of Kotlin-only (no Android dependency) libraries for various purposes, designed specifically for Kotlin Multiplatform projects.

### Documentation

Dokka documentation for all modules can be found [here](https://nordicsemi.github.io/Kotlin-Util-Library/html/index.html).

---

## Modules

| Module | Description | Version |
|:---|:---|:---|
| [`:data`](#data) | Extension methods for `ByteArray`, `Byte`, `Short`, and UUID operations. | ![Maven Central](https://img.shields.io/maven-central/v/no.nordicsemi.kotlin/data) |
| [`:id`](#id) | A simple interface for objects with unique identifiers. | ![Maven Central](https://img.shields.io/maven-central/v/no.nordicsemi.kotlin/id) |
| [`:log`](#log) | A flexible, lightweight logging facade for KMP libraries and apps. | ![Maven Central](https://img.shields.io/maven-central/v/no.nordicsemi.kotlin/log) |
| [`:log-timber`](#log-timber) | Timber sink addon for forwarding `:log` events on Android. | ![Maven Central](https://img.shields.io/maven-central/v/no.nordicsemi.kotlin/log-timber) |

---

## Data

Kotlin library for data manipulation. It contains extension methods for `ByteArray` allowing to read numbers in big-endian and little-endian order in various formats, bit operators for `Byte` and `Short`, and methods for converting UUIDs to and from `ByteArray`.

### Setting up

```kotlin
implementation("no.nordicsemi.kotlin:data:<version>")
```

### Usage Examples

#### Reading Integers & Floats
```kotlin
val array = byteArrayOf(0x87.toByte(), 0x65, 0x43, 0x21, 0xFF.toByte(), 0x00, 0x01, 0x6C)

// Read Int32
val intVal = array.getInt(offset = 0, format = IntFormat.INT32, order = ByteOrder.BIG_ENDIAN)

// Read IEEE 11073 32-bit Float
val floatVal = array.getFloat(offset = 4, format = FloatFormat.IEEE_11073_32_BIT, order = ByteOrder.BIG_ENDIAN)
```

#### Bit operators
```kotlin
val value = 0b1010_1001.toByte()
assertTrue(value hasAllBitsSet 0b1000_0001)
assertTrue(value hasBitSet 0)
```

#### UUID
```kotlin
val uuid = UUID.randomUUID()
val array = uuid.toByteArray(order = ByteOrder.BIG_ENDIAN)
val recoveredUuid = array.toUuid(order = ByteOrder.BIG_ENDIAN)
```

---

## ID

A lightweight module providing a standard interface for identifiable objects.

### Setting up

```kotlin
implementation("no.nordicsemi.kotlin:id:<version>")
```

### Usage

```kotlin
class Device(override val identifier: String) : Identifiable<String>
```

---

## Log

A flexible logging facade that separates log production (Emitters) from log consumption (Sinks).

### Key Features
- **Lazy Evaluation**: Messages are only computed if they pass the sink's filters.
- **Strong Typing**: Use your own `enum class` for log categories.
- **Identifiable Emitters**: Automatically includes source identifiers for multi-instance components.
- **Relay**: Easily forward logs from internal components to the main application sink.

### Setting up

```kotlin
implementation("no.nordicsemi.kotlin:log:<version>")
```

### Usage Example

#### 1. Define Categories and Emitter
```kotlin
enum class MyCategory : Log.Category { NETWORK, UI }

// Emitter for a single instance component
class NetworkManager : Log.Emitter {
    var logger: Log.Sink<MyCategory>? = Log.Sink.Null

    fun connect() {
        // Message is only evaluated if logger is not null and filter passes
        logger?.i(MyCategory.NETWORK) { "Connecting to server..." }
    }
}

// IdentifiableEmitter for multi-instance components (e.g. connected devices)
class Device(override val identifier: String) : Identifiable<String>, Log.IdentifiableEmitter<String> {
    var logger: Log.Sink<MyCategory>? = Log.Sink.Null

    fun update() {
        // Automatically includes the identifier ("source") in log events
        logger?.d(MyCategory.UI) { "Updating UI" }
    }
}
```

#### 2. Assign a Sink in the Application
```kotlin
val manager = NetworkManager()

// Log to console (Logcat on Android, println on JVM/Native)
// By default, it logs Level.INFO and above.
manager.logger = Log.Sink.Default() 

// Or customize the filter
manager.logger = Log.Sink.Default { category, level ->
    level >= Log.Level.DEBUG && category == MyCategory.NETWORK
}

// Or direct to a SharedFlow for reactive processing
val logFlow = Log.Sink.Flow<MyCategory>()
manager.logger = logFlow
```

---

## Log Timber

Android addon for `:log` that forwards logs to [Timber](https://github.com/JakeWharton/timber).

### Setting up

```kotlin
implementation("no.nordicsemi.kotlin:log-timber:<version>")
```

### Usage Example

```kotlin
import no.nordicsemi.kotlin.log.Log
import no.nordicsemi.kotlin.log.timber.Timber
import timber.log.Timber

enum class AppCategory : Log.Category { NETWORK }

fun createLogger(): Log.Sink<AppCategory> {
    Timber.plant(Timber.DebugTree())

    return Log.Sink.Timber { _, level ->
        level >= Log.Level.DEBUG
    }
}
```

---

License
-------

The Kotlin Util Library is available under the [BSD 3-Clause license](LICENSE).
