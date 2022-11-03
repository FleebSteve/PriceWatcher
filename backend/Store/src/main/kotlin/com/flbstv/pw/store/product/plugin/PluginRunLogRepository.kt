package com.flbstv.pw.store.product.plugin

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PluginRunLogRepository : MongoRepository<PluginRunLog, ObjectId> {
}