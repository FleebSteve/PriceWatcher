package com.flbstv.pw.store.product.datasource

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("plugin_run_log")
data class ProductDatasourceRunLog(@Id val id: ObjectId,
                                   val name: String,
                                   val runId: Int,
                                   val startTime: LocalDateTime,
                                   val finishTime: LocalDateTime)