
package com.eventa.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanningTaskRepository
        extends JpaRepository<PlanningTask, Long> {

    List<PlanningTask> findByEventId(Long eventId);
}