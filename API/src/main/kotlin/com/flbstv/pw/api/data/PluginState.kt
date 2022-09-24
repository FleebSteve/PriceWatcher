package com.flbstv.pw.api.data

import com.flbstv.pw.api.const.PluginStatus
import java.util.Date

data class PluginState(val name: String, val status: PluginStatus, val lastRun: Date?)