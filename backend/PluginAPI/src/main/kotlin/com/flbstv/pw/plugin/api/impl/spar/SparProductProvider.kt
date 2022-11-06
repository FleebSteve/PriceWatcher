package com.flbstv.pw.plugin.api.impl.spar

import com.flbstv.pw.plugin.api.ProductProvider
import com.flbstv.pw.plugin.api.model.Product
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class SparProductProvider : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = SparProductIterator()
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }

    override fun imageUrl(product: Product): String {
        return product.raw["image-url"] as String
    }
}