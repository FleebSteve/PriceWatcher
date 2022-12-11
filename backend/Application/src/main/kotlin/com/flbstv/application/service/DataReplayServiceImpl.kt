package com.flbstv.application.service

import com.flbstv.pw.api.service.DataReplayService
import com.flbstv.pw.api.service.ProductConsumer
import com.flbstv.pw.api.service.ProductStore
import org.springframework.stereotype.Service

@Service
class DataReplayServiceImpl(private val productStore: ProductStore, private val productConsumer: ProductConsumer) :
    DataReplayService {

    override fun replayProductStream() {
        productStore.findAll().forEach { productConsumer.replay(it) }
    }
}