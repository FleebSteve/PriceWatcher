package com.flbstv.application.service

import com.flbstv.pw.api.service.ProductDatasource
import com.flbstv.pw.api.service.ProductDatasourceService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class ProductDatasourceServiceImpl(val productDatasourceList: List<ProductDatasource>) : ProductDatasourceService {

    var productDatasourceMap: MutableMap<String, ProductDatasource> = HashMap()


    @PostConstruct
    fun init() {
        productDatasourceList.forEach { productDatasourceMap[it.getNane()] = it }

    }

    override fun productDatasourceList(): List<ProductDatasource> {
        return productDatasourceList
    }

    override fun getProductDatasourceByName(name: String): ProductDatasource {
        var productDatasource = productDatasourceMap[name]
        if (productDatasource != null) {
            return productDatasource
        }
        throw RuntimeException("ProductDatasource not found: $name")
    }
}