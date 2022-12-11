package com.flbstv.pw.product.datasource.aldi

import com.flbstv.pw.api.service.ProductDatasource
import com.flbstv.pw.api.service.ProductProvider
import org.springframework.stereotype.Service

@Service
class AldiProductDatasource : ProductDatasource {

    override fun getNane(): String {
        return "ALDI"
    }

    override fun productProvider(): ProductProvider {
        return AldiProductProvider()
    }
}