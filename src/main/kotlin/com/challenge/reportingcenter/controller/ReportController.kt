package com.challenge.reportingcenter.controller

import com.challenge.reportingcenter.configuration.ReportProperties
import com.challenge.reportingcenter.coreapi.commands.SubmitReportCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/reports")
class ReportController(private val commandGateway: CommandGateway, private val reportProperties: ReportProperties) {

    @PostMapping
        (consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun submitReport(request: ReportRequest): ResponseEntity<String> {
        if (!reportProperties.types.containsKey(request.reportType)) {
            return ResponseEntity<String>("Report type ${request.reportType} is not supported", HttpStatus.BAD_REQUEST)
        }
        val id = UUID.randomUUID()
        commandGateway.send<Any>(SubmitReportCommand(id, request.reportType, request.xmlContent))
        return ResponseEntity<String>("Report submitted to $id", HttpStatus.CREATED)
    }
}

data class ReportRequest(val reportType: String, val xmlContent: String)
