package com.flbstv.pw.store.product.plugin

import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PluginStateProviderImpl(
    private val pluginRepository: PluginRepository,
    private val pluginRunLogRepository: PluginRunLogRepository
) : PluginStateProvider {

    override fun getState(name: String): PluginState {
        if (pluginRepository.existsByName(name)) {
            var plugin = pluginRepository.findByName(name)
            return PluginState(plugin.lastRunId, plugin.name, plugin.state, plugin.lastRun)
        }
        return PluginState(0, name, PluginStatus.IDLE, LocalDateTime.MIN)

    }

    override fun saveState(plugin: PluginState) {
        pluginRepository.save(PluginStateData(plugin.name, plugin.status, plugin.lastRun, plugin.id))
    }

    override fun updateState(plugin: PluginState) {
        saveState(plugin)
    }

    override fun saveLog(
        name: String,
        runId: Int,
        startTime: LocalDateTime,
        finishTime: LocalDateTime
    ) {
        var pluginRunLog = PluginRunLog(ObjectId(), name, runId, startTime, finishTime)
        pluginRunLogRepository.save(pluginRunLog)
    }

}