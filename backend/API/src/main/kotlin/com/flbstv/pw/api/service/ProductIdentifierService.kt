package com.flbstv.pw.api.service

import com.flbstv.pw.api.data.ProductIdentifier

interface ProductIdentifierService {

    fun identify(name: String): ProductIdentifier?

    fun add(name: String, source: String, id: String)
}