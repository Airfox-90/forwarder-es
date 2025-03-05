package com.challenge.reportingcenter.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.StringReader
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource

private val logger = KotlinLogging.logger {}

class XmlByPathPhoneNumberFormatterImpl(
    private val applicableReportTypes: List<String>,
    private val phoneNumberLocations: List<String>,
    private val isNodeTypeBased: Boolean
): XmlPhoneNumberFormatter {

    override fun isApplicableForType(reportType: String): Boolean {
        return applicableReportTypes.contains(reportType)
    }

    override fun replacePhoneNumberValues(xmlContent: String): String {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(InputSource(StringReader(xmlContent)))

        for (location in phoneNumberLocations) {
            val xPath = XPathFactory.newInstance().newXPath()
            if (isNodeTypeBased) {
                val nodeList = xPath.compile(location).evaluate( doc, XPathConstants.NODESET) as NodeList

                for (i in 0 until nodeList.length) {
                    val node = nodeList.item(i)
                    formatNodeContent(node, location)
                }
            } else {
                val node = xPath.compile(location).evaluate(doc, XPathConstants.NODE) as? Node
                if (node != null) {
                    formatNodeContent(node, location)
                }
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        val writer = StringWriter()
        transformer.transform(DOMSource(doc), StreamResult(writer))

        return writer.toString()
    }

    private fun formatNodeContent(node: Node, location: String) {
        val formattedPhoneNumber = formatPhoneNumber(node.textContent)
        logger.info { "Formatting phone number ${node.textContent} at location $location to $formattedPhoneNumber" }
        node.textContent = formattedPhoneNumber
    }

    private fun formatPhoneNumber(phone: String): String {
        val phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance()
        val number = phoneUtil.parse(phone, "DE") // Annahme: Deutschland als Standardland
        return phoneUtil.format(number, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164)
    }
}