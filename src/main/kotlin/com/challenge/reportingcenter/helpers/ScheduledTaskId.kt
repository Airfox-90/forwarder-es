package com.challenge.reportingcenter.helpers

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*

@Embeddable
open class ScheduledTaskId : Serializable {
    @Column(name = "task_name", nullable = false, length = 100)
    open var taskName: String? = null

    @Column(name = "task_instance", nullable = false, length = 100)
    open var taskInstance: String? = null
    override fun hashCode(): Int = Objects.hash(taskName, taskInstance)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as ScheduledTaskId

        return taskName == other.taskName &&
                taskInstance == other.taskInstance
    }

    companion object {
        private const val serialVersionUID = 8168147407566497385L
    }
}