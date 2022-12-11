package com.flbstv.pw.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.api.data.Product
import com.flbstv.pw.api.service.ProductDatasourceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import org.springframework.web.client.RestTemplate
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest


@Service
class ImageDownloader(private val productDatasourceService: ProductDatasourceService, private val objectMapper: ObjectMapper) {

    @Value("\${image.store.location:}")
    lateinit var storeLocation: String

    private val restTemplate: RestTemplate = RestTemplate()


    var logger: Logger = LoggerFactory.getLogger(ImageDownloader::class.java)

    @KafkaListener(topics = [KafkaTopics.PRODUCT_STREAM], groupId = "product.image.downloader")
    fun consumeProductStream(message: String) {
        val product = objectMapper.readValue(message, Product::class.java)
        val imageUrl = productDatasourceService.getProductDatasourceByName(product.source).productProvider().imageUrl(product)
        if (imageUrl.isEmpty()) {
            logger.warn("Couldn't find image URL: ${product.source}/${product.id}")
        }
        val fileName = getFileName(imageUrl)
        val path = Paths.get(storeLocation, fileName)
        if (!Files.exists(path)) {
            for (i in 1..5) {
                try {
                    downloadFile(path, imageUrl)
                    break
                } catch (e: Exception) {
                    logger.warn("Failed to download image: $imageUrl ${e.message}")
                }
            }
        }
    }

    private fun downloadFile(path: Path, imageUrl: String) {
        val newImageFile = path.toFile()
        restTemplate.execute(URI(imageUrl), HttpMethod.GET, null) { clientHttpResponse ->
            StreamUtils.copy(clientHttpResponse.body, FileOutputStream(newImageFile))
        }
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