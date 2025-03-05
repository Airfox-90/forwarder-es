package com.challenge.reportingcenter.coreapi.queries

import java.util.*

data class FindAllReportsById(
    val reportIds: List<UUID>
)