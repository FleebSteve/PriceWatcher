package com.flbstv.pw.store.product.pricechange

import com.flbstv.pw.api.data.ProductPriceChange
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("product_price_change")
data class ProductPriceChangeDocument(
    @Id val id: ObjectId,
    @Indexed(unique = false) val source: String,
    @Indexed(unique = false) val productId: String,
    @Indexed(unique = false) val date: Date,
    @Indexed(unique = false) val name: String,
    val currentPrice: Double,
    val lastKnownPrice: Double,
    val firstKnownPrice: Double,
    @Indexed(unique = false) val changeSinceLastPrice: Double,
    @Indexed(unique = false) val changeSinceFirstKnownPrice: Double,
    @Indexed(unique = false) val changeSinceLastPriceRatio: Double,
    @Indexed(unique = false) val changeSinceFirstKnownPriceRatio: Double
) {
    fun toModel(): ProductPriceChange {
        return ProductPriceChange(
            source,
            productId,
            date,
            name,
            currentPrice,
            lastKnownPrice,
            firstKnownPrice,
            changeSinceLastPrice,
            changeSinceFirstKnownPrice,
            changeSinceLastPriceRatio,
            changeSinceFirstKnownPriceRatio
        )
    }

    companion object {
        fun fromModel(productPriceChange: ProductPriceChange): ProductPriceChangeDocument {
            return ProductPriceChangeDocument(
                ObjectId(),
                productPriceChange.source,
                productPriceChange.id,
                productPriceChange.date,
                productPriceChange.name,
                productPriceChange.currentPrice,
                productPriceChange.lastKnownPrice,
                productPriceChange.firstKnownPrice,
                productPriceChange.changeSinceLastPrice,
                productPriceChange.changeSinceFirstKnownPrice,
                productPriceChange.changeSinceLastPriceRatio,
                productPriceChange.changeSinceFirstKnownPriceRatio
            )
        }
    }
}
