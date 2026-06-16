# Module id

A lightweight module providing a standard interface for identifiable objects.

This module is designed to be a common dependency for other modules that need to work with unique 
identifiers in a type-safe way.

# Package no.nordicsemi.kotlin.id

Contains the [Identifiable] interface.

## Usage

Implement this interface in any class that requires a unique identity.

```kotlin
class Device(override val identifier: String) : Identifiable<String>
```

The [Identifiable] interface is particularly useful when combined with other modules, such as `:log`, 
to provide automatic context to operations (e.g., source-tagged logging).
