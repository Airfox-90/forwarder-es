package com.challenge.reportingcenter.coreapi.events

import java.util.UUID

data class PhoneNumberFormattedEvent(
    val reportId: UUID,
    val reportType: String,
    val inhalt: String
)
