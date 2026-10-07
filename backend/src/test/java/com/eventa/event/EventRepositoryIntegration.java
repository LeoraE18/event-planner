package com.eventa.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EventRepositoryIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveAndRetrieveEventFromDatabase() {

        Event event = new Event();

        event.setName("Integration Test Event");
        event.setDate(LocalDate.of(2027, 1, 15));
        event.setLocation("Baltimore");
        event.setDescription("Testing PostgreSQL integration");
        event.setEventType("Conference");

        Event savedEvent = eventRepository.save(event);

        assertNotNull(savedEvent.getId());
        assertEquals("Integration Test Event", savedEvent.getName());
        assertEquals(LocalDate.of(2027, 1, 15), savedEvent.getDate());
    }
}