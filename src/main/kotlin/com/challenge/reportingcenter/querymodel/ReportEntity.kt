package com.challenge.reportingcenter.querymodel

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class ReportEntity(
    @Id
    val id: UUID,

    val reportType: String,

    @Column(columnDefinition="TEXT")
    var inhalt: String,
    var processed: Boolean = false) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as ReportEntity

        if (id != other.id) return false
        if (reportType != other.reportType) return false
        if (inhalt != other.inhalt) return false
        if (processed != other.processed) return false

        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

