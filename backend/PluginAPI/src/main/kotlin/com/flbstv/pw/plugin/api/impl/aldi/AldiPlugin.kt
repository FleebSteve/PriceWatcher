package com.flbstv.pw.plugin.api.impl.aldi

import com.flbstv.pw.plugin.api.Plugin
import com.flbstv.pw.plugin.api.ProductProvider
import org.springframework.stereotype.Service

@Service
class AldiPlugin : Plugin {

    override fun getNane(): String {
        return "ALDI"
    }

    override fun productProvider(): ProductProvider {
        return AldiProductProvider()
    }
}