package com.flbstv.pw.product.datasource.aldi

import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductProvider
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class AldiProductProvider(private val proxyHost: String, private val proxyPort: Int) : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = AldiProductIterator(proxyHost, proxyPort)
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }

    override fun imageUrl(product: Product): String {
        return product.raw["MediaUrlM"] as String
    }
}