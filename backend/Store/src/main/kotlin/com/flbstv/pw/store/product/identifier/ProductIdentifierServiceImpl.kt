package com.flbstv.pw.store.product.identifier

import com.flbstv.pw.api.service.ProductIdentifierService
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ProductIdentifierServiceImpl(private val productIdentifierRepository: ProductIdentifierRepository) :
    ProductIdentifierService {

    override fun identify(name: String): com.flbstv.pw.api.data.ProductIdentifier? {
        return productIdentifierRepository.findByName(name)?.toModel()
    }

    override fun add(name: String, source: String, id: String) {
        val productIdentifier = productIdentifierRepository.findByName(name) ?: saveNewProductIdentifier(name, source, id)
        if (!productIdentifier.idMap.containsKey(source)) {
            productIdentifier.idMap[source] = id
            productIdentifierRepository.save(productIdentifier)
        }
    }

    private fun saveNewProductIdentifier(name: String, source: String, id: String): ProductIdentifier {
        return productIdentifierRepository.save(
            ProductIdentifier(
                ObjectId(),
                name,
                mutableMapOf(source to id)
            )
        )
    }
}