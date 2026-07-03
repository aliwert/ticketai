package com.example.discovery_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiscoveryServerApplication

fun main(args: Array<String>) {
	runApplication<DiscoveryServerApplication>(*args)
}
