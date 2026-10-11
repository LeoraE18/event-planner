
package com.eventa.event;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.math.BigDecimal;
import java.util.Map;

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

        @PatchMapping("/{budgetItemId}/actual-cost")
        public BudgetItem updateActualCost(
                @PathVariable Long eventId,
                @PathVariable Long budgetItemId,
                @RequestBody Map<String, BigDecimal> body) {

            BigDecimal actualCost = body.get("actualCost");

            if (actualCost == null || actualCost.signum() < 0) {
                throw new IllegalArgumentException(
                        "Actual cost must be a non-negative amount");
            }

            return budgetItemService.updateActualCost(
                    eventId, budgetItemId, actualCost);
        }
}