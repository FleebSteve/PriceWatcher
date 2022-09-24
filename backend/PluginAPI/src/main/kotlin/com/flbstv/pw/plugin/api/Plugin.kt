package com.flbstv.pw.plugin.api

interface Plugin {

    fun getNane(): String

    fun productProvider() : ProductProvider
}