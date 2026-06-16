# Module log-timber

Timber addon for the logging facade.

The `:log-timber` module provides a `Log.Sink` implementation that forwards logs from `:log`
to [Timber](https://github.com/JakeWharton/timber).

# Package no.nordicsemi.kotlin.log.timber

Contains the `Log.Sink.Timber(...)` extension function.

## What it does

- Bridges `no.nordicsemi.kotlin.log.Log` events to Timber.
- Keeps lazy message evaluation from `:log`.
- Supports category and source-aware tags.
- Lets you customize the log-level filter.

## Tag format

The Timber tag is built as:

- `${category.name}`,
- `${category.name} (${source})` when source is available.

## Usage

```kotlin
import no.nordicsemi.kotlin.log.Log
import no.nordicsemi.kotlin.log.timber.Timber
import timber.log.Timber

enum class AppCategory : Log.Category {
    NETWORK,
    STORAGE,
}

fun configureLogging(): Log.Sink<AppCategory> {
    Timber.plant(Timber.DebugTree())

    // INFO and above by default; override with your own filter.
    return Log.Sink.Timber { _, level ->
        level >= Log.Level.DEBUG
    }
}
```
