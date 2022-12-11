package com.flbstv.application.service

import com.flbstv.pw.api.service.Plugin
import com.flbstv.pw.api.service.PluginService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class PluginServiceImpl(val plugins: List<Plugin>) : PluginService {

    lateinit var pluginMap: MutableMap<String, Plugin>


    @PostConstruct
    fun init() {
        pluginMap = HashMap()
        plugins.forEach { pluginMap[it.getNane()] = it }

    }

    override fun plugins(): List<Plugin> {
        return plugins
    }

    override fun getPlugin(name: String): Plugin {
        var plugin = pluginMap[name]
        if (plugin != null) {
            return plugin
        }
        throw RuntimeException("Plugin not found: $name")
    }
}