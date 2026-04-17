package no.nordicsemi.kotlin.data

/**
 * Returns the value encoded in Big Endian.
 *
 * Example: 0x1234 -> 0x34120000
 */
val Int.bigEndian: Int
    get() =
        ((this and 0xFF000000.toInt()) ushr 24) or
        ((this and 0x00FF0000) ushr 8) or
        ((this and 0x0000FF00) shl 8) or
        ((this and 0x000000FF) shl 24)

/**
 * Returns the value encoded in Big Endian.
 *
 * Example: 0x1234u -> 0x34120000u
 */
val UInt.bigEndian: UInt
    get() =
        ((this and 0xFF000000u) shr 24) or
        ((this and 0x00FF0000u) shr 8) or
        ((this and 0x0000FF00u) shl 8) or
        ((this and 0x000000FFu) shl 24)

/**
 * Returns the value encoded in Big Endian.
 *
 * Example: 0x1234 -> 0x3412
 */
val Short.bigEndian: Short
    get() =
        (((this.toInt() and 0xFF00) ushr 8) or
        ((this.toInt() and 0x00FF) shl 8)).toShort()

/**
 * Returns the value encoded in Big Endian.
 *
 * Example: 0x1234 -> 0x3412
 */
val UShort.bigEndian: UShort
    get() =
        (((this.toUInt() and 0xFF00u) shr 8) or
        ((this.toUInt() and 0x00FFu) shl 8)).toUShort()

/**
 * Returns the value encoded in Big Endian.
 *
 * Example: 0x1234 -> 0x3412000000000000
 */
val Long.bigEndian: Long
    get() =
        ((this and -72057594037927936) ushr 56) or
        ((this and 0x00FF000000000000L) ushr 40) or
        ((this and 0x0000FF0000000000L) ushr 24) or
        ((this and 0x000000FF00000000L) ushr 8) or
        ((this and 0x00000000FF000000L) shl 8) or
        ((this and 0x0000000000FF0000L) shl 24) or
        ((this and 0x000000000000FF00L) shl 40) or
        ((this and 0x00000000000000FFL) shl 56)

/**
 * Returns the value encoded in Big Endian.
 *
 * Example: 0x1234 -> 0x3412000000000000
 */
val ULong.bigEndian: ULong
    get() =
        ((this and 0xFF00000000000000u) shr 56) or
        ((this and 0x00FF000000000000u) shr 40) or
        ((this and 0x0000FF0000000000u) shr 24) or
        ((this and 0x000000FF00000000u) shr 8) or
        ((this and 0x00000000FF000000u) shl 8) or
        ((this and 0x0000000000FF0000u) shl 24) or
        ((this and 0x000000000000FF00u) shl 40) or
        ((this and 0x00000000000000FFu) shl 56)