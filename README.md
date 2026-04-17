# Kotlin Util Library

This repository contains set of Kotlin-only (no Android dependency) libraries for various purposes.

## Data

![Maven Central Version](https://img.shields.io/maven-central/v/no.nordicsemi.kotlin/data)  ![Kotlin Multiplatform](https://img.shields.io/badge/kmp-ready-green)

Kotlin library for data manipulation.

It contains extension methods for `ByteArray` allowing to read numbers in big-endian and little-endian 
order in various formats, bit operators for `Byte` and `Short` and methods for converting UUIDs to 
and from `ByteArray`.

### Setting up

Add the following to your `build.gradle` file:

```groovy
implementation "no.nordicsemi.kotlin:data:<version>"
```

Artifacts can be found on [Maven Central repository](https://central.sonatype.com/artifact/no.nordicsemi.kotlin/data/versions).

### Documentation

Dokka documentation can be found [here](https://nordicsemi.github.io/Kotlin-Util-Library/html/index.html).

#### Reading Integers

```kotlin
val array = byteArrayOf(0x87.toByte(), 0x65, 0x43, 0x21, 0x21, 0x43, 0x65, 0x87.toByte())
val int1 = array.getInt(offset = 0, format = IntFormat.INT32, order = ByteOrder.BIG_ENDIAN)
val int2 = array.getInt(offset = 4, format = IntFormat.INT32, order = ByteOrder.LITTLE_ENDIAN)
assertEquals(-2023406815, int1)
assertEquals(-2023406815, int2)
```

#### Reading Floats

Supported types include:
* IEEE 754 single and double precision
* IEEE 11073 32-bit and 16-bit

```kotlin
val array = byteArrayOf(0xFF.toByte(), 0x00, 0x01, 0x6C, 0x97.toByte(), 0x0D, 0x00, 0xFE.toByte())
val float1 = array.getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)
val float2 = array.getFloat(4, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
assertEquals(36.4f, float1)
assertEquals(34.79f, float2)
```

#### Bit operators

The library also contains extension methods for `Byte` allowing to perform bitwise operations.

```kotlin
val value = 0b1010_1001.toByte()
assertTrue(value hasAllBitsSet 0b1000_0001)
```

```kotlin
val value = 0b1010_1001.toByte()
assertTrue(value hasBitSet 0)
assertFalse(value hasBitCleared 0)
```

```kotlin
 val value = 0b1010_1001_1010_1001.toShort()
assertEquals(0b1101_0100_1101_0100.toShort(), value shr 1)
assertEquals(0b1111_1010_1001_1010.toShort(), value shr 4)
```

#### UUID

The library also contains extension methods for `ByteArray` allowing to convert UUIDs to and from `ByteArray`.

```kotlin
val uuid1 = UUID.randomUUID()
val array = uuid1.toByteArray(order = ByteOrder.BIG_ENDIAN)
val uuid2 = array.toUuid(order = ByteOrder.BIG_ENDIAN)
assertEquals(uuid, uuid2)
```
