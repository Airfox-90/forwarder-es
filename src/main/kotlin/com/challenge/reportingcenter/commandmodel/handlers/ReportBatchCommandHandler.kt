package com.challenge.reportingcenter.commandmodel.handlers
import com.challenge.reportingcenter.configuration.ReportProperties
import com.challenge.reportingcenter.coreapi.commands.MarkBatchCompletedCommand
import com.challenge.reportingcenter.coreapi.commands.SendBatchCommand
import com.challenge.reportingcenter.coreapi.queries.FindAllReportsById
import com.challenge.reportingcenter.querymodel.ReportEntity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
@EnableConfigurationProperties(ReportProperties::class)
class ReportBatchCommandHandler(
    val queryGateway: QueryGateway,
    val commandGateway: CommandGateway,
    val reportProperties: ReportProperties
) {

    @CommandHandler
    fun handle(cmd: SendBatchCommand) {
        sendToExternalSystem(cmd.reportType, cmd.reportList)
        commandGateway.send<Any>(MarkBatchCompletedCommand(cmd.batchId, cmd.reportType, cmd.reportList))
    }

    private fun sendToExternalSystem (reportType: String, batch: List<UUID>): Boolean {

        val reports = mutableMapOf<String, String>( )
        queryGateway.query(FindAllReportsById(batch), ResponseTypes.multipleInstancesOf(ReportEntity::class.java))
        .get().forEach(
            fun (report) {
                reports[report.id.toString()] = report.inhalt
            }
        )

        val endpoint = reportProperties.types[reportType]?.endpoint
        logger.info { "Sending batch of reports of type $reportType to official API $endpoint: $reports" }
        return true
    }
}