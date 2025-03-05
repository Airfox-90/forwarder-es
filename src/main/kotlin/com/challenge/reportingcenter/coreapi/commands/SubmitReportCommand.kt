package com.challenge.reportingcenter.coreapi.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class SubmitReportCommand(
    @TargetAggregateIdentifier
    val reportId: UUID,
    val reportType: String,
    val inhalt: String,
)
