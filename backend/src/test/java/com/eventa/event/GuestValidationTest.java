
package com.eventa.event;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuestValidationTest {

    private final ValidatorFactory factory =
            Validation.buildDefaultValidatorFactory();

    private final Validator validator = factory.getValidator();

    @Test
    void shouldRejectPartySizeOfZero() {
        Guest guest = new Guest();
        guest.setName("Alex Smith");
        guest.setRsvpStatus("Pending");
        guest.setPartySize(0);

        var violations = validator.validate(guest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(
                violation -> violation.getMessage()
                        .equals("Party size must be at least 1")
        ));
    }

    @Test
    void shouldAcceptPartySizeOfOne() {
        Guest guest = new Guest();
        guest.setName("Alex Smith");
        guest.setRsvpStatus("Pending");
        guest.setPartySize(1);

        var violations = validator.validate(guest);

        assertTrue(violations.stream().noneMatch(
                violation -> violation.getPropertyPath()
                        .toString().equals("partySize")
        ));
    }
}