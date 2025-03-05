package com.challenge.reportingcenter.coreapi.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class DeliverReportCommand(
    @TargetAggregateIdentifier
    val reportId: UUID
)
