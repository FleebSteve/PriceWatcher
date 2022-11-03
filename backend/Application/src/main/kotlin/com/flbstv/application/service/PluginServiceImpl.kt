package com.flbstv.application.service

import com.flbstv.pw.api.PluginService
import com.flbstv.pw.plugin.api.Plugin
import com.flbstv.pw.plugin.api.impl.spar.SparPlugin
import com.flbstv.pw.plugin.api.impl.tesco.TescoPlugin
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PluginServiceImpl: PluginService {

    val plugins: MutableMap<String, Plugin>

    init {
        plugins = HashMap()
    }

    @PostConstruct
    fun init() {
        val reflections = Reflections(ConfigurationBuilder().forPackage(""))
        val pluginImplementations = reflections.getSubTypesOf(Plugin::class.java)
        for (pluginImplementation in pluginImplementations) {
            val newInstance = pluginImplementation.getDeclaredConstructor().newInstance()
            plugins[newInstance.getNane()] = newInstance
        }
    }

    override fun plugins(): List<Plugin> {
        return plugins.values.stream().toList();
    }
}