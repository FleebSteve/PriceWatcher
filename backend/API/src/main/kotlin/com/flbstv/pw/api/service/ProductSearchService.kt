package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.ProductInfo

interface ProductSearchService {

    fun search(searchTerm: String): List<ProductInfo>
}