package com.flbstv.pw.plugin.api.impl.tesco

import com.flbstv.pw.plugin.api.Plugin
import com.flbstv.pw.plugin.api.ProductProvider

class TescoPlugin : Plugin {

    override fun getNane(): String {
        return "TESCO"
    }

    override fun productProvider(): ProductProvider {
        return TescoProductProvider()
    }
}