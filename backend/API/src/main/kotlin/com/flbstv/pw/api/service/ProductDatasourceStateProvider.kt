package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.ProductDatasourceState
import java.time.LocalDateTime

interface ProductDatasourceStateProvider {
    fun getState(name: String): ProductDatasourceState
    fun saveState(productDatasource: ProductDatasourceState)
    fun updateState(productDatasource: ProductDatasourceState)

    fun saveLog(name: String, runId: Int, startTime: LocalDateTime, finishTime: LocalDateTime, collectedProductCount: Int)
}