
package com.eventa.event;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetItemService {

    private final BudgetItemRepository budgetItemRepository;
    private final EventRepository eventRepository;

    public BudgetItemService(
            BudgetItemRepository budgetItemRepository,
            EventRepository eventRepository) {
        this.budgetItemRepository = budgetItemRepository;
        this.eventRepository = eventRepository;
    }

    public List<BudgetItem> getBudgetItemsByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }

        return budgetItemRepository.findByEventId(eventId);
    }

    public BudgetItem createBudgetItem(Long eventId, BudgetItem budgetItem) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found"));

        budgetItem.setEvent(event);

        return budgetItemRepository.save(budgetItem);
    }

    public BudgetSummary getBudgetSummary(Long eventId) {
        List<BudgetItem> items = getBudgetItemsByEventId(eventId);

        BigDecimal totalEstimated = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;

        for (BudgetItem item : items) {
            totalEstimated = totalEstimated.add(item.getEstimatedCost());
            totalActual = totalActual.add(item.getActualCost());
        }

        return new BudgetSummary(totalEstimated, totalActual);
    }


    public BudgetItem updateActualCost(
            Long eventId, Long budgetItemId, BigDecimal actualCost) {

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }

        BudgetItem item = budgetItemRepository.findById(budgetItemId)
                .filter(existingItem ->
                        existingItem.getEvent() != null
                        && existingItem.getEvent().getId().equals(eventId))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Budget item not found for this event"));

        item.setActualCost(actualCost);

        return budgetItemRepository.save(item);
    }
}