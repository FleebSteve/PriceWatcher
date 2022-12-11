package com.flbstv.pw.product.datasource.auchan

import com.flbstv.pw.api.service.ProductDatasource
import com.flbstv.pw.api.service.ProductProvider
import org.springframework.stereotype.Service

@Service
class AuchanProudctDatasource : ProductDatasource {

    override fun getNane(): String {
        return "AUCHAN"
    }

    override fun productProvider(): ProductProvider {
        return AuchanProductProvider()
    }
}