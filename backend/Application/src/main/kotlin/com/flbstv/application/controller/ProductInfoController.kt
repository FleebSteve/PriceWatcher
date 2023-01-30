package com.flbstv.application.controller

import com.flbstv.pw.api.data.PricePoint
import com.flbstv.pw.api.service.ProductPriceChangeStore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/product/info")
class ProductInfoController(private val  productPriceChangeStore: ProductPriceChangeStore) {

    @GetMapping("/{id}")
    fun query(@PathVariable id: String): Map<String, List<PricePoint>> {
        return productPriceChangeStore.getPricePoints(id)
    }


}