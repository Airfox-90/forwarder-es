package com.challenge.reportingcenter.sagas

import com.challenge.reportingcenter.coreapi.commands.FormatPhoneNumberCommand
import com.challenge.reportingcenter.coreapi.events.ReportDeliveredEvent
import com.challenge.reportingcenter.coreapi.events.ReportSubmittedEvent
import io.github.oshai.kotlinlogging.KotlinLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

private val logger = KotlinLogging.logger {}

@Saga(sagaStore = "sagaStore")
class ReportSaga {

    @Autowired
    private lateinit var commandGateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "reportId")
    fun on(event: ReportSubmittedEvent) {
        logger.info { "Starting ReportSaga for Report ${event.reportId}" }
        commandGateway.send<Any>(FormatPhoneNumberCommand(event.reportId, event.reportType, event.inhalt))
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "reportId")
    fun on(event: ReportDeliveredEvent) {
        logger.info { "Terminating ReportSaga on received ReportDeliveredEvent for report ${event.reportId}" }
    }
}