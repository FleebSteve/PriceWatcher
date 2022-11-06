package com.flbstv.pw.plugin.api.impl.auchan

import com.flbstv.pw.plugin.api.ProductProvider
import com.flbstv.pw.plugin.api.model.Product
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class AuchanProductProvider : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = AuchanProductIterator()
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }

    override fun imageUrl(product: Product): String {
        var defaultVariant = product.raw["defaultVariant"] as Map<String, Any>
        var media = defaultVariant["media"] as Map<String, Any>
        return media["mainImage"] as String
    }
}