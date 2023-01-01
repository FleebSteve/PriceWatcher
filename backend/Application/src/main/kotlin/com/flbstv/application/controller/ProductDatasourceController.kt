package com.flbstv.application.controller

import com.flbstv.pw.api.const.ProductDatasourceStatus
import com.flbstv.pw.api.data.ProductDatasourceState
import com.flbstv.pw.api.service.DataReplayService
import com.flbstv.pw.api.service.ProductDatasourceService
import com.flbstv.pw.api.service.ProductDatasourceStateProvider
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/product/datasource")
class ProductDatasourceController(
    private val productDatasourceService: ProductDatasourceService,
    private val productDatasourceStateProvider: ProductDatasourceStateProvider,
    private val dataReplayService: DataReplayService
) {

    @GetMapping("fix")
    fun fix() {
        for (productDatasource in productDatasourceService.productDatasourceList()) {
            val state = productDatasourceStateProvider.getState(productDatasource.getNane())
            if (state.status == ProductDatasourceStatus.RUNNING) {
                productDatasourceStateProvider.saveState(
                    ProductDatasourceState(
                        state.id,
                        state.name,
                        ProductDatasourceStatus.FAILED,
                        state.lastRun
                    )
                )
            }
        }

    }

    @GetMapping("replay")
    fun replay() {
        dataReplayService.replayProductStream()
    }


}