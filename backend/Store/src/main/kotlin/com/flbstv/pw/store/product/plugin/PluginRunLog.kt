package com.flbstv.pw.store.product.plugin

import com.flbstv.pw.api.const.PluginStatus
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("plugin_run_log")
data class PluginRunLog(@Id val id: ObjectId,
                        val name: String,
                        val runId: Int,
                        val startTime: LocalDateTime,
                        val finishTime: LocalDateTime)