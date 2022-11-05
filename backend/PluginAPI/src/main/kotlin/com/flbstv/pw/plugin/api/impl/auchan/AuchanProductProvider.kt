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
}