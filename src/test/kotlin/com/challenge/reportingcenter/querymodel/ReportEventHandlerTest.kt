package com.challenge.reportingcenter.querymodel

import com.challenge.reportingcenter.coreapi.events.PhoneNumberFormattedEvent
import com.challenge.reportingcenter.coreapi.events.ReportDeliveredEvent
import com.challenge.reportingcenter.coreapi.events.ReportSubmittedEvent
import com.challenge.reportingcenter.coreapi.queries.FindAllReportsById
import com.challenge.reportingcenter.repository.ReportRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.*

class ReportEventHandlerTest {

    private lateinit var repository: ReportRepository
    private lateinit var eventHandler: ReportEventHandler

    @BeforeEach
    fun setUp() {
        repository = mock(ReportRepository::class.java)
        eventHandler = ReportEventHandler(repository)
    }

    @Test
    fun `should handle ReportSubmittedEvent and save report`() {
        val reportId = UUID.randomUUID()
        val reportType = "ALARM"
        val inhalt = "<Report><Content>Feueralarm</Content></Report>"
        val event = ReportSubmittedEvent(reportId, reportType, inhalt)
        val reportEntity = ReportEntity(reportId, reportType, inhalt, false)

        eventHandler.on(event)

        verify(repository).save(eq(reportEntity))
    }

    @Test
    fun `should handle PhoneNumberFormattedEvent and update report`() {
        val reportId = UUID.randomUUID()
        val inhalt = "<Report><Content>Feueralarm</Content><PhoneNumber>+491701234567</PhoneNumber></Report>"
        val event = PhoneNumberFormattedEvent(reportId, "ALARM", inhalt)
        val reportEntity = ReportEntity(reportId, "ALARM", inhalt, false)

        `when`(repository.findById(reportId)).thenReturn(Optional.of(reportEntity))

        eventHandler.on(event)

        verify(repository).save(reportEntity)
    }

    @Test
    fun `should handle ReportDeliveredEvent and mark report as processed`() {
        val reportId = UUID.randomUUID()
        val event = ReportDeliveredEvent(reportId)
        val reportEntity = ReportEntity(reportId, "ALARM", "<Report><Content>Feueralarm</Content></Report>", false)

        `when`(repository.findById(reportId)).thenReturn(Optional.of(reportEntity))

        eventHandler.on(event)

        verify(repository).save(reportEntity.apply { processed = true })
    }

    @Test
    fun `should handle FindAllReportsById query and return reports`() {
        val reportIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val query = FindAllReportsById(reportIds)
        val reportEntities = listOf(
            ReportEntity(reportIds[0], "ALARM", "<Report><Content>Feueralarm</Content></Report>", false),
            ReportEntity(reportIds[1], "ALARM", "<Report><Content>Feueralarm</Content></Report>", true)
        )

        `when`(repository.findAllById(reportIds)).thenReturn(reportEntities)

        val result = eventHandler.handle(query)

        assert(result == reportEntities)
    }
}
