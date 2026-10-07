package com.eventa.event;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id).orElse(null);

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

    public String deleteEvent(Long id) {
        Event event = eventRepository.findById(id).orElse(null);

        if (event == null) {
            return "Event not found";
        }

        eventRepository.delete(event);
        return "Event deleted successfully";
    }
}