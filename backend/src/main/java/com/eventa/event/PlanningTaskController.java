
package com.eventa.event;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/tasks")
public class PlanningTaskController {

    private final PlanningTaskService planningTaskService;

    public PlanningTaskController(
            PlanningTaskService planningTaskService) {
        this.planningTaskService = planningTaskService;
    }

    @GetMapping
    public List<PlanningTask> getTasks(@PathVariable Long eventId) {
        return planningTaskService.getTasksByEventId(eventId);
    }

    @PostMapping
    public PlanningTask createTask(
            @PathVariable Long eventId,
            @Valid @RequestBody PlanningTask task) {
        return planningTaskService.createTask(eventId, task);
    }

    @PatchMapping("/{taskId}/completion")
    public PlanningTask updateTaskCompletion(
            @PathVariable Long eventId,
            @PathVariable Long taskId,
            @RequestBody Map<String, Boolean> body) {

        Boolean completed = body.get("completed");

        if (completed == null) {
            throw new IllegalArgumentException(
                    "The completed field is required");
        }

        return planningTaskService.updateTaskCompletion(
                eventId, taskId, completed);
    }
}