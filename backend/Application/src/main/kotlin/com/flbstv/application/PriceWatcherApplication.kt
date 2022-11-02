package com.flbstv.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(scanBasePackages = ["com.flbstv"])
class PriceWatcherApplication

fun main(args: Array<String>) {
	runApplication<PriceWatcherApplication>(*args)
}
