package com.flbstv.pw.plugin.api.impl.tesco

import com.flbstv.pw.api.service.Plugin
import com.flbstv.pw.api.service.ProductProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TescoPlugin : Plugin {

    @Value("\${proxy.host}")
    private lateinit var proxyHost: String

    @Value("\${proxy.port}")
    private var proxyPort: Int = 0

    override fun getNane(): String {
        return "TESCO"
    }

    override fun productProvider(): ProductProvider {
        return TescoProductProvider(proxyHost, proxyPort)
    }

    override fun enabled(): Boolean {
        return true
    }
}