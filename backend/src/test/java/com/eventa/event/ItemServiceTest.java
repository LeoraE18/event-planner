
package com.eventa.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BudgetItemServiceTest {

    private BudgetItemRepository budgetItemRepository;
    private EventRepository eventRepository;
    private BudgetItemService budgetItemService;

    @BeforeEach
    void setUp() {
        budgetItemRepository = mock(BudgetItemRepository.class);
        eventRepository = mock(EventRepository.class);
        budgetItemService = new BudgetItemService(
                budgetItemRepository, eventRepository);
    }

    @Test
    void shouldCalculateBudgetSummary() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        BudgetItem venue = new BudgetItem();
        venue.setEstimatedCost(new BigDecimal("1500.00"));
        venue.setActualCost(new BigDecimal("1200.00"));

        BudgetItem food = new BudgetItem();
        food.setEstimatedCost(new BigDecimal("800.00"));
        food.setActualCost(new BigDecimal("600.00"));

        when(budgetItemRepository.findByEventId(1L))
                .thenReturn(List.of(venue, food));

        BudgetSummary summary =
                budgetItemService.getBudgetSummary(1L);

        assertEquals(new BigDecimal("2300.00"),
                summary.getTotalEstimated());
        assertEquals(new BigDecimal("1800.00"),
                summary.getTotalActual());
        assertEquals(new BigDecimal("500.00"),
                summary.getRemaining());
    }

    @Test
    void shouldReturnZeroTotalsWhenThereAreNoBudgetItems() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(budgetItemRepository.findByEventId(1L))
                .thenReturn(List.of());

        BudgetSummary summary =
                budgetItemService.getBudgetSummary(1L);

        assertEquals(new BigDecimal("0"),
                summary.getTotalEstimated());
        assertEquals(new BigDecimal("0"),
                summary.getTotalActual());
        assertEquals(new BigDecimal("0"),
                summary.getRemaining());
    }

    @Test
    void shouldRejectSummaryForMissingEvent() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        assertThrows(EventNotFoundException.class,
                () -> budgetItemService.getBudgetSummary(99L));

        verify(budgetItemRepository, never()).findByEventId(99L);
    }
}