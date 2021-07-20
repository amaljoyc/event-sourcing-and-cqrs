package com.linusfinance.es.boilerplate.config

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.SnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnapshotConfig {

    /**
     * creates a Snapshot after every 5 events, including the Snapshot itself.
     * eg: when we hit the 5th event, a snapshot will be created (5 events).
     *     when we hit the 9th event, another snapshot will be created (1 snapshot event + 4 events)
     *     and so on and so forth.
     */
    @Bean
    fun customerSnapshotTrigger(snapshotter: Snapshotter): SnapshotTriggerDefinition {
        return EventCountSnapshotTriggerDefinition(snapshotter, 5) // creates a snapshot after every 5 events
    }
}