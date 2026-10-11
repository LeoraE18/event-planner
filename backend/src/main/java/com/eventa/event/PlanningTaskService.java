
package com.eventa.event;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanningTaskService {

    private final PlanningTaskRepository planningTaskRepository;
    private final EventRepository eventRepository;

    public PlanningTaskService(
            PlanningTaskRepository planningTaskRepository,
            EventRepository eventRepository) {
        this.planningTaskRepository = planningTaskRepository;
        this.eventRepository = eventRepository;
    }

    public List<PlanningTask> getTasksByEventId(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }

        return planningTaskRepository.findByEventId(eventId);
    }

    public PlanningTask createTask(Long eventId, PlanningTask task) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException("Event not found"));

        task.setEvent(event);

        return planningTaskRepository.save(task);
    }

    public PlanningTask updateTaskCompletion(
            Long eventId, Long taskId, boolean completed) {

        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }

        PlanningTask task = planningTaskRepository.findById(taskId)
                .filter(existingTask ->
                        existingTask.getEvent() != null
                        && existingTask.getEvent().getId().equals(eventId))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Planning task not found for this event"));

        task.setCompleted(completed);

        return planningTaskRepository.save(task);
    }

    public void deleteTask(Long eventId, Long taskId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found");
        }

        PlanningTask task = planningTaskRepository.findById(taskId)
                .filter(existingTask ->
                        existingTask.getEvent() != null
                        && existingTask.getEvent().getId().equals(eventId))
                .orElseThrow(() ->
                        new RuntimeException(
                                "Planning task not found for this event"));

        planningTaskRepository.delete(task);
    }
}