package com.flbstv.pw.api

import com.flbstv.pw.api.plugin.Plugin

interface PluginService {
    fun plugins(): List<Plugin>
}