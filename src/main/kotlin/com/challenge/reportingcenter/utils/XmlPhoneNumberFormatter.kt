package com.challenge.reportingcenter.utils

interface XmlPhoneNumberFormatter {
    fun isApplicableForType(reportType: String): Boolean
    fun replacePhoneNumberValues(xmlContent: String): String
}