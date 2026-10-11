
package com.eventa.event;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/budget")
public class BudgetItemController {

    private final BudgetItemService budgetItemService;

    public BudgetItemController(BudgetItemService budgetItemService) {
        this.budgetItemService = budgetItemService;
    }

    @GetMapping
    public List<BudgetItem> getBudgetItems(@PathVariable Long eventId) {
        return budgetItemService.getBudgetItemsByEventId(eventId);
    }

    @PostMapping
    public BudgetItem createBudgetItem(
            @PathVariable Long eventId,
            @Valid @RequestBody BudgetItem budgetItem) {
        return budgetItemService.createBudgetItem(eventId, budgetItem);
    }

    @GetMapping("/summary")
    public BudgetSummary getBudgetSummary(@PathVariable Long eventId) {
        return budgetItemService.getBudgetSummary(eventId);
    }
}