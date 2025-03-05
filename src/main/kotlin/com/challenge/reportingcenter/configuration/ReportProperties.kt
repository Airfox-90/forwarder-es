package com.challenge.reportingcenter.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "report")
data class ReportProperties(
    /**
     * The interval in seconds in which the reports should be sent
     */
    val sendReportIntervalSeconds: Long = 60,

    /**
     * The maximum number of reports that should be sent in one batch
     * Use -1 to disable sending batches after a certain size is reached
     */
    val maxBatchSize: Int = 10,

    /**
     * Types of reports that can be sent
     */
    val types: Map<String, ReportType>
) {
    data class ReportType(
        /**
         * The endpoint to which the reports should be sent
         */
        val endpoint: String,

        /**
         * The XPath to the phone number fields in the report
         */
        val phonenumberXpath: List<String>,

        /**
         * Whether the XPath expression should be handled node based
         */
        val nodeBased: Boolean = false
    )
}