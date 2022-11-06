package com.flbstv.pw.api

import com.flbstv.pw.plugin.api.Plugin

interface PluginService {
    fun plugins(): List<Plugin>

    fun getPlugin(name: String): Plugin
}