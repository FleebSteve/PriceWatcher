package com.flbstv.applicattion.service

import com.flbstv.pw.api.PriceDataSource
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PluginService {

    val priceDatasourceProviders: MutableList<PriceDataSource>

    init {
        priceDatasourceProviders = ArrayList()
    }

    @PostConstruct
    fun init() {
        val reflections = Reflections(ConfigurationBuilder().forPackage(""))
        val priceDataSourceImplementations = reflections.getSubTypesOf(PriceDataSource::class.java);
        for (priceDataSourceImplementation in priceDataSourceImplementations) {
            val newInstance = priceDataSourceImplementation.getDeclaredConstructor().newInstance()
            priceDatasourceProviders.add(newInstance)
        }
    }
}