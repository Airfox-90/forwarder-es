package com.challenge.reportingcenter.configuration

import com.challenge.reportingcenter.utils.XmlPhoneNumberFormatter
import com.challenge.reportingcenter.utils.XmlByPathPhoneNumberFormatterImpl
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(ReportProperties::class)
class ReportcenterConfiguration(private val reportProperties: ReportProperties) {

    @Bean
    fun xmlPhoneNumberFormatters(): Map<String, XmlPhoneNumberFormatter> {
        val formatters = mutableMapOf<String, XmlPhoneNumberFormatter>()
        reportProperties.types.forEach { (key, value) ->
            formatters[key] = XmlByPathPhoneNumberFormatterImpl(
                listOf(key),
                value.phonenumberXpath,
                value.nodeBased
            )
        }
        return formatters
    }

    @Bean
    fun reportAggregateSnapshotTriggerDefinition(
        snapshotter: Snapshotter?,
        @Value("\${axon.aggregate.report.snapshot-threshold:250}") threshold: Int
    ): SnapshotTriggerDefinition {
        return EventCountSnapshotTriggerDefinition(snapshotter, threshold)
    }

    @Bean
    fun reportBatchAggregateSnapshotTriggerDefinition(
        snapshotter: Snapshotter?,
        @Value("\${axon.aggregate.report-batch.snapshot-threshold:250}") threshold: Int
    ): SnapshotTriggerDefinition {
        return EventCountSnapshotTriggerDefinition(snapshotter, threshold)
    }
}
