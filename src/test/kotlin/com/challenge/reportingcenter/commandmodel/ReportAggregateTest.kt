package com.challenge.reportingcenter.commandmodel

import com.challenge.reportingcenter.configuration.XmlPhoneNumberFormatterProvider
import com.challenge.reportingcenter.coreapi.commands.DeliverReportCommand
import com.challenge.reportingcenter.coreapi.commands.FormatPhoneNumberCommand
import com.challenge.reportingcenter.coreapi.commands.SubmitReportCommand
import com.challenge.reportingcenter.coreapi.events.PhoneNumberFormattedEvent
import com.challenge.reportingcenter.coreapi.events.ReportDeliveredEvent
import com.challenge.reportingcenter.coreapi.events.ReportSubmittedEvent
import com.challenge.reportingcenter.utils.XmlPhoneNumberFormatter
import io.mockk.every
import io.mockk.mockk
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ReportAggregateTest {

    private lateinit var fixture: AggregateTestFixture<ReportAggregate>
    private lateinit var xmlPhoneNumberFormatterProvider: XmlPhoneNumberFormatterProvider

    @BeforeEach
    fun setUp() {
        fixture = AggregateTestFixture(ReportAggregate::class.java)
        xmlPhoneNumberFormatterProvider = mockk()
        fixture.registerInjectableResource(xmlPhoneNumberFormatterProvider)
    }

    @Test
    fun `should handle SubmitReportCommand and apply ReportSubmittedEvent`() {
        val reportId = UUID.randomUUID()
        val reportType = "ALARM"
        val inhalt = "<Report><Content>Feueralarm</Content></Report>"
        val command = SubmitReportCommand(reportId, reportType, inhalt)

        fixture.givenNoPriorActivity()
            .`when`(command)
            .expectEvents(ReportSubmittedEvent(reportId, reportType, inhalt))
            .expectSuccessfulHandlerExecution()
    }

    @Test
    fun `should handle FormatPhoneNumberCommand and apply PhoneNumberFormattedEvent`() {
        val reportId = UUID.randomUUID()
        val reportType = "ALARM"
        val inhalt = "<Report><Content>Feueralarm</Content><PhoneNumber>+49 170 1234567</PhoneNumber></Report>"
        val formattedInhalt = "<Report><Content>Feueralarm</Content><PhoneNumber>+491701234567</PhoneNumber></Report>"
        val command = FormatPhoneNumberCommand(reportId, reportType, inhalt)

        val xmlPhoneNumberFormatter = mockk<XmlPhoneNumberFormatter>()
        every { xmlPhoneNumberFormatterProvider.getFormattersForReportType(reportType) } returns listOf<XmlPhoneNumberFormatter>(xmlPhoneNumberFormatter).stream()
        every { xmlPhoneNumberFormatter.isApplicableForType(reportType) } returns true
        every { xmlPhoneNumberFormatter.replacePhoneNumberValues(inhalt) } returns formattedInhalt

        fixture.given(ReportSubmittedEvent(reportId, reportType, inhalt))
            .`when`(command)
            .expectEvents(PhoneNumberFormattedEvent(reportId, reportType, formattedInhalt))
    }

    @Test
    fun `should handle DeliverReportCommand and apply ReportDeliveredEvent`() {
        val reportId = UUID.randomUUID()
        val reportType = "ALARM"
        val inhalt = "<Report><Content>Feueralarm</Content></Report>"
        val command = DeliverReportCommand(reportId)

        fixture.given(ReportSubmittedEvent(reportId, reportType, inhalt))
            .`when`(command)
            .expectEvents(ReportDeliveredEvent(reportId))
            .expectSuccessfulHandlerExecution()
    }
}