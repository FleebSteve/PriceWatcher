package com.flbstv.pw.store.product.plugin

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface PluginRepository : MongoRepository<PluginStateData, ObjectId> {

    fun findByName(name: String): PluginStateData

    fun existsByName(name: String): Boolean
}