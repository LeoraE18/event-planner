
package com.eventa.event;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;

    public GuestService(
            GuestRepository guestRepository,
            EventRepository eventRepository) {
        this.guestRepository = guestRepository;
        this.eventRepository = eventRepository;
    }

    public List<Guest> getGuestsByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }

        return guestRepository.findByEventId(eventId);
    }

    public Guest createGuest(Long eventId, Guest guest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found"));

        guest.setEvent(event);

        return guestRepository.save(guest);
    }


    public Guest updateGuestRsvp(Long eventId, Long guestId, String rsvpStatus) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found"));

        Guest guest = guestRepository.findById(guestId)
                .filter(g -> g.getEvent().getId().equals(event.getId()))
                .orElseThrow(() ->
                        new RuntimeException("Guest not found for this event"));

        guest.setRsvpStatus(rsvpStatus);

        return guestRepository.save(guest);
    }

    public void deleteGuest(Long eventId, Long guestId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found"));

        Guest guest = guestRepository.findById(guestId)
                .filter(g -> g.getEvent().getId().equals(event.getId()))
                .orElseThrow(() ->
                        new RuntimeException("Guest not found for this event"));

        guestRepository.delete(guest);
    }
}