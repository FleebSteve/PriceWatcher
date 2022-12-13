package com.flbstv.application.service

import com.flbstv.pw.api.const.ProductDatasourceStatus
import com.flbstv.pw.api.data.NULL_OBJECT
import com.flbstv.pw.api.data.ProductDatasourceState
import com.flbstv.pw.api.service.ProductConsumer
import com.flbstv.pw.api.service.ProductDatasource
import com.flbstv.pw.api.service.ProductDatasourceService
import com.flbstv.pw.api.service.ProductDatasourceStateProvider
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class DataCollectorService(
    private val productDatasourceService: ProductDatasourceService,
    private val productDatasourceStateProvider: ProductDatasourceStateProvider,
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
        for (productDatasource in productDatasourceService.productDatasourceList()) {
            logger.info("Checking: {}", productDatasource.getNane())
            val state = productDatasourceStateProvider.getState(productDatasource.getNane())
            if (needToRun(state) && productDatasource.enabled()) {
                run(state, productDatasource)
            }
        }
        logger.info("Check datasources finished")
    }

    private fun run(state: ProductDatasourceState, productDatasource: ProductDatasource) {
        try {
            doRun(state, productDatasource)
        } catch (e: Exception) {
            logger.error("ProductDatasource run failed", e)
            setFailedState(state, productDatasource)
        }
    }

    private fun doRun(state: ProductDatasourceState, productDatasource: ProductDatasource) {
        var startTime = LocalDateTime.now()
        var runId = state.id + 1;
        setRunningState(runId, productDatasource)
        productDatasource.productProvider().getProducts().filter { it != NULL_OBJECT }
            .forEach { productConsumer.consume(runId, it) }
        var finishTime = LocalDateTime.now()
        setFinishedState(runId, productDatasource, startTime, finishTime)
    }

    private fun setFinishedState(
        runId: Int,
        productDatasource: ProductDatasource,
        startTime: LocalDateTime,
        finishTime: LocalDateTime
    ) {
        var finishedState = ProductDatasourceState(runId, productDatasource.getNane(), ProductDatasourceStatus.IDLE, LocalDateTime.now())
        productDatasourceStateProvider.saveState(finishedState)
        productDatasourceStateProvider.saveLog(productDatasource.getNane(), runId, startTime, finishTime)
    }

    private fun setRunningState(runId: Int, productDatasource: ProductDatasource) {
        var productDatasourceState = ProductDatasourceState(runId, productDatasource.getNane(), ProductDatasourceStatus.RUNNING, LocalDateTime.now())
        productDatasourceStateProvider.saveState(productDatasourceState)
    }

    private fun setFailedState(state: ProductDatasourceState, productDatasource: ProductDatasource) {
        var finishedState = ProductDatasourceState(state.id + 1, productDatasource.getNane(), ProductDatasourceStatus.FAILED, LocalDateTime.now())
        productDatasourceStateProvider.saveState(finishedState)
    }

    private fun needToRun(state: ProductDatasourceState): Boolean {
        if (state.status == ProductDatasourceStatus.FAILED) {
            return true
        }
        val diffInSeconds = Duration.between(state.lastRun, LocalDateTime.now()).get(ChronoUnit.SECONDS)
        return diffInSeconds >= 24 * 60 * 60
    }
}