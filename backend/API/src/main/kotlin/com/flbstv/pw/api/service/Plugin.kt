package com.flbstv.pw.api.service

interface Plugin {

    fun getNane(): String

    fun productProvider() : ProductProvider

    fun enabled(): Boolean = true
}