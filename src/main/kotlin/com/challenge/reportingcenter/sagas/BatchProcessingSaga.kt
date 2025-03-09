package com.challenge.reportingcenter.sagas

import com.challenge.reportingcenter.configuration.ReportProperties
import com.challenge.reportingcenter.coreapi.commands.DeliverReportCommand
import com.challenge.reportingcenter.coreapi.commands.SendBatchCommand
import com.challenge.reportingcenter.coreapi.events.BatchSentEvent
import com.challenge.reportingcenter.coreapi.events.PhoneNumberFormattedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.deadline.DeadlineManager
import org.axonframework.deadline.annotation.DeadlineHandler
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.util.*

private val logger = KotlinLogging.logger {}

@Saga(sagaStore = "sagaStore")
class BatchProcessingSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var deadlineManager: DeadlineManager

    @Autowired
    private lateinit var reportProperties: ReportProperties

    val reportList: MutableList<UUID> = mutableListOf()

    var timerId: String? = null

    var reportType: String? = null

    var batchId: UUID? = null

    @StartSaga(forceNew = false)
    @SagaEventHandler(associationProperty = "reportType")
    fun on(event: PhoneNumberFormattedEvent) {
        logger.info { "Starting/joining BatchProcessingSaga for report type: ${event.reportType}" }
        reportType = event.reportType
        reportList.add(event.reportId)
        logger.info { "ReportList size: ${reportList.size}" }

        // If this is the first report, schedule the timer
        if (reportList.size == 1) {
            timerId = deadlineManager.schedule(Duration.ofSeconds(reportProperties.sendReportIntervalSeconds), "batchDeadline")
        }
        if (reportProperties.maxBatchSize > 0 &&  reportList.size >= reportProperties.maxBatchSize) {
            sendBatch()
            resetSaga()
            timerId?.let { deadlineManager.cancelAll(it) }
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "batchId")
    fun on(event: BatchSentEvent) {
        logger.info { "Terminating BatchProcessingSaga on received BatchSentEvent for batchId: ${event.batchId}" }
        event.reportList.forEach {
            commandGateway.send<Any>(DeliverReportCommand(it))
        }
    }

    @DeadlineHandler(deadlineName = "batchDeadline")
    fun onDeadline() {
        logger.info { "BatchProcessingSaga deadline reached for sending next batch. Sending ${reportList.size} reports of type $reportType" }
        sendBatch()
        resetSaga()
    }

    private fun sendBatch() {
        if (reportList.isNotEmpty()) {
            SagaLifecycle.removeAssociationWith("reportType", reportType)
            batchId = UUID.randomUUID()
            SagaLifecycle.associateWith("batchId", batchId.toString())
            commandGateway.send<Any>(SendBatchCommand(batchId!! ,reportType!!,reportList))
        } else {
            SagaLifecycle.end()
        }
    }

    private fun resetSaga() {
        timerId = null
    }
}
