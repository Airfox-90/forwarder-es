package com.challenge.reportingcenter.repository

import com.challenge.reportingcenter.querymodel.ReportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ReportRepository : JpaRepository<ReportEntity, UUID>
