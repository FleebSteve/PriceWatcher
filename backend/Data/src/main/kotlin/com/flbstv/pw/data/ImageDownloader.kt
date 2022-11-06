package com.flbstv.pw.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.flbstv.pw.api.PluginService
import com.flbstv.pw.api.const.KafkaTopics
import com.flbstv.pw.plugin.api.model.Product
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.util.StreamUtils
import org.springframework.web.client.RestTemplate
import java.io.FileOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


@Service
class ImageDownloader(private val pluginService: PluginService, private val objectMapper: ObjectMapper) {

    @Value("\${image.store.location:}")
    lateinit var storeLocation: String

    private val restTemplate: RestTemplate = RestTemplate()


    var logger: Logger = LoggerFactory.getLogger(ImageDownloader::class.java)

    @KafkaListener(topics = [KafkaTopics.PRODUCT_STREAM], groupId = "product.image.downloader")
    fun consumeProductStream(message: String) {
        val product = objectMapper.readValue(message, Product::class.java)
        val imageUrl = pluginService.getPlugin(product.source).productProvider().imageUrl(product)
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
        var name = Base64.getEncoder().encodeToString(imageUrl.encodeToByteArray())
        val ext = imageUrl.split(".")
        if (ext.isNotEmpty()) {
            name = "$name.${ext.last()}"
        }
        return name
    }
}