package com.flbstv.pw.api.service

interface ProductDatasourceService {
    fun productDatasourceList(): List<ProductDatasource>

    fun getProductDatasourceByName(name: String): ProductDatasource
}