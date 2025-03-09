package com.challenge.reportingcenter

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportIntegrationTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun `should submit a new report successfully`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val request1: MultiValueMap<String, String> = LinkedMultiValueMap()
        request1.add("reportType", "kvpv")
        request1.add(
            "xmlContent",
            "<Report><Content>Feueralarm</Content><PhoneNumber>+49 170 1234567</PhoneNumber><ContactName>Max Mustermann</ContactName></Report>"
        )

        val entity1 = HttpEntity(request1, headers)
        val response1: ResponseEntity<String> =
            restTemplate.postForEntity("/api/v1/reports", entity1, String::class.java)

        assertEquals(HttpStatus.CREATED, response1.statusCode)
        assertTrue(response1.body!!.contains("Report submitted to"))

        val request2: MultiValueMap<String, String> = LinkedMultiValueMap()
        request2.add("reportType", "INFO")
        request2.add(
            "xmlContent",
            "<Report><Content>Information</Content><PhoneNumber>+49 171 7654321</PhoneNumber><ContactName>Erika Mustermann</ContactName></Report>"
        )

        val entity2 = HttpEntity(request2, headers)
        val response2: ResponseEntity<String> =
            restTemplate.postForEntity("/api/v1/reports", entity2, String::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response2.statusCode)
        assertTrue(response2.body!!.contains("not supported"))
    }
}