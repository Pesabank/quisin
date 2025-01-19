package com.quisin.restaurant.service

import com.quisin.restaurant.event.ChainEvent
import com.quisin.restaurant.event.RestaurantEvent
import org.apache.kafka.streams.StreamsBuilder
import org.springframework.context.annotation.Bean
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Supplier

@Service
class EventPublisherService {
    private val restaurantEventSink = Sinks.many().multicast().onBackpressureBuffer<Message<RestaurantEvent>>()
    private val chainEventSink = Sinks.many().multicast().onBackpressureBuffer<Message<ChainEvent>>()

    fun publishEvent(event: RestaurantEvent) {
        val message = MessageBuilder.withPayload(event)
            .setHeader("eventType", event.javaClass.simpleName)
            .build()
        restaurantEventSink.tryEmitNext(message)
    }

    fun publishEvent(event: ChainEvent) {
        val message = MessageBuilder.withPayload(event)
            .setHeader("eventType", event.javaClass.simpleName)
            .build()
        chainEventSink.tryEmitNext(message)
    }

    @Bean
    fun restaurantEvents(): Supplier<Flux<Message<RestaurantEvent>>> {
        return Supplier { restaurantEventSink.asFlux() }
    }

    @Bean
    fun chainEvents(): Supplier<Flux<Message<ChainEvent>>> {
        return Supplier { chainEventSink.asFlux() }
    }
} 