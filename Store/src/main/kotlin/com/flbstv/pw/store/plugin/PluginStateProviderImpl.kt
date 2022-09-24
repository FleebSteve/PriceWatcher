package com.flbstv.pw.store.plugin

import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
import org.springframework.stereotype.Service

@Service
class PluginStateProviderImpl(private val pluginRepository: PluginRepository) : PluginStateProvider {

    override fun getState(name: String): PluginState {
        if (pluginRepository.existsByName(name)) {
            var plugin = pluginRepository.findByName(name)
            return PluginState(plugin.name, plugin.state, plugin.lastRun)
        }
        return PluginState(name, PluginStatus.IDLE, null)

    }

    override fun saveState(plugin: PluginState) {
        pluginRepository.save(PluginStateData(plugin.name, plugin.status, plugin.lastRun))
    }

    override fun updateState(plugin: PluginState) {
        saveState(plugin)
    }
}