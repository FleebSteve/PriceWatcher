package com.flbstv.pw.api

import com.flbstv.pw.plugin.api.model.Product

interface ProductStore {
    fun store(id: Int, product: Product)
}