package com.flbstv.pw.store.product.pricechange

import com.flbstv.pw.api.data.ProductPriceChange
import com.flbstv.pw.api.service.ProductPriceChangeStore
import org.springframework.stereotype.Service
import java.util.*

@Service
class MongoDBProductPriceChangeStore(private val productPriceChangeRepository: ProductPriceChangeRepository) :
    ProductPriceChangeStore {
    override fun getLastPriceChange(source: String, id: String): Optional<ProductPriceChange> {
        val productPriceChangeDocument =
            productPriceChangeRepository.findFirstBySourceAndProductIdOrderByDateDesc(source, id)
                ?: return Optional.empty()
        return Optional.of(productPriceChangeDocument.toModel())
    }

    override fun getPriceChanges(source: String, id: String): List<ProductPriceChange> {
        return productPriceChangeRepository.findBySourceAndProductId(source, id).stream().map { it.toModel() }.toList()
    }

    override fun addPriceChange(productPriceChange: ProductPriceChange) {
        productPriceChangeRepository.save(ProductPriceChangeDocument.fromModel(productPriceChange))
    }
}