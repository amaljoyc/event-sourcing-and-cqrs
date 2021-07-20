package com.linusfinance.es.boilerplate.config

import com.linusfinance.es.boilerplate.coreapi.IntegrationEvent
import com.rabbitmq.client.Channel
import org.axonframework.eventhandling.EventMessage
import org.axonframework.extensions.amqp.eventhandling.AMQPMessageConverter
import org.axonframework.extensions.amqp.eventhandling.RoutingKeyResolver
import org.axonframework.extensions.amqp.eventhandling.spring.SpringAMQPMessageSource
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AmqpConfig {

    @Value("\${axon.amqp.exchange}")
    private lateinit var exchangeName: String

    /*
        To receive events from a queue and process them inside an Axon application,
        you need to configure a SpringAMQPMessageSource
     */
    @Bean
    fun myQueueMessageSource(messageConverter: AMQPMessageConverter): SpringAMQPMessageSource {
        return object : SpringAMQPMessageSource(messageConverter) {
            @RabbitListener(queues = [INTERNAL_Q, INCOMING_Q]) // we always listen to internal & incoming queues
            override fun onMessage(message: Message, channel: Channel) {
                super.onMessage(message, channel)
            }
        }
    }

    // create a spring amqp Exchange
    @Bean
    fun exchange(): Exchange = ExchangeBuilder.topicExchange(exchangeName).build()

    // create a Queue for routing internal Domain Events
    @Bean
    fun internalQueue(): Queue = QueueBuilder.durable(INTERNAL_Q).build()

    // create another Queue for routing outgoing Integration Events
    // feel free to create separate outgoing queues as required for each of the external domains
    @Bean
    fun outgoingQueue(): Queue = QueueBuilder.durable(OUTGOING_Q).build()

    // ideally we don't have to create the incoming queues as they would be created by the external domains
    // for now doing it here for simplicity
    @Bean
    fun incomingQueue(): Queue = QueueBuilder.durable(INCOMING_Q).build()

    // create a Binding for internalQueue & exchange
    @Bean
    fun internalBinding(internalQueue: Queue, exchange: Exchange): Binding =
        BindingBuilder.bind(internalQueue).to(exchange).with("domain").noargs()

    // create a Binding for outgoingQueue & exchange
    @Bean
    fun outgoingBinding(outgoingQueue: Queue, exchange: Exchange): Binding =
        BindingBuilder.bind(outgoingQueue).to(exchange).with("integration").noargs()

    // declare exchange, queues and bindings
    @Autowired
    fun configure(
        amqpAdmin: AmqpAdmin,
        exchange: Exchange,
        internalQueue: Queue,
        outgoingQueue: Queue,
        incomingQueue: Queue,
        internalBinding: Binding,
        outgoingBinding: Binding
    ) {
        amqpAdmin.declareExchange(exchange)
        amqpAdmin.declareQueue(internalQueue)
        amqpAdmin.declareQueue(outgoingQueue)
        amqpAdmin.declareQueue(incomingQueue) // we don't ideally have to declare the incoming queues as they are declared by external domains
        amqpAdmin.declareBinding(internalBinding)
        amqpAdmin.declareBinding(outgoingBinding)
    }

    /*
        By default, Axon Framework uses the package name of the event as the AMQP Routing Key
        its easier to rather customize it for domain vs integration event routing in our case
     */
    @Bean
    fun routingKeyResolver(): RoutingKeyResolver {
        return CustomRoutingKeyResolver()
    }
}

class CustomRoutingKeyResolver : RoutingKeyResolver {
    override fun resolveRoutingKey(event: EventMessage<*>): String {
        return if (event.payload is IntegrationEvent) {
            "integration"
        } else {
            "domain"
        }
    }
}

const val INTERNAL_Q = "internalQueue" // for internal domain events
const val OUTGOING_Q = "outgoingQueue" // for sending integration events to external/other domains
const val INCOMING_Q = "incomingQueue" // for receiving events from external/other domains