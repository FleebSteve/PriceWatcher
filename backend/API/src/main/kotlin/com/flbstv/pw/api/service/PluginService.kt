package com.flbstv.pw.api.service

interface PluginService {
    fun plugins(): List<Plugin>

    fun getPlugin(name: String): Plugin
}