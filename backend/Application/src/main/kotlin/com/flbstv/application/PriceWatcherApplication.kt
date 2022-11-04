package com.flbstv.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication(scanBasePackages = ["com.flbstv"])
@EnableScheduling
class PriceWatcherApplication

fun main(args: Array<String>) {
	runApplication<PriceWatcherApplication>(*args)
}
