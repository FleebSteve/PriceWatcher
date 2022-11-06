package com.flbstv.application.controller

import com.flbstv.pw.api.PluginService
import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/plugin")
class PluginController(private val pluginService: PluginService, private val pluginStateProvider: PluginStateProvider) {

    @GetMapping("fix")
    fun fix() {
        for (plugin in pluginService.plugins()) {
            val state = pluginStateProvider.getState(plugin.getNane())
            if (state.status == PluginStatus.RUNNING) {
                pluginStateProvider.saveState(PluginState(state.id, state.name, PluginStatus.FAILED, state.lastRun))
            }
        }

    }


}