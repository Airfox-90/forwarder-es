package com.challenge.reportingcenter.controller

import com.challenge.reportingcenter.coreapi.commands.SubmitReportCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(private val commandGateway: CommandGateway) {

    @PostMapping
        (consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun submitReport(request: ReportRequest): String {
        val id = UUID.randomUUID()
        commandGateway.send<Any>(SubmitReportCommand(id, request.reportType, request.xmlContent))
        return "Report mit ID $id wurde verarbeitet"
    }
}

data class ReportRequest(val reportType: String, val xmlContent: String)
