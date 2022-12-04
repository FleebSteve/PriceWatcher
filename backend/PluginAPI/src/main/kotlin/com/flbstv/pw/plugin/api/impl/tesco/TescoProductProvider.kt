package com.flbstv.pw.plugin.api.impl.tesco

import com.flbstv.pw.plugin.api.ProductProvider
import com.flbstv.pw.plugin.api.model.Product
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class TescoProductProvider(private val proxyHost: String, private val proxyPort: Int) : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = TescoProductIterator(proxyHost, proxyPort)
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }

    override fun imageUrl(product: Product): String {
        return product.raw["defaultImageUrl"] as String
    }
}