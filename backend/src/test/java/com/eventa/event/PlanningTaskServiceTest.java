package com.eventa.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanningTaskServiceTest {

    private PlanningTaskRepository planningTaskRepository;
    private EventRepository eventRepository;
    private PlanningTaskService planningTaskService;

    @BeforeEach
    void setUp() {
        planningTaskRepository = mock(PlanningTaskRepository.class);
        eventRepository = mock(EventRepository.class);

        planningTaskService = new PlanningTaskService(
                planningTaskRepository, eventRepository);
    }

    @Test
    void shouldReturnTasksForExistingEvent() {
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(planningTaskRepository.findByEventId(1L))
                .thenReturn(List.of(new PlanningTask()));

        List<PlanningTask> tasks =
                planningTaskService.getTasksByEventId(1L);

        assertEquals(1, tasks.size());
    }

    @Test
    void shouldRejectTasksForMissingEvent() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        assertThrows(EventNotFoundException.class,
                () -> planningTaskService.getTasksByEventId(99L));

        verify(planningTaskRepository, never()).findByEventId(99L);
    }

    @Test
    void shouldCreateTaskAndConnectItToEvent() {
        Event event = new Event();

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        PlanningTask task = new PlanningTask();
        task.setTitle("Book the photographer");

        when(planningTaskRepository.save(task)).thenReturn(task);

        PlanningTask savedTask =
                planningTaskService.createTask(1L, task);

        assertSame(event, savedTask.getEvent());
        verify(planningTaskRepository).save(task);
    }

    @Test
    void shouldMarkTaskAsCompleted() {
        Event event = mock(Event.class);
        when(event.getId()).thenReturn(1L);

        PlanningTask task = new PlanningTask();
        task.setEvent(event);

        when(eventRepository.existsById(1L)).thenReturn(true);
        when(planningTaskRepository.findById(5L))
                .thenReturn(Optional.of(task));
        when(planningTaskRepository.save(task)).thenReturn(task);

        PlanningTask updatedTask =
                planningTaskService.updateTaskCompletion(1L, 5L, true);

        assertTrue(updatedTask.isCompleted());
        verify(planningTaskRepository).save(task);
    }
}