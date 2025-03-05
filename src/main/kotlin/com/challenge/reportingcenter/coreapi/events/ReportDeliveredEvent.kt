package com.challenge.reportingcenter.coreapi.events

import java.util.UUID

data class ReportDeliveredEvent(
    val reportId: UUID
)
