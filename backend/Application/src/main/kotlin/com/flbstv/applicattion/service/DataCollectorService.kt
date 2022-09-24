package com.flbstv.applicattion.service

import com.flbstv.pw.api.PluginService
import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.ProductConsumer
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
import org.springframework.stereotype.Service
import java.util.*
import javax.annotation.PostConstruct

@Service
class DataCollectorService(
    private val pluginService: PluginService,
    private val pluginStateProvider: PluginStateProvider,
    private val productConsumer: ProductConsumer
) {

    @PostConstruct
    fun init() {
        for (plugin in pluginService.plugins()) {
            val state = pluginStateProvider.getState(plugin.getNane())
            if (needToRun(state)) {
                var pluginState = PluginState(plugin.getNane(), PluginStatus.RUNNING, Date())
                pluginStateProvider.saveState(pluginState)
                plugin.productProvider().getProducts().forEach { productConsumer.consume(it) }
                var finishedState = PluginState(plugin.getNane(), PluginStatus.IDLE, Date())
                pluginStateProvider.saveState(finishedState)
            }
        }
    }

    private fun needToRun(state: PluginState): Boolean {
        if (state.lastRun == null) {
            return true
        }
        var diff = Date().time - state.lastRun!!.time
        return (diff / (1000 * 60 * 60)) >= 24
    }
}