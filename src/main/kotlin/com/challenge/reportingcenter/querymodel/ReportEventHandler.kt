package com.challenge.reportingcenter.querymodel

import com.challenge.reportingcenter.coreapi.events.PhoneNumberFormattedEvent
import com.challenge.reportingcenter.coreapi.events.ReportDeliveredEvent
import com.challenge.reportingcenter.coreapi.events.ReportSubmittedEvent
import com.challenge.reportingcenter.coreapi.queries.FindAllReportsById
import com.challenge.reportingcenter.repository.ReportRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
@ProcessingGroup("report")
class ReportEventHandler(private val repository: ReportRepository) {

    @EventHandler
    @Transactional
    fun on(event: ReportSubmittedEvent) {
        logger.debug { "ReportProjection received event: $event" }
        val report = ReportEntity(
            id = event.reportId,
            reportType = event.reportType,
            inhalt = event.inhalt,
            processed = false
        )
        repository.save(report)
    }

    @EventHandler
    @Transactional
    fun on(event: PhoneNumberFormattedEvent) {
        logger.debug { "ReportProjection received event: $event" }
        repository.findById(event.reportId).ifPresent(
            fun(report) {
                report.inhalt = event.inhalt
                repository.save(report)
            }
        )
    }

    @EventHandler
    @Transactional
    fun on(event: ReportDeliveredEvent) {
        logger.debug { "ReportProjection received event: $event" }
        repository.findById(event.reportId).ifPresent(
            fun(report) {
                report.processed = true
                repository.save(report)
            }
        )
    }

    @QueryHandler
    fun handle(query: FindAllReportsById): List<ReportEntity> {
        return repository.findAllById(query.reportIds)
    }
}

