package com.flbstv.pw.api

import com.flbstv.pw.api.data.PluginState

interface PluginStateProvider {
    fun getState(name: String): PluginState
    fun saveState(plugin: PluginState)
    fun updateState(plugin: PluginState)
}