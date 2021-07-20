package com.linusfinance.es.boilerplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AxonApplication

fun main(args: Array<String>) {
    runApplication<AxonApplication>(*args)
}
