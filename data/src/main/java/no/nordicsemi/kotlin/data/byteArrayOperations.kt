/*
 * Copyright (c) 2024, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

@file:Suppress("unused")

package no.nordicsemi.kotlin.data

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

/**
 * Converts a `Long` to a byte array using the given endianness.
 *
 * The optional [length] parameter allows you to specify the actual length of the field, to convert
 * 8-bit, 16-bit, 24-bit, 32-bit, 40-bit, 48-bit or 54-bit integers stored in a 'Long' to a byte
 * array.
 *
 * @param length The length of the field, in bytes, within the range of 1-[Long.SIZE_BYTES] with
 * default value equal to [Long.SIZE_BYTES] bytes.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @throws IllegalArgumentException If the length is not within the range of 1 to [Long.SIZE_BYTES].
 */
fun Long.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN, length: Int = Long.SIZE_BYTES): ByteArray {
    require(length > 0 && length <= Long.SIZE_BYTES) {
        "Length must be between 1 and ${Long.SIZE_BYTES} bytes, got $length"
    }
    return when (order) {
        ByteOrder.BIG_ENDIAN -> ByteArray(length) { (this ushr (((length - 1) * 8) - it * 8)).toByte() }
        else ->                 ByteArray(length) { (this ushr (it * 8)).toByte() }
    }
}

/**
 * Converts a `ULong` to a byte array using the given endianness.
 *
 * The optional [length] parameter allows you to specify the actual length of the field,
 * to convert 8-bit, 16-bit or 24-bit integers stored in an `UInt` to a byte array.
 * @param length The length of the field, in bytes, within the range of 1-[ULong.SIZE_BYTES] with
 * default value equal to [ULong.SIZE_BYTES] bytes.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @throws IllegalArgumentException If the length is not within the range of 1 to [ULong.SIZE_BYTES].
 */
fun ULong.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN, length: Int = ULong.SIZE_BYTES): ByteArray {
    require(length > 0 && length <= ULong.SIZE_BYTES) {
        "Length must be between 1 and ${ULong.SIZE_BYTES} bytes, got $length"
    }
    return when (order) {
        ByteOrder.BIG_ENDIAN -> ByteArray(length) { (this shr (((length - 1) * 8) - it * 8)).toByte() }
        else -> ByteArray(length) { (this shr (it * 8)).toByte() }
    }
}

/**
 * Converts an `Int` to a byte array using the given endianness.
 *
 * The optional [length] parameter allows you to specify the actual length of the field,
 * to convert 8-bit, 16-bit or 24-bit integers stored in an `Int` to a byte array.
 * @param length The length of the field, in bytes, within the range of 1-[Int.SIZE_BYTES] with
 * default value equal to [Int.SIZE_BYTES] bytes.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @throws IllegalArgumentException If the length is not within the range of 1 to [Int.SIZE_BYTES].
 */
fun Int.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN, length: Int = Int.SIZE_BYTES): ByteArray {
    require(length > 0 && length <= Int.SIZE_BYTES) {
        "Length must be between 1 and ${Int.SIZE_BYTES} bytes, got $length"
    }
    return when (order) {
        ByteOrder.BIG_ENDIAN -> ByteArray(length) { (this ushr (((length - 1) * 8) - it * 8)).toByte() }
        else ->                 ByteArray(length) { (this ushr (it * 8)).toByte() }
    }
}

/**
 * Converts an `UInt` to a byte array using the given endianness.
 *
 * The optional [length] parameter allows you to specify the actual length of the field,
 * to convert 8-bit, 16-bit or 24-bit integers stored in an `UInt` to a byte array.
 * @param length The length of the field, in bytes, within the range of 1-[UInt.SIZE_BYTES] with
 * default value equal to [UInt.SIZE_BYTES] bytes.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @throws IllegalArgumentException If the length is not within the range of 1 to [UInt.SIZE_BYTES].
 */
fun UInt.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN, length: Int = UInt.SIZE_BYTES): ByteArray {
    require(length > 0 && length <= UInt.SIZE_BYTES) {
        "Length must be between 1 and ${UInt.SIZE_BYTES} bytes, got $length"
    }
    return when (order) {
        ByteOrder.BIG_ENDIAN -> ByteArray(length) { (this shr (((length - 1) * 8) - it * 8)).toByte() }
        else -> ByteArray(length) { (this shr (it * 8)).toByte() }
    }
}

/**
 * Converts a Short to a byte array using the given endianness.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 */
fun Short.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN) = when (order) {
    ByteOrder.BIG_ENDIAN -> ByteArray(2) { (this ushr (8 - it * 8)).toByte() }
    else ->                 ByteArray(2) { (this ushr (it * 8)).toByte() }
}

/**
 * Converts a UShort to a byte array using the given endianness.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 */
fun UShort.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN) = when (order) {
    ByteOrder.BIG_ENDIAN -> ByteArray(2) { (this shr (8 - it * 8)).toByte() }
    else ->                 ByteArray(2) { (this shr (it * 8)).toByte() }
}

/**
 * Converts a Byte to a byte array.
 */
fun Byte.toByteArray() = ByteArray(1) { this }

/**
 * Converts a UByte to a byte array.
 */
fun UByte.toByteArray() = ByteArray(1) { this.toByte() }

//--------------------------------------------------------------------------------------------------

/**
 * Integer format enumeration.
 */
enum class IntFormat {
    /** 8-bit unsigned integer. */
    UINT8,
    /** 16-bit unsigned integer. */
    UINT16,
    /** 24-bit unsigned integer. */
    UINT24,
    /** 32-bit unsigned integer. */
    UINT32,
    /** 8-bit signed integer. */
    INT8,
    /** 16-bit signed integer. */
    INT16,
    /** 24-bit signed integer. */
    INT24,
    /** 32-bit signed integer. */
    INT32;

    /**
     * The length of the format in bytes.
     */
    val length: Int
        get() = when (this) {
            UINT8, INT8 -> 1
            UINT16, INT16 -> 2
            UINT24, INT24 -> 3
            UINT32, INT32 -> 4
        }
}

/**
 * Floating point format enumeration.
 */
enum class FloatFormat {
    /** 32-bit IEEE 754 floating point. */
    IEEE_754_SINGLE_PRECISION,
    /** 32-bit IEEE 11073-20601 floating point. */
    IEEE_11073_32_BIT,
    /** 16-bit IEEE 11073-20601 floating point. */
    IEEE_11073_16_BIT;

    /**
     * The length of the format in bytes.
     */
    val length: Int
        get() = when (this) {
            IEEE_754_SINGLE_PRECISION, IEEE_11073_32_BIT -> 4
            IEEE_11073_16_BIT -> 2
        }
}

/**
 * Double precision floating point format enumeration.
 */
enum class DoubleFormat {
    /** 64-bit IEEE 754 floating point. */
    IEEE_754_DOUBLE_PRECISION;

    /**
     * The length of the format in bytes.
     */
    val length: Int = 8
}

/**
 * Returns an Int from a byte array with a given offset.
 *
 * @param offset The index to start from.
 * @param format The format or the Int value to read.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return Int.
 * @throws IllegalArgumentException If the length of byte array is less than the offset plus
 * format length.
 */
fun ByteArray.getInt(
    offset: Int,
    format: IntFormat,
    order: ByteOrder = ByteOrder.BIG_ENDIAN
): Int {
    require(offset >= 0 && size >= offset + format.length) {
        throw IndexOutOfBoundsException("Cannot return an Int from an array of size $size from offset $offset")
    }
    return when (format) {
        IntFormat.UINT8 -> this[offset].toInt() and 0xFF
        IntFormat.UINT16 -> ByteBuffer.wrap(this, offset, format.length).order(order).short.toInt() and 0xFFFF
        IntFormat.UINT24 -> when (order) {
            ByteOrder.BIG_ENDIAN ->
                (this[offset + 0].toInt() and 0xFF shl 16) or
                (this[offset + 1].toInt() and 0xFF shl 8) or
                (this[offset + 2].toInt() and 0xFF)
            else ->
                (this[offset + 2].toInt() and 0xFF shl 16) or
                (this[offset + 1].toInt() and 0xFF shl 8) or
                (this[offset + 0].toInt() and 0xFF)
        }
        IntFormat.UINT32 -> ByteBuffer.wrap(this, offset, format.length).order(order).int
        IntFormat.INT8 -> this[offset].toInt()
        IntFormat.INT16 -> ByteBuffer.wrap(this, offset, format.length).order(order).short.toInt()
        IntFormat.INT24 -> when (order) {
            ByteOrder.BIG_ENDIAN ->
                (this[offset + 0].toInt() shl 16) or
                (this[offset + 1].toInt() and 0xFF shl 8) or
                (this[offset + 2].toInt() and 0xFF)
            else ->
                (this[offset + 2].toInt() shl 16) or
                (this[offset + 1].toInt() and 0xFF shl 8) or
                (this[offset + 0].toInt() and 0xFF)
        }
        IntFormat.INT32 -> ByteBuffer.wrap(this, offset, format.length).order(order).int
    }
}

/**
 * Returns an Int from a byte array with a given offset.
 *
 * @param offset The index to start from.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return Int.
 * @throws IllegalArgumentException If the length of byte array is less than offset + 4.
 */
fun ByteArray.getInt(
    offset: Int,
    order: ByteOrder = ByteOrder.BIG_ENDIAN
): Int {
    return getInt(offset, IntFormat.INT32, order)
}

/**
 * Returns an Int from a byte array with a given offset.
 *
 * @param offset The index to start from.
 * @param format The format or the Int value to read.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return UInt.
 * @throws IllegalArgumentException If the length of byte array is less than offset + 4.
 */
fun ByteArray.getUInt(
    offset: Int,
    format: IntFormat = IntFormat.UINT32,
    order: ByteOrder = ByteOrder.BIG_ENDIAN
): UInt {
    require(offset >= 0 && size >= offset + format.length) {
        throw IndexOutOfBoundsException("Cannot return an UInt from an array of size $size from offset $offset")
    }
    return getInt(offset, format, order).toUInt()
}

/**
 * Returns an Int from a byte array with a given offset.
 *
 * @param offset The index to start from.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return Short.
 * @throws IndexOutOfBoundsException If the length of the byte array is less than offset + [Short.SIZE_BYTES].
 */
fun ByteArray.getShort(offset: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): Short {
    require(offset >= 0 && size >= offset + Short.SIZE_BYTES) {
        throw IndexOutOfBoundsException("Cannot return a Short from an array of size $size from offset $offset")
    }
    return ByteBuffer.wrap(this, offset, Short.SIZE_BYTES).order(order).short
}

/**
 * Returns an Int from a byte array with a given offset.
 *
 * @param offset The index to start from.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return UShort.
 * @throws IndexOutOfBoundsException If the length of the byte array is less than offset + [UShort.SIZE_BYTES].
 */
fun ByteArray.getUShort(offset: Int, order: ByteOrder = ByteOrder.BIG_ENDIAN): UShort {
    require(offset >= 0 && size >= offset + UShort.SIZE_BYTES) {
        throw IndexOutOfBoundsException("Cannot return a UShort from an array of size $size from offset $offset")
    }
    return ByteBuffer.wrap(this, offset, UShort.SIZE_BYTES).order(order).short.toUShort()
}

/**
 * Returns a Float value from a byte array with a given offset.
 *
 * @param offset The index to start from.
 * @param format The format or the Float value to read.
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return Float.
 * @throws IndexOutOfBoundsException If the length of the byte array is shorter than offset plus
 * the format length.
 */
fun ByteArray.getFloat(
    offset: Int,
    format: FloatFormat,
    order: ByteOrder = ByteOrder.BIG_ENDIAN
): Float {
    require(offset >= 0 && size >= offset + format.length) {
        throw IndexOutOfBoundsException("Cannot return a $format Float from an array of size $size from offset $offset")
    }
    return when (format) {
        FloatFormat.IEEE_754_SINGLE_PRECISION -> ByteBuffer.wrap(this, offset, format.length).order(order).float
        FloatFormat.IEEE_11073_32_BIT -> {
            val raw = getInt(offset, IntFormat.INT32, order)
            // The following information is defined in https://www.iso.org/standard/84781.html
            when (raw) {
                0x7FFFFE -> return Float.POSITIVE_INFINITY
                0x7FFFFF, 0x800000, 0x800001 -> return Float.NaN
                0x800002 -> return Float.NEGATIVE_INFINITY
            }
            val mantissa = raw and 0x00FFFFFF // unsigned
            val exponent = raw shr 24 // signed
            val value = mantissa * 10.0.pow(exponent)
            value.toFloat()
        }
        FloatFormat.IEEE_11073_16_BIT -> {
            val raw = getInt(offset, IntFormat.INT16, order)
            // The following information is defined in https://www.iso.org/standard/84781.html
            when (raw) {
                0x7FE -> return Float.POSITIVE_INFINITY
                0x7FF, 0x800, 0x801 -> return Float.NaN
                0x802 -> return Float.NEGATIVE_INFINITY
            }
            val mantissa = raw and 0x0FFF // unsigned
            val exponent = raw shr 12 // signed
            val value = mantissa * 10.0.pow(exponent)
            value.toFloat()
        }
    }
}

/**
 * Returns a Double value from a byte array with a given offset.
 *
 * @param offset The index to start from.format
 * @param order The byte order, default is [ByteOrder.BIG_ENDIAN].
 * @return Double.
 * @throws IndexOutOfBoundsException If the length of the byte array is less than offset + 8.
 */
fun ByteArray.getDouble(
    offset: Int,
    format: DoubleFormat = DoubleFormat.IEEE_754_DOUBLE_PRECISION,
    order: ByteOrder = ByteOrder.BIG_ENDIAN
): Double {
    require(offset >= 0 && size >= offset + format.length) {
        throw IndexOutOfBoundsException("Cannot return a Double from an array of size $size from offset $offset")
    }
    return when (format) {
        DoubleFormat.IEEE_754_DOUBLE_PRECISION -> ByteBuffer.wrap(this, offset, format.length).order(order).double
    }
}