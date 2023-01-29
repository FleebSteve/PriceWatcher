package com.flbstv.pw.data

import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductDatasourceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Service
class ImageFilenameResolver(private val productDatasourceService: ProductDatasourceService) {

    var logger: Logger = LoggerFactory.getLogger(ImageFilenameResolver::class.java)


    fun getImageFileName(product: Product): String {
        val imageUrl = productDatasourceService.getProductDatasourceByName(product.source).productProvider().imageUrl(product)
        if (imageUrl.isEmpty()) {
            logger.warn("Couldn't find image URL: ${product.source}/${product.id}")
        }
        return getFileName(imageUrl)
    }

    fun getImageUrl(product: Product): String {
        return productDatasourceService.getProductDatasourceByName(product.source).productProvider().imageUrl(product)
    }

    private fun getFileName(imageUrl: String): String {
        var file = File(imageUrl)
        var extension = file.extension

        val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset();

        messageDigest.update(imageUrl.toByteArray(StandardCharsets.UTF_8))
        val bigInt = BigInteger(1, messageDigest.digest())
        var name = bigInt.toString(48)

        if (extension.isNotEmpty()) {
            name = "$name.${extension}"
        }
        return name
    }
}