package com.flbstv.application.service

import com.flbstv.pw.api.PluginService
import com.flbstv.pw.api.PluginStateProvider
import com.flbstv.pw.api.ProductConsumer
import com.flbstv.pw.api.const.PluginStatus
import com.flbstv.pw.api.data.PluginState
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

    var logger: Logger =  LoggerFactory.getLogger(DataCollectorService::class.java)


    @PostConstruct
    fun init() {
        run()
    }

    @Scheduled(cron = "0 0 0/1 * * ?")
    fun run() {
        logger.info("Check datasources")
        for (plugin in pluginService.plugins()) {
            logger.info("Checking: {}", plugin.getNane())
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
        logger.info("Check datasources finished")
    }

    private fun needToRun(state: PluginState): Boolean {
        if (state.status == PluginStatus.FAILED) {
            return true
        }
        val diffInSeconds = Duration.between(state.lastRun, LocalDateTime.now()).get(ChronoUnit.SECONDS)
        return diffInSeconds >= 24 * 60 * 60
    }
}