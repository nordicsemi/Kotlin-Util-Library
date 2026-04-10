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

import java.nio.ByteOrder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class UuidOperationsKtTest {

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun toByteArray_BigEndian() {
        val uuid = Uuid.parse("00112233-4455-6677-8899-AABBCCDDEEFF")
        val byteArray = uuid.toByteArray(ByteOrder.BIG_ENDIAN)
        val expected = byteArrayOf(
            0x00, 0x11, 0x22, 0x33,
            0x44, 0x55, 0x66, 0x77,
            0x88.toByte(), 0x99.toByte(), 0xAA.toByte(), 0xBB.toByte(),
            0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte(),
        )
        assertContentEquals(expected, byteArray)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun toByteArray_LittleEndian() {
        val uuid = Uuid.parse("00001530-1212-EFDE-1523-785FEABCD123") // Legacy DFU Service Uuid
        val byteArray = uuid.toByteArray(ByteOrder.LITTLE_ENDIAN)
        val expected = byteArrayOf(
            0x23, 0xD1.toByte(), 0xBC.toByte(), 0xEA.toByte(),
            0x5F, 0x78, 0x23, 0x15,
            0xDE.toByte(), 0xEF.toByte(), 0x12, 0x12,
            0x30, 0x15, 0x00, 0x00,
        )
        assertContentEquals(expected, byteArray)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun getUuid_BigEndian() {
        val array = byteArrayOf(
            0x00, 0x11, 0x22, 0x33, // offset
            0x00, 0x11, 0x22, 0x33,
            0x44, 0x55, 0x66, 0x77,
            0x88.toByte(), 0x99.toByte(), 0xAA.toByte(), 0xBB.toByte(),
            0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte(),
        )
        val uuid = array.getUuid(4, ByteOrder.BIG_ENDIAN)
        val expected = Uuid.parse("00112233-4455-6677-8899-AABBCCDDEEFF")
        assertEquals(expected, uuid)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun getUuid_LittleEndian() {
        val array = byteArrayOf(
            0x00, 0x11, 0x22, 0x33, // offset
            0x23, 0xD1.toByte(), 0xBC.toByte(), 0xEA.toByte(),
            0x5F, 0x78, 0x23, 0x15,
            0xDE.toByte(), 0xEF.toByte(), 0x12, 0x12,
            0x30, 0x15, 0x00, 0x00,
        )
        val uuid = array.getUuid(4, ByteOrder.LITTLE_ENDIAN)
        val expected = Uuid.parse("00001530-1212-EFDE-1523-785FEABCD123")
        assertEquals(expected, uuid)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun toUuid_BigEndian() {
        val array = byteArrayOf(
            0x00, 0x11, 0x22, 0x33,
            0x44, 0x55, 0x66, 0x77,
            0x88.toByte(), 0x99.toByte(), 0xAA.toByte(), 0xBB.toByte(),
            0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(), 0xFF.toByte(),
        )
        val uuid = array.toUuid(ByteOrder.BIG_ENDIAN)
        val expected = Uuid.parse("00112233-4455-6677-8899-AABBCCDDEEFF")
        assertEquals(expected, uuid)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun toUuid_LittleEndian() {
        val array = byteArrayOf(
            0x23, 0xD1.toByte(), 0xBC.toByte(), 0xEA.toByte(),
            0x5F, 0x78, 0x23, 0x15,
            0xDE.toByte(), 0xEF.toByte(), 0x12, 0x12,
            0x30, 0x15, 0x00, 0x00,
        )
        val uuid = array.toUuid(ByteOrder.LITTLE_ENDIAN)
        val expected = Uuid.parse("00001530-1212-EFDE-1523-785FEABCD123")
        assertEquals(expected, uuid)
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun convertUuid() {
        val uuid1 = Uuid.random()
        val array = uuid1.toByteArray(order = ByteOrder.BIG_ENDIAN)
        val uuid2 = array.toUuid(order = ByteOrder.BIG_ENDIAN)
        assertEquals(uuid1, uuid2)

        val array2 = uuid1.toByteArray(order = ByteOrder.LITTLE_ENDIAN)
        val uuid3 = array2.toUuid(order = ByteOrder.LITTLE_ENDIAN)
        assertEquals(uuid1, uuid3)
    }
}