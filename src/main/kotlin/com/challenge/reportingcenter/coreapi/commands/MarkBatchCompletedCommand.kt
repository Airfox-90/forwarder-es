package com.challenge.reportingcenter.coreapi.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class MarkBatchCompletedCommand(
    @TargetAggregateIdentifier
    val batchId: UUID,
    val reportType: String,
    val reportList: List<UUID>
)