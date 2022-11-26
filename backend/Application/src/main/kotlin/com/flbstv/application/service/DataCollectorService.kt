package com.flbstv.application.service

import com.flbstv.pw.api.PluginService
import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.ProductConsumer
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
import com.flbstv.pw.plugin.api.Plugin
import com.flbstv.pw.plugin.api.model.NULL_OBJECT
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct

@Service
class DataCollectorService(
    private val pluginService: PluginService,
    private val pluginStateProvider: PluginStateProvider,
    private val productConsumer: ProductConsumer
) {

    var logger: Logger = LoggerFactory.getLogger(DataCollectorService::class.java)


    @PostConstruct
    fun init() {
        var thread = Thread { run() }
        thread.start()
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    fun run() {
        logger.info("Check datasources")
        for (plugin in pluginService.plugins()) {
            logger.info("Checking: {}", plugin.getNane())
            val state = pluginStateProvider.getState(plugin.getNane())
            if (needToRun(state) && plugin.enabled()) {
                run(state, plugin)
            }
        }
        logger.info("Check datasources finished")
    }

    private fun run(state: PluginState, plugin: Plugin) {
        try {
            doRun(state, plugin)
        } catch (e: Exception) {
            logger.error("Plugin run failed", e)
            setFailedState(state, plugin)
        }
    }

    private fun doRun(state: PluginState, plugin: Plugin) {
        var startTime = LocalDateTime.now()
        var runId = state.id + 1;
        setRunningState(runId, plugin)
        plugin.productProvider().getProducts().filter { it != NULL_OBJECT }
            .forEach { productConsumer.consume(runId, it) }
        var finishTime = LocalDateTime.now()
        setFinishedState(runId, plugin, startTime, finishTime)
    }

    private fun setFinishedState(
        runId: Int,
        plugin: Plugin,
        startTime: LocalDateTime,
        finishTime: LocalDateTime
    ) {
        var finishedState = PluginState(runId, plugin.getNane(), PluginStatus.IDLE, LocalDateTime.now())
        pluginStateProvider.saveState(finishedState)
        pluginStateProvider.saveLog(plugin.getNane(), runId, startTime, finishTime)
    }

    private fun setRunningState(runId: Int, plugin: Plugin) {
        var pluginState = PluginState(runId, plugin.getNane(), PluginStatus.RUNNING, LocalDateTime.now())
        pluginStateProvider.saveState(pluginState)
    }

    private fun setFailedState(state: PluginState, plugin: Plugin) {
        var finishedState = PluginState(state.id + 1, plugin.getNane(), PluginStatus.FAILED, LocalDateTime.now())
        pluginStateProvider.saveState(finishedState)
    }

    private fun needToRun(state: PluginState): Boolean {
        if (state.status == PluginStatus.FAILED) {
            return true
        }
        val diffInSeconds = Duration.between(state.lastRun, LocalDateTime.now()).get(ChronoUnit.SECONDS)
        return diffInSeconds >= 24 * 60 * 60
    }
}