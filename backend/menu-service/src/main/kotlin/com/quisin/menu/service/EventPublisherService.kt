package com.quisin.menu.service

import com.quisin.menu.event.MenuEvent
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
    private val sink = Sinks.many().multicast().onBackpressureBuffer<Message<MenuEvent>>()

    fun publishEvent(event: MenuEvent) {
        val message = MessageBuilder.withPayload(event)
            .setHeader("eventType", event.javaClass.simpleName)
            .build()
        sink.tryEmitNext(message)
    }

    @Bean
    fun menuEvents(): Supplier<Flux<Message<MenuEvent>>> {
        return Supplier { sink.asFlux() }
    }
} 