
package com.eventa.event;

import java.math.BigDecimal;

public class BudgetSummary {

    private BigDecimal totalEstimated;
    private BigDecimal totalActual;

    public BudgetSummary() {
    }

    public BudgetSummary(
            BigDecimal totalEstimated,
            BigDecimal totalActual) {
        this.totalEstimated = totalEstimated;
        this.totalActual = totalActual;
    }

    public BigDecimal getTotalEstimated() {
        return totalEstimated;
    }

    public void setTotalEstimated(BigDecimal totalEstimated) {
        this.totalEstimated = totalEstimated;
    }

    public BigDecimal getTotalActual() {
        return totalActual;
    }

    public void setTotalActual(BigDecimal totalActual) {
        this.totalActual = totalActual;
    }

    public BigDecimal getRemaining() {
        return totalEstimated.subtract(totalActual);
    }
}