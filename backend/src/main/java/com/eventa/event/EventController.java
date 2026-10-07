package com.eventa.event;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Get all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // Get one event by ID
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    // Create an event
    @PostMapping
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventService.createEvent(event);
    }

    // Update an event
    @PutMapping("/{id}")
    public Event updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody Event eventDetails) {

        return eventService.updateEvent(id, eventDetails);
    }

    // Delete an event
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }
}