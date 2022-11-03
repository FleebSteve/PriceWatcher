package com.flbstv.pw.api

import com.flbstv.pw.api.data.PluginState
import java.time.LocalDateTime

interface PluginStateProvider {
    fun getState(name: String): PluginState
    fun saveState(plugin: PluginState)
    fun updateState(plugin: PluginState)

    fun saveLog(name: String, runId: Int, startTime: LocalDateTime, finishTime: LocalDateTime)
}