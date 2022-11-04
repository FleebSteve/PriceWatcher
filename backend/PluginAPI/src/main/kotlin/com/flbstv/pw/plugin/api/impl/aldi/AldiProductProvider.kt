package com.flbstv.pw.plugin.api.impl.aldi

import com.flbstv.pw.plugin.api.ProductProvider
import com.flbstv.pw.plugin.api.model.Product
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class AldiProductProvider : ProductProvider {

    override fun getProducts(): Stream<Product> {
        var iterator = AldiProductIterator()
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false)
    }
}