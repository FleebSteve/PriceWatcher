package com.flbstv.pw.plugin.api.impl.spar

import com.flbstv.pw.plugin.api.Plugin
import com.flbstv.pw.plugin.api.ProductProvider
import org.springframework.stereotype.Service

@Service
class SparPlugin : Plugin {

    override fun getNane(): String {
        return "SPAR"
    }

    override fun productProvider(): ProductProvider {
        return SparProductProvider()
    }
}