package com.flbstv.pw.api.data

import com.flbstv.pw.api.const.PluginStatus
import java.time.LocalDateTime

data class PluginState(val id: Int, val name: String, val status: PluginStatus, val lastRun: LocalDateTime)