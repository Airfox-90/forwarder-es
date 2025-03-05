package com.challenge.reportingcenter.coreapi.events

import java.util.*

data class BatchSentEvent(
    val batchId: UUID,
    val reportType: String,
    val reportList: List<UUID>
)