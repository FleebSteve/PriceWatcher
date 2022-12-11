package com.flbstv.pw.plugin.api.impl.auchan

import com.flbstv.pw.api.service.Plugin
import com.flbstv.pw.api.service.ProductProvider
import org.springframework.stereotype.Service

@Service
class AuchanPlugin : Plugin {

    override fun getNane(): String {
        return "AUCHAN"
    }

    override fun productProvider(): ProductProvider {
        return AuchanProductProvider()
    }
}