package com.eventa.event;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // Get all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Get one event by ID
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    // Create an event
    @PostMapping
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventRepository.save(event);
    }

    // Update an event
    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @Valid @RequestBody Event eventDetails) {        Event event = eventRepository.findById(id).orElse(null);

        if (event == null) {
            return null;
        }

        event.setName(eventDetails.getName());
        event.setDate(eventDetails.getDate());
        event.setLocation(eventDetails.getLocation());
        event.setDescription(eventDetails.getDescription());
        event.setEventType(eventDetails.getEventType());

        return eventRepository.save(event);
    }

    // Delete an event
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Event event = eventRepository.findById(id).orElse(null);

        if (event == null) {
            return "Event not found";
        }

        eventRepository.delete(event);
        return "Event deleted successfully";
    }
}