package com.flbstv.application.service

import com.flbstv.pw.api.PluginService
import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.ProductConsumer
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalUnit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class DataCollectorService(
    private val pluginService: PluginService,
    private val pluginStateProvider: PluginStateProvider,
    private val productConsumer: ProductConsumer
) {

    @PostConstruct
    fun init() {
        run()
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    fun run() {
        for (plugin in pluginService.plugins()) {
            val state = pluginStateProvider.getState(plugin.getNane())
            if (needToRun(state)) {
                var startTime = LocalDateTime.now()
                var runId = state.id + 1;
                var pluginState = PluginState(runId, plugin.getNane(), PluginStatus.RUNNING, LocalDateTime.now())
                pluginStateProvider.saveState(pluginState)
                plugin.productProvider().getProducts().forEach { productConsumer.consume(runId, it) }
                var finishTime = LocalDateTime.now()
                var finishedState = PluginState(runId, plugin.getNane(), PluginStatus.IDLE, LocalDateTime.now())
                pluginStateProvider.saveState(finishedState)
                pluginStateProvider.saveLog(plugin.getNane(), runId, startTime, finishTime)
            }
        }
    }

    private fun needToRun(state: PluginState): Boolean {
        if (state.status == PluginStatus.FAILED) {
            return true
        }
        val diffInSeconds = Duration.between(LocalDateTime.now(), state.lastRun).get(ChronoUnit.SECONDS)
        return diffInSeconds >= 24 * 60 * 60
    }
}