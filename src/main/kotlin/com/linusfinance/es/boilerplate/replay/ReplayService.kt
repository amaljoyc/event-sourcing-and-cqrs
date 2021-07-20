package com.linusfinance.es.boilerplate.replay

import org.axonframework.config.EventProcessingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.springframework.stereotype.Service

@Service
class ReplayService(
    private val configuration: EventProcessingConfiguration,
) {
    fun replay(name: String) {
        configuration.eventProcessor(name, TrackingEventProcessor::class.java).ifPresent { processor ->
            processor.shutDown()
            processor.resetTokens()
            processor.start()
        }
    }
}