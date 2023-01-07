package com.flbstv.pw.store.product.datasource

import com.flbstv.pw.api.const.ProductDatasourceStatus
import com.flbstv.pw.api.data.ProductDatasourceState
import com.flbstv.pw.api.service.ProductDatasourceStateProvider
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProductDatasourceStateProviderImpl(
    private val productDatasourceRepository: ProductDatasourceRepository,
    private val productDatasourceRunLogRepository: ProductDatasourceRunLogRepository
) : ProductDatasourceStateProvider {

    override fun getState(name: String): ProductDatasourceState {
        if (productDatasourceRepository.existsByName(name)) {
            var productDatasource = productDatasourceRepository.findByName(name)
            return ProductDatasourceState(productDatasource.lastRunId, productDatasource.name, productDatasource.state, productDatasource.lastRun)
        }
        return ProductDatasourceState(0, name, ProductDatasourceStatus.IDLE, LocalDateTime.MIN)

    }

    override fun saveState(productDatasource: ProductDatasourceState) {
        productDatasourceRepository.save(ProductDatasourceStateData(productDatasource.name, productDatasource.status, productDatasource.lastRun, productDatasource.id))
    }

    override fun updateState(productDatasource: ProductDatasourceState) {
        saveState(productDatasource)
    }

    override fun saveLog(
        name: String,
        runId: Int,
        startTime: LocalDateTime,
        finishTime: LocalDateTime,
        collectedProductCount: Int
    ) {
        var productDatasourceRunLog = ProductDatasourceRunLog(ObjectId(), name, runId, startTime, finishTime, collectedProductCount)
        productDatasourceRunLogRepository.save(productDatasourceRunLog)
    }

}