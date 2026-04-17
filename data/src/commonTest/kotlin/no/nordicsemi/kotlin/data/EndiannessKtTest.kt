package no.nordicsemi.kotlin.data

import kotlin.test.Test
import kotlin.test.assertEquals

class EndiannessKtTest {

    @Test
    fun `Int bigEndian with a positive value`() {
        // Test the byte swapping for a typical positive integer value (e.g., 0x12345678 results in 0x78563412).
        val input = 0x12345678
        val expected = 0x78563412
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Int bigEndian with a negative value`() {
        // Test the byte swapping for a negative integer to ensure that sign extension and bitwise operations are handled correctly.
        val input = -0x12345678 // Corresponds to 0xEDCBA988
        val expected = 0x88A9CBED.toInt()
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Int bigEndian with zero`() {
        // Test that an input of 0 correctly returns 0.
        val input = 0
        val expected = 0
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Int bigEndian with Int MAX VALUE`() {
        // Test the function with the maximum integer value (0x7FFFFFFF) to check for correct byte swapping at the upper boundary.
        val input = Int.MAX_VALUE // 0x7FFFFFFF
        val expected = 0xFFFFFF7F.toInt()
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Int bigEndian with Int MIN VALUE`() {
        // Test the function with the minimum integer value (0x80000000) to check for correct byte swapping at the lower boundary.
        val input = Int.MIN_VALUE // 0x80000000
        val expected = 0x00000080
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Int bigEndian with all bytes the same`() {
        // Test that an input where all bytes are identical (e.g., 0xAAAAAAAA or 0x11111111) returns itself.
        val input = 0x1A1A1A1A
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `Int bigEndian with palindromic byte pattern`() {
        // Test that an input with a palindromic byte pattern (e.g., 0x12343412) returns its swapped equivalent (0x12343412).
        val input = 0x12343412
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `Short bigEndian with a positive value`() {
        // Test the byte swapping for a typical positive short value (e.g., 0x1234 results in 0x3412).
        val input: Short = 0x1234
        val expected: Short = 0x3412
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Short bigEndian with a negative value`() {
        // Test the byte swapping for a negative short value to ensure correct handling of sign bits during conversion and shifting.
        val input: Short = -0x1234 // Corresponds to 0xEDCC
        val expected: Short = 0xCCED.toShort()
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Short bigEndian with zero`() {
        // Test that an input of 0 as a Short correctly returns 0.
        val input: Short = 0
        val expected: Short = 0
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Short bigEndian with Short MAX VALUE`() {
        // Test the function with the maximum short value (0x7FFF) to check byte swapping at the upper boundary.
        val input = Short.MAX_VALUE // 0x7FFF
        val expected: Short = 0xFF7F.toShort()
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Short bigEndian with Short MIN VALUE`() {
        // Test the function with the minimum short value (0x8000) to check byte swapping at the lower boundary.
        val input = Short.MIN_VALUE // 0x8000
        val expected: Short = 0x0080.toShort()
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Short bigEndian with all bytes the same`() {
        // Test that a short input where both bytes are identical (e.g., 0xAAAA) returns itself.
        val input: Short = 0x4242
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with a positive value`() {
        // Test the byte swapping for a typical positive long value (e.g., 0x1122334455667788 results in 0x8877665544332211).
        val input = 0x1122334455667788L
        val expected = -8613303245920329199 // 0x8877665544332211L
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with a negative value`() {
        // Test the byte swapping for a negative long value to ensure that the unsigned right shift (ushr) on the most significant byte is handled correctly.
        val input = -0x1122334455667788L // Corresponds to 0xEEDDCCBBAA998878
        val expected = 0x788899AABBCCDDEEL
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with zero`() {
        // Test that an input of 0 as a Long correctly returns 0.
        val input = 0L
        val expected = 0L
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with Long MAX VALUE`() {
        // Test the function with the maximum long value (0x7FFFFFFFFFFFFFFF) to check byte swapping at the upper boundary.
        val input = Long.MAX_VALUE // 0x7FFFFFFFFFFFFFFF
        val expected = -129L // 0xFFFFFFFFFFFFFF7F
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with Long MIN VALUE`() {
        // Test the function with the minimum long value (0x8000000000000000) to check byte swapping at the lower boundary.
        val input = Long.MIN_VALUE // 0x8000000000000000
        val expected = 0x0000000000000080L
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with all bytes the same`() {
        // Test that a long input where all bytes are identical (e.g., 0xAAAAAAAAAAAAAAAA) returns itself.
        val input = -6076574518398440533 // 0xABABABABABABABABL
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `Long bigEndian with palindromic byte pattern`() {
        // Test that a long input with a palindromic byte pattern (e.g., 0x1122334444332211) returns its swapped equivalent.
        val input = 0x1122334444332211L
        assertEquals(input, input.bigEndian)
    }
    @Test
    fun `UShort bigEndian with a positive value`() {
        // Test byte swapping for a standard UShort value.
        val input: UShort = 0x1234U
        val expected: UShort = 0x3412U
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `UShort bigEndian with zero`() {
        // Test that 0 as a UShort correctly returns 0.
        val input: UShort = 0U
        val expected: UShort = 0U
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `UShort bigEndian with UShort MAX VALUE`() {
        // Test with the maximum UShort value (0xFFFF).
        val input = UShort.MAX_VALUE // 0xFFFF
        val expected: UShort = 0xFFFFU
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `UShort bigEndian with all bytes the same`() {
        // Test that a UShort with identical bytes returns itself.
        val input: UShort = 0xABABU
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `UInt bigEndian with a positive value`() {
        // Test byte swapping for a standard UInt value.
        val input = 0x12345678U
        val expected = 0x78563412U
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `UInt bigEndian with zero`() {
        // Test that 0 as a UInt correctly returns 0.
        val input = 0U
        val expected = 0U
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `UInt bigEndian with UInt MAX VALUE`() {
        // Test with the maximum UInt value (0xFFFFFFFF).
        val input = UInt.MAX_VALUE // 0xFFFFFFFF
        val expected = 0xFFFFFFFFU
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `UInt bigEndian with all bytes the same`() {
        // Test that a UInt with identical bytes returns itself.
        val input = 0xABABABABU
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `UInt bigEndian with palindromic byte pattern`() {
        // Test a UInt with a palindromic byte pattern.
        val input = 0x12343412U
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `ULong bigEndian with a positive value`() {
        // Test byte swapping for a standard ULong value.
        val input = 0x1122334455667788UL
        val expected = 0x8877665544332211UL
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `ULong bigEndian with zero`() {
        // Test that 0 as a ULong correctly returns 0.
        val input = 0UL
        val expected = 0UL
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `ULong bigEndian with ULong MAX VALUE`() {
        // Test with the maximum ULong value (0xFFFFFFFFFFFFFFFF).
        val input = ULong.MAX_VALUE // 0xFFFFFFFFFFFFFFFF
        val expected = 0xFFFFFFFFFFFFFFFFUL
        assertEquals(expected, input.bigEndian)
    }

    @Test
    fun `ULong bigEndian with all bytes the same`() {
        // Test that a ULong with identical bytes returns itself.
        val input = 0xABABABABABABABABUL
        assertEquals(input, input.bigEndian)
    }

    @Test
    fun `ULong bigEndian with palindromic byte pattern`() {
        // Test a ULong with a palindromic byte pattern.
        val input = 0x1122334444332211UL
        assertEquals(input, input.bigEndian)
    }

}
