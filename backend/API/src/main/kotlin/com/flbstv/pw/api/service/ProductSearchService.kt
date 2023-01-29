package com.flbstv.pw.api.service

import com.flbstv.pw.api.const.ProductOrder
import com.flbstv.pw.api.data.ProductInfo

interface ProductSearchService {

    fun search(searchTerm: String, order: ProductOrder): List<ProductInfo>

    fun suggest(searchTerm: String): List<String>

}