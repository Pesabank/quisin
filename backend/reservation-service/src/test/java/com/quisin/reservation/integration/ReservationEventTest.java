package com.quisin.reservation.integration;

import com.quisin.reservation.config.TestConfig;
import com.quisin.reservation.dto.ReservationRequest;
import com.quisin.reservation.event.ReservationEvent;
import com.quisin.reservation.mock.RestaurantServiceMock;
import com.quisin.reservation.model.ReservationStatus;
import com.quisin.reservation.service.ReservationService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestConfig.class, RestaurantServiceMock.class})
public class ReservationEventTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, ReservationEvent> consumer;
    private static final String TOPIC = "reservation-events";
    private static final String TEST_USER_ID = "test-user";

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        JsonDeserializer<ReservationEvent> deserializer = new JsonDeserializer<>(ReservationEvent.class);
        deserializer.addTrustedPackages("com.quisin.reservation.event");
        
        DefaultKafkaConsumerFactory<String, ReservationEvent> cf = new DefaultKafkaConsumerFactory<>(
            consumerProps,
            new StringDeserializer(),
            deserializer
        );
        
        consumer = cf.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, TOPIC);
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void createReservation_ShouldPublishEvent() {
        // Given
        ReservationRequest request = createValidReservationRequest();

        // When
        reservationService.createReservation(request, TEST_USER_ID);

        // Then
        ConsumerRecord<String, ReservationEvent> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
        assertNotNull(record);
        assertNotNull(record.value());
        assertEquals("RESERVATION_CREATED", record.value().getEventType());
        assertEquals(request.getRestaurantId(), record.value().getRestaurantId());
        assertEquals(TEST_USER_ID, record.value().getUserId());
    }

    @Test
    void cancelReservation_ShouldPublishEvent() {
        // Given
        ReservationRequest request = createValidReservationRequest();
        var reservation = reservationService.createReservation(request, TEST_USER_ID);
        
        // Clear any existing records
        KafkaTestUtils.getRecords(consumer);

        // When
        reservationService.cancelReservation(reservation.getId());

        // Then
        ConsumerRecord<String, ReservationEvent> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
        assertNotNull(record);
        assertNotNull(record.value());
        assertEquals("RESERVATION_CANCELLED", record.value().getEventType());
        assertEquals(reservation.getId(), record.value().getReservationId());
        assertEquals(ReservationStatus.CANCELLED, record.value().getStatus());
    }

    private ReservationRequest createValidReservationRequest() {
        ReservationRequest request = new ReservationRequest();
        request.setRestaurantId(RestaurantServiceMock.getTestRestaurantId());
        request.setTableId(RestaurantServiceMock.getValidTableId());
        request.setPartySize(4);
        request.setReservationTime(LocalDateTime.now().plusHours(2));
        request.setSpecialRequests("No special requests");
        return request;
    }
} 