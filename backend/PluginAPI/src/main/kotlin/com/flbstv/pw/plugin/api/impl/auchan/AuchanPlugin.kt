package com.flbstv.pw.plugin.api.impl.auchan

import com.flbstv.pw.plugin.api.Plugin
import com.flbstv.pw.plugin.api.ProductProvider

class AuchanPlugin : Plugin {

    override fun getNane(): String {
        return "AUCHAN"
    }

    override fun productProvider(): ProductProvider {
        return AuchanProductProvider()
    }
}