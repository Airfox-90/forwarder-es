package com.challenge.reportingcenter

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PhoneNumberFormatterTest {

    private val phoneUtil = PhoneNumberUtil.getInstance()

    @Test
    fun `should correctly format valid phone number`() {
        val number = phoneUtil.parse("+49 151 12345678", "DE")
        val formatted = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
        assertEquals("+4915112345678", formatted)
    }

    @Test
    fun `should throw exception for invalid phone number`() {
        assertThrows(NumberParseException::class.java) {
            phoneUtil.parse("invalid_number", "DE")
        }
    }
}
