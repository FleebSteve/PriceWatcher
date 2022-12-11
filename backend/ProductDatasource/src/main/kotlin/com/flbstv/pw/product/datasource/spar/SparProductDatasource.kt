package com.flbstv.pw.product.datasource.spar

import com.flbstv.pw.api.service.ProductDatasource
import com.flbstv.pw.api.service.ProductProvider
import org.springframework.stereotype.Service

@Service
class SparProductDatasource : ProductDatasource {

    override fun getNane(): String {
        return "SPAR"
    }

    override fun productProvider(): ProductProvider {
        return SparProductProvider()
    }
}