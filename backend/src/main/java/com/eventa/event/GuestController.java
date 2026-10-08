package com.eventa.event;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public List<Guest> getGuestsByEventId(@PathVariable Long eventId) {
        return guestService.getGuestsByEventId(eventId);
    }

    @PostMapping
    public Guest createGuest(
            @PathVariable Long eventId,
            @Valid @RequestBody Guest guest) {
        return guestService.createGuest(eventId, guest);
    }

    @PatchMapping("/{guestId}/rsvp")
    public Guest updateGuestRsvp(
            @PathVariable Long eventId,
            @PathVariable Long guestId,
            @RequestBody java.util.Map<String, String> request) {

        String rsvpStatus = request.get("rsvpStatus");

        return guestService.updateGuestRsvp(eventId, guestId, rsvpStatus);
    }


    @DeleteMapping("/{guestId}")
    public org.springframework.http.ResponseEntity<Void> deleteGuest(
            @PathVariable Long eventId,
            @PathVariable Long guestId) {

        guestService.deleteGuest(eventId, guestId);

        return org.springframework.http.ResponseEntity.noContent().build();
    }
}