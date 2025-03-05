package com.challenge.reportingcenter.commandmodel

import com.challenge.reportingcenter.configuration.XmlPhoneNumberFormatterProvider
import com.challenge.reportingcenter.coreapi.commands.DeliverReportCommand
import com.challenge.reportingcenter.coreapi.commands.FormatPhoneNumberCommand
import com.challenge.reportingcenter.coreapi.commands.SubmitReportCommand
import com.challenge.reportingcenter.coreapi.events.PhoneNumberFormattedEvent
import com.challenge.reportingcenter.coreapi.events.ReportDeliveredEvent
import com.challenge.reportingcenter.coreapi.events.ReportSubmittedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

private val logger = KotlinLogging.logger {}

@Aggregate(snapshotTriggerDefinition = "reportAggregateSnapshotTriggerDefinition")
class ReportAggregate() {

    var phoneNumberFormatted: Boolean = false
    var reportDelivered: Boolean = false

    @AggregateIdentifier
    private lateinit var reportId: UUID

    private lateinit var reportType: String

    @CommandHandler
    constructor(command: SubmitReportCommand) : this() {
        logger.info { "Creating new ReportAggregate on received command SubmitReportCommand for reportID ${command.reportId} and type ${command.reportType}}" }
        AggregateLifecycle.apply(
            ReportSubmittedEvent(
                reportId = command.reportId,
                reportType = command.reportType,
                inhalt = command.inhalt
            )
        )
    }

    @CommandHandler
    fun handle(command: FormatPhoneNumberCommand, xmlPhoneNumberFormatterProvider: XmlPhoneNumberFormatterProvider) {
        logger.info { "ReportAggregate received command FormatPhoneNumberCommand for reportID: ${command.reportId}" }
        if (phoneNumberFormatted) {
            return
        }
        var xmlContent = command.inhalt
        xmlPhoneNumberFormatterProvider.getFormattersForReportType(command.reportType)
            .forEach { replacer -> xmlContent = replacer.replacePhoneNumberValues(xmlContent) }
        AggregateLifecycle.apply(PhoneNumberFormattedEvent(reportId, reportType, xmlContent))
    }

    @CommandHandler
    fun handle(command: DeliverReportCommand) {
        logger.info { "ReportAggregate received DeliverReportCommand for reportId: ${command.reportId}" }
        AggregateLifecycle.apply(ReportDeliveredEvent(reportId))
    }


    @EventSourcingHandler
    fun on(event: ReportSubmittedEvent) {
        logger.debug { "Received event: $event" }
        this.reportId = event.reportId
        this.reportType = event.reportType
    }

    @EventSourcingHandler
    fun on(event: PhoneNumberFormattedEvent) {
        logger.debug { "Received event: $event" }
        this.phoneNumberFormatted = true
    }

    @EventSourcingHandler
    fun on(event: ReportDeliveredEvent) {
        logger.debug { "Received event: $event" }
        this.reportDelivered = true
    }
}
