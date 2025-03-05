package com.challenge.reportingcenter.commandmodel.handlers

import com.challenge.reportingcenter.configuration.ReportProperties
import com.challenge.reportingcenter.coreapi.commands.MarkBatchCompletedCommand
import com.challenge.reportingcenter.coreapi.commands.SendBatchCommand
import com.challenge.reportingcenter.coreapi.queries.FindAllReportsById
import com.challenge.reportingcenter.querymodel.ReportEntity
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CompletableFuture

class ReportBatchCommandHandlerTest {

    private lateinit var queryGateway: QueryGateway
    private lateinit var commandGateway: CommandGateway
    private lateinit var reportProperties: ReportProperties
    private lateinit var handler: ReportBatchCommandHandler

    @BeforeEach
    fun setUp() {
        queryGateway = mockk()
        commandGateway = mockk(relaxed = true)
        reportProperties = mockk()
        handler = ReportBatchCommandHandler(queryGateway, commandGateway, reportProperties)
    }

    @Test
    fun `should handle SendBatchCommand and send MarkBatchCompletedCommand`() {
        val batchId = UUID.randomUUID()
        val reportType = "ALARM"
        val reportList = listOf(UUID.randomUUID(), UUID.randomUUID())
        val sendBatchCommand = SendBatchCommand(batchId, reportType, reportList)

        val reportEntity1 = ReportEntity(reportList[0], "gkv", "someXml")
        val reportEntity2 = ReportEntity(reportList[1], "kvpv", "someXml")

        every { queryGateway.query(FindAllReportsById(reportList), ResponseTypes.multipleInstancesOf(ReportEntity::class.java)) } returns CompletableFuture.completedFuture(listOf(reportEntity1, reportEntity2))
        every { reportProperties.types[reportType]?.endpoint } returns "http://example.com/api"

        handler.handle(sendBatchCommand)

        verify { commandGateway.send<Any>(MarkBatchCompletedCommand(batchId, reportType, reportList)) }
    }
}