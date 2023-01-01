package com.flbstv.application.service

import com.flbstv.pw.api.service.DataReplayService
import com.flbstv.pw.api.service.ProductConsumer
import com.flbstv.pw.api.service.ProductStore
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class DataReplayServiceImpl(private val productStore: ProductStore, private val productConsumer: ProductConsumer) :
    DataReplayService {

    private var isRunning: Boolean = false

    @Async
    override fun replayProductStream() {
        isRunning = true
        productStore.findAll().forEach { productConsumer.replay(it) }
        isRunning = false
    }

    override fun isRunning(): Boolean {
        return isRunning
    }
}