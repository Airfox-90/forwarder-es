package com.challenge.reportingcenter.configuration

import com.challenge.reportingcenter.utils.XmlPhoneNumberFormatter
import org.springframework.stereotype.Service
import java.util.stream.Stream

@Service
class XmlPhoneNumberFormatterProvider(
    val xmlPhoneNumberFormatters: Map<String, XmlPhoneNumberFormatter>
) {
    fun getFormattersForReportType(reportType: String): Stream<XmlPhoneNumberFormatter> {
        return xmlPhoneNumberFormatters.values.stream()
            .filter { formatter -> formatter.isApplicableForType(reportType) }
    }
}