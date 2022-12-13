package com.flbstv.pw.api.service

interface ProductDatasource {

    fun getNane(): String

    fun productProvider() : ProductProvider

    fun enabled(): Boolean = true
}