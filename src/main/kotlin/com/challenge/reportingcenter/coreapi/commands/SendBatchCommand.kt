package com.challenge.reportingcenter.coreapi.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class SendBatchCommand(
    @TargetAggregateIdentifier
    val batchId: UUID,
    val reportType: String,
    val reportList: List<UUID>
)