package com.challenge.reportingcenter.coreapi.commands

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.UUID

data class FormatPhoneNumberCommand(
    @TargetAggregateIdentifier
    val reportId: UUID,
    val reportType: String,
    val inhalt: String,
)
