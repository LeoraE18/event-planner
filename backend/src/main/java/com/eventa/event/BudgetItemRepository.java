
package com.eventa.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetItemRepository
        extends JpaRepository<BudgetItem, Long> {

    List<BudgetItem> findByEventId(Long eventId);
}