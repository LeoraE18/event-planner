package com.eventa.event;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventServiceTest {

    @Test
    void createEventShouldSaveEvent() {

        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);

        Event event = new Event();
        event.setName("Test Event");
        event.setDate("2026-12-01");
        event.setLocation("Baltimore");
        event.setDescription("A test event");
        event.setEventType("Party");

        Mockito.when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.createEvent(event);

        assertNotNull(result);
        assertEquals("Test Event", result.getName());
        assertEquals("Party", result.getEventType());

        Mockito.verify(eventRepository).save(event);
    }

    @Test
    void getEventByIdShouldReturnEvent() {

        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);

        Event event = new Event();
        event.setName("Test Wedding");
        event.setDate("2026-12-20");
        event.setEventType("Wedding");

        Mockito.when(eventRepository.findById(1L))
                .thenReturn(java.util.Optional.of(event));

        Event result = eventService.getEventById(1L);

        assertNotNull(result);
        assertEquals("Test Wedding", result.getName());
        assertEquals("Wedding", result.getEventType());

        Mockito.verify(eventRepository).findById(1L);
    }

    @Test
    void updateEventShouldUpdateExistingEvent() {

        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);

        Event existingEvent = new Event();
        existingEvent.setName("Old Event");
        existingEvent.setDate("2026-12-01");
        existingEvent.setLocation("Baltimore");
        existingEvent.setDescription("Old description");
        existingEvent.setEventType("Party");

        Event updatedDetails = new Event();
        updatedDetails.setName("Updated Event");
        updatedDetails.setDate("2026-12-15");
        updatedDetails.setLocation("New York");
        updatedDetails.setDescription("Updated description");
        updatedDetails.setEventType("Wedding");

        Mockito.when(eventRepository.findById(1L))
                .thenReturn(java.util.Optional.of(existingEvent));

        Mockito.when(eventRepository.save(existingEvent))
                .thenReturn(existingEvent);

        Event result = eventService.updateEvent(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Event", result.getName());
        assertEquals("2026-12-15", result.getDate());
        assertEquals("New York", result.getLocation());
        assertEquals("Wedding", result.getEventType());

        Mockito.verify(eventRepository).findById(1L);
        Mockito.verify(eventRepository).save(existingEvent);
    }

    @Test
    void getEventByIdShouldThrowExceptionWhenEventDoesNotExist() {

        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);

        Mockito.when(eventRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> eventService.getEventById(999L)
        );

        assertEquals("Event not found", exception.getMessage());

        Mockito.verify(eventRepository).findById(999L);
    }

    @Test
    void deleteEventShouldDeleteExistingEvent() {

        EventRepository eventRepository = Mockito.mock(EventRepository.class);
        EventService eventService = new EventService(eventRepository);

        Event event = new Event();
        event.setName("Event to Delete");

        Mockito.when(eventRepository.findById(1L))
                .thenReturn(java.util.Optional.of(event));

        String result = eventService.deleteEvent(1L);

        assertEquals("Event deleted successfully", result);

        Mockito.verify(eventRepository).findById(1L);
        Mockito.verify(eventRepository).delete(event);
    }
}