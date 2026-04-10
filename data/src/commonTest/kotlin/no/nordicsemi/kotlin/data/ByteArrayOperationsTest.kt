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

package no.nordicsemi.kotlin.data

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class ByteArrayOperationsTest {

    @Test
    fun longToByteArray() {
        // 0x11_22_33_44_55_66_77_88L as a signed 64-bit value is 1234605616436508552
        val value = 1234605616436508552L // 0x11_22_33_44_55_66_77_88L
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN)
        assertContentEquals(byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte()), bigEndian)
        assertContentEquals(byteArrayOf(0x88.toByte(), 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11), littleEndian)
    }

    @Test
    fun uLongToByteArray() {
        // 0x11_22_33_44_55_66_77_88L as a signed 64-bit value is 1234605616436508552
        val value = 1234605616436508552UL
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN)
        assertContentEquals(byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte()), bigEndian)
        assertContentEquals(byteArrayOf(0x88.toByte(), 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11), littleEndian)
    }

    @Test
    fun intToByteArray() {
        val value: Int = -2023406815 // 0x87_65_43_21
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN)
        assertContentEquals(byteArrayOf(0x87.toByte(), 0x65, 0x43, 0x21), bigEndian)
        assertContentEquals(byteArrayOf(0x21, 0x43, 0x65, 0x87.toByte()), littleEndian)
    }

    @Test
    fun uIntToByteArray() {
        val value = 2271560481u // 0x87_65_43_21
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN)
        assertContentEquals(byteArrayOf(0x87.toByte(), 0x65, 0x43, 0x21), bigEndian)
        assertContentEquals(byteArrayOf(0x21, 0x43, 0x65, 0x87.toByte()), littleEndian)
    }

    @Test
    fun uInt24ToByteArray() {
        val value = 0xF23456u
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN, length = IntFormat.INT24.length)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN, length = IntFormat.INT24.length)
        assertEquals(bigEndian.size, 3)
        assertEquals(littleEndian.size, 3)
        assertContentEquals(byteArrayOf(0xF2.toByte(), 0x34, 0x56), bigEndian)
        assertContentEquals(byteArrayOf(0x56, 0x34, 0xF2.toByte()), littleEndian)
    }

    @Test
    fun int16ToByteArray() {
        val value = -6000 // 0xE890
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN, length = IntFormat.INT16.length)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN, length = IntFormat.INT16.length)
        assertEquals(bigEndian.size, 2)
        assertEquals(littleEndian.size, 2)
        assertContentEquals(byteArrayOf(0xE8.toByte(), 0x90.toByte()), bigEndian)
        assertContentEquals(byteArrayOf(0x90.toByte(), 0xE8.toByte()), littleEndian)
    }

    @Test
    fun shortToByteArray() {
        val value: Short = -30875
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN)
        assertContentEquals(byteArrayOf(0x87.toByte(), 0x65), bigEndian)
        assertContentEquals(byteArrayOf(0x65, 0x87.toByte()), littleEndian)
    }

    @Test
    fun uShortToByteArray() {
        val value: UShort = 34661u
        val bigEndian = value.toByteArray(ByteOrder.BIG_ENDIAN)
        val littleEndian = value.toByteArray(ByteOrder.LITTLE_ENDIAN)
        assertContentEquals(byteArrayOf(0x87.toByte(), 0x65), bigEndian)
        assertContentEquals(byteArrayOf(0x65, 0x87.toByte()), littleEndian)
    }

    @Test
    fun byteToByteArray() {
        val value: Byte = 0x87.toByte()
        val array = value.toByteArray()
        assertContentEquals(byteArrayOf(0x87.toByte()), array)
    }

    @Test
    fun uByteToByteArray() {
        val value: UByte = 0x87u
        val array = value.toByteArray()
        assertContentEquals(byteArrayOf(0x87.toByte()), array)
    }

    @Test
    fun getLong(){
        val array = byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte())
        val long1 = array.getLong(0)
        val long2 = array.getLong(0, ByteOrder.BIG_ENDIAN)
        val long3 = array.getLong(0, ByteOrder.LITTLE_ENDIAN)
        assertEquals(1234605616436508552L, long1)
        assertEquals(1234605616436508552L, long2)
        assertEquals(-8613303245920329199L, long3)
        assertFails {
            array.getLong(7)
        }
    }

    @Test
    fun getULong(){
        val array = byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x88.toByte())
        val long1 = array.getULong(0)
        val long2 = array.getULong(0, ByteOrder.BIG_ENDIAN)
        val long3 = array.getULong(0, ByteOrder.LITTLE_ENDIAN)
        assertEquals(1234605616436508552UL, long1)
        assertEquals(1234605616436508552UL, long2)
        assertEquals(9833440827789222417UL, long3)
        assertFails {
            array.getULong(7)
        }
    }

    @Test
    fun getInt() {
        val array = byteArrayOf(0x87.toByte(), 0x65, 0x43, 0x21, 0x21, 0x43, 0x65, 0x87.toByte())
        val int1 = array.getInt(0, IntFormat.INT8)
        val int2 = array.getInt(0, IntFormat.INT16, ByteOrder.BIG_ENDIAN)
        val int3 = array.getInt(0, IntFormat.INT16, ByteOrder.LITTLE_ENDIAN)
        val int4 = array.getInt(0, IntFormat.INT24, ByteOrder.BIG_ENDIAN)
        val int5 = array.getInt(0, IntFormat.INT24, ByteOrder.LITTLE_ENDIAN)
        val int6 = array.getInt(0, IntFormat.INT32, ByteOrder.BIG_ENDIAN)
        val int7 = array.getInt(4, IntFormat.INT32, ByteOrder.LITTLE_ENDIAN)
        assertEquals(-121, int1)
        assertEquals(-30875, int2)
        assertEquals(25991, int3)
        assertEquals(-7903933, int4)
        assertEquals(4416903, int5)
        assertEquals(-2023406815, int6)
        assertEquals(-2023406815, int7)
        assertFails {
            array.getInt(7, IntFormat.INT32, ByteOrder.LITTLE_ENDIAN)
        }
    }

    @Test
    fun getUInt() {
        val array = byteArrayOf(0x87.toByte(), 0x65, 0x43, 0x21, 0x21, 0x43, 0x65, 0x87.toByte())
        val int1 = array.getInt(0, IntFormat.UINT8)
        val int2 = array.getInt(0, IntFormat.UINT16, ByteOrder.BIG_ENDIAN)
        val int3 = array.getUInt(0, IntFormat.UINT16, ByteOrder.LITTLE_ENDIAN)
        val int4 = array.getUInt(0, IntFormat.UINT24, ByteOrder.BIG_ENDIAN)
        val int5 = array.getInt(0, IntFormat.UINT24, ByteOrder.LITTLE_ENDIAN)
        val int6 = array.getUInt(0, IntFormat.UINT32, ByteOrder.BIG_ENDIAN)
        val int7 = array.getUInt(4, IntFormat.UINT32, ByteOrder.LITTLE_ENDIAN)
        assertEquals(135, int1)
        assertEquals(34661, int2)
        assertEquals(25991u, int3)
        assertEquals(8873283u, int4)
        assertEquals(4416903, int5)
        assertEquals(2271560481u, int6)
        assertEquals(2271560481u, int7)
        assertFails {
            array.getUInt(7, IntFormat.UINT32, ByteOrder.BIG_ENDIAN)
        }
    }

    @Test
    fun getShort() {
        val array = byteArrayOf(0x87.toByte(), 0x65, 0x65, 0x87.toByte())
        val short1 = array.getShort(0, ByteOrder.BIG_ENDIAN)
        val short2 = array.getShort(2, ByteOrder.LITTLE_ENDIAN)
        assertEquals((-30875).toShort(), short1)
        assertEquals((-30875).toShort(), short2)
        assertFails {
            array.getShort(3, ByteOrder.BIG_ENDIAN)
        }
    }

    @Test
    fun getUShort() {
        val array = byteArrayOf(0x87.toByte(), 0x65, 0x65, 0x87.toByte())
        val short1 = array.getUShort(0, ByteOrder.BIG_ENDIAN)
        val short2 = array.getUShort(2, ByteOrder.LITTLE_ENDIAN)
        assertEquals(34661u.toUShort(), short1)
        assertEquals(34661u.toUShort(), short2)
        assertFails {
            array.getUShort(3, ByteOrder.LITTLE_ENDIAN)
        }
    }

    @Test
    fun getFloat_IEEE754() {
        val array = byteArrayOf(0x3f, 0x80.toByte(), 0x00, 0x07, 0xdb.toByte(), 0x0f, 0x49, 0x40)
        val float1 = array.getFloat(0, FloatFormat.IEEE_754_SINGLE_PRECISION, ByteOrder.BIG_ENDIAN)
        val float2 = array.getFloat(4, FloatFormat.IEEE_754_SINGLE_PRECISION, ByteOrder.LITTLE_ENDIAN)
        assertEquals(1.0000008f, float1)
        assertEquals(3.1415927f, float2)
        assertFails {
            array.getFloat(7, FloatFormat.IEEE_754_SINGLE_PRECISION, ByteOrder.LITTLE_ENDIAN)
        }
    }

    @Test
    fun getFloat_IEEE11073_32Bit() {
        val array = byteArrayOf(0xFF.toByte(), 0x00, 0x01, 0x6C, 0x97.toByte(), 0x0D, 0x00, 0xFE.toByte())
        val float1 = array.getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)
        val float2 = array.getFloat(4, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
        assertEquals(36.4f, float1)
        assertEquals(34.79f, float2)
        assertFails {
            array.getFloat(6, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
        }
    }

    @Test
    fun getFloat_IEEE11073_32Bit_Special_BigEndian() {
        val infinity = byteArrayOf(0x00, 0x7F, 0xFF.toByte(), 0xFE.toByte()).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)
        val nan = byteArrayOf(0x00, 0x7F, 0xFF.toByte(), 0xFF.toByte()).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)
        val nres = byteArrayOf(0x00, 0x80.toByte(), 0x00, 0x00).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)
        val reserved = byteArrayOf(0x00, 0x80.toByte(), 0x00, 0x01).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)
        val minusInfinity = byteArrayOf(0x00, 0x80.toByte(), 0x00, 0x02).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.BIG_ENDIAN)

        assertEquals(Float.POSITIVE_INFINITY, infinity)
        assertEquals(Float.NaN, nan)
        assertEquals(Float.NaN, nres)
        assertEquals(Float.NaN, reserved)
        assertEquals(Float.NEGATIVE_INFINITY, minusInfinity)
    }

    @Test
    fun getFloat_IEEE11073_32Bit_Special_LittleEndian() {
        val infinity = byteArrayOf(0xFE.toByte(), 0xFF.toByte(), 0x7F, 0x00).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
        val nan = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0x7F, 0x00).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
        val nres = byteArrayOf(0x00, 0x00, 0x80.toByte(), 0x00).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
        val reserved = byteArrayOf(0x01, 0x00, 0x80.toByte(), 0x00).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)
        val minusInfinity = byteArrayOf(0x02, 0x00, 0x80.toByte(), 0x00).getFloat(0, FloatFormat.IEEE_11073_32_BIT, ByteOrder.LITTLE_ENDIAN)

        assertEquals(Float.POSITIVE_INFINITY, infinity)
        assertEquals(Float.NaN, nan)
        assertEquals(Float.NaN, nres)
        assertEquals(Float.NaN, reserved)
        assertEquals(Float.NEGATIVE_INFINITY, minusInfinity)
    }

    @Test
    fun getFloat_IEEE11073_16Bit() {
        val array = byteArrayOf(0xF1.toByte(), 0x6C, 0x97.toByte(), 0xED.toByte())
        val float1 = array.getFloat(0, FloatFormat.IEEE_11073_16_BIT, ByteOrder.BIG_ENDIAN)
        val float2 = array.getFloat(2, FloatFormat.IEEE_11073_16_BIT, ByteOrder.LITTLE_ENDIAN)
        assertEquals(36.4f, float1)
        assertEquals(34.79f, float2)
        assertFails {
            array.getFloat(6, FloatFormat.IEEE_11073_16_BIT, ByteOrder.BIG_ENDIAN)
        }
    }

    @Test
    fun getDouble() {
        val array = byteArrayOf(0x3f, 0xf0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x18, 0x2D, 0x44, 0x54, 0xfb.toByte(), 0x21, 0x09, 0x40)
        val double1 = array.getDouble(0, order = ByteOrder.BIG_ENDIAN)
        val double2 = array.getDouble(8, order = ByteOrder.LITTLE_ENDIAN)
        assertEquals(1.0, double1)
        assertEquals(3.141592653589793, double2)
        assertFails {
            array.getDouble(15, order = ByteOrder.LITTLE_ENDIAN)
        }
    }

    @Test
    fun getDouble_Special() {
        val infinity = byteArrayOf(0x7F.toByte(), 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00).getDouble(0, order = ByteOrder.BIG_ENDIAN)
        val minusInfinity = byteArrayOf(0xFF.toByte(), 0xF0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00).getDouble(0, order = ByteOrder.BIG_ENDIAN)
        assertEquals(Double.POSITIVE_INFINITY, infinity)
        assertEquals(Double.NEGATIVE_INFINITY, minusInfinity)
    }
}
