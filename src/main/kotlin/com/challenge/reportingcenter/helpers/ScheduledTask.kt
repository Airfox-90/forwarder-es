package com.challenge.reportingcenter.helpers

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "scheduled_tasks")
open class ScheduledTask {
    @EmbeddedId
    open var id: ScheduledTaskId? = null

    @Column(name = "task_data")
    open var taskData: ByteArray? = null

    @Column(name = "execution_time")
    open var executionTime: OffsetDateTime? = null

    @Column(name = "picked_by", length = 50)
    open var pickedBy: String? = null

    @Column(name = "last_success")
    open var lastSuccess: OffsetDateTime? = null

    @Column(name = "last_failure")
    open var lastFailure: OffsetDateTime? = null

    @Column(name = "consecutive_failures")
    open var consecutiveFailures: Int? = null

    @Column(name = "last_heartbeat")
    open var lastHeartbeat: OffsetDateTime? = null

    @Column(name = "version")
    open var version: Long? = null

    @Column(name = "priority")
    open var priority: Int? = null

    @Column(name = "picked")
    open var picked: Boolean? = null

}