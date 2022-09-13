package com.flbstv.applicattion

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PriceWatcherApplication

fun main(args: Array<String>) {
	runApplication<PriceWatcherApplication>(*args)
}
