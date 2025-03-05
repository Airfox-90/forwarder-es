package com.challenge.reportingcenter.commandmodel

import com.challenge.reportingcenter.coreapi.commands.MarkBatchCompletedCommand
import com.challenge.reportingcenter.coreapi.events.BatchSentEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

private val logger = KotlinLogging.logger {}

@Aggregate(snapshotTriggerDefinition = "reportBatchAggregateSnapshotTriggerDefinition")
class ReportBatchAggregate() {

    @AggregateIdentifier
    private lateinit var batchId: UUID

    private lateinit var reportType: String
    private var batchSendComplete: Boolean = false

    @CommandHandler
    constructor(command: MarkBatchCompletedCommand) : this() {
        logger.info { "Creating new ReportBatchAggregate on MarkBatchCompletedCommand for batch ${command.batchId}" }
        batchId = command.batchId
        reportType = command.reportType
        AggregateLifecycle.apply(BatchSentEvent(command.batchId, command.reportType, command.reportList))
    }

    @EventSourcingHandler
    fun on(event: BatchSentEvent) {
        logger.info { "ReportBatchAggregate received BatchSentEvent for batch: ${event.batchId}" }
        this.batchSendComplete = true
    }
}
