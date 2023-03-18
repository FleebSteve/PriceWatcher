package com.flbstv.pw.product.datasource.aldi

import com.flbstv.pw.api.service.ProductDatasource
import com.flbstv.pw.api.service.ProductProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AldiProductDatasource : ProductDatasource {

    @Value("\${proxy.host}")
    private lateinit var proxyHost: String

    @Value("\${proxy.port}")
    private var proxyPort: Int = 0

    override fun getNane(): String {
        return "ALDI"
    }

    override fun productProvider(): ProductProvider {
        return AldiProductProvider(proxyHost, proxyPort)
    }
}