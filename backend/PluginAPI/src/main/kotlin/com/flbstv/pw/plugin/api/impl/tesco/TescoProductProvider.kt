package com.flbstv.pw.plugin.api.impl.tesco

import com.flbstv.pw.plugin.api.ProductProvider
import com.flbstv.pw.plugin.api.model.Product
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport

class TescoProductProvider : ProductProvider {

    fun init() {

    }

    override fun getProducts(): Stream<Product> {

        var iterator = TescoProductIterator()
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.DISTINCT), false);
    }
}