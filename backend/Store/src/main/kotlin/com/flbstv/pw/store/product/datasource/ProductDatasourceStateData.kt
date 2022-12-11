package com.flbstv.pw.store.product.datasource

import com.flbstv.pw.api.const.ProductDatasourceStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("plugin_state")
data class ProductDatasourceStateData(
    @Id val name: String,
    val state: ProductDatasourceStatus,
    val lastRun: LocalDateTime,
    val lastRunId: Int
)