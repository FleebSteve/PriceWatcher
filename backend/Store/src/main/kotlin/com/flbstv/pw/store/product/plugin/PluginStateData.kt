package com.flbstv.pw.store.product.plugin

import com.flbstv.pw.api.const.PluginStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@Document("plugin_state")
data class PluginStateData(
    @Id val name: String,
    val state: PluginStatus,
    val lastRun: Date?
)