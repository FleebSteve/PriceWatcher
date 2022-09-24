package com.flbstv.pw.api.plugin

interface Plugin {

    fun getNane(): String

    fun productProvider() : ProductProvider
}