package com.flbstv.pw.plugin.api.impl.auchan

import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductProvider
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
        var mainImage = media["mainImage"]
        return if (mainImage != null) {
            media["mainImage"] as String
        } else {
            ""
        }
    }
}