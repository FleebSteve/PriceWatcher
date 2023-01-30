package com.flbstv.application.controller

import com.flbstv.pw.api.const.ProductOrder
import com.flbstv.pw.api.data.ProductInfo
import com.flbstv.pw.api.service.ProductSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/product/search")
class SearchController(private val productSearchService: ProductSearchService) {

    @GetMapping
    fun query(@RequestParam("query") query: String, @RequestParam("orderBy") orderBy: String, @RequestParam("source") source: String): List<ProductInfo> {
        return productSearchService.search(query, ProductOrder.crate(orderBy), source)
    }

    @GetMapping("suggest")
    fun suggest(@RequestParam("query") query: String): List<String> {
        return productSearchService.suggest(query)
    }
}