
import { useEffect, useState } from "react";
import "./App.css";


const guest = {
  name: "Alex Smith",
  rsvpStatus: "Confirmed",
  partySize: 2,
};



function formatDate(dateString) {
  return new Date(`${dateString}T12:00:00`).toLocaleDateString(
    "en-US",
    {
      month: "short",
      day: "numeric",
      year: "numeric",
    }
  );
}

function formatLongDate(dateString) {
  return new Date(`${dateString}T12:00:00`).toLocaleDateString(
    "en-US",
    {
      weekday: "long",
      month: "long",
      day: "numeric",
      year: "numeric",
    }
  );
}

function App() {
  const [activePage, setActivePage] = useState("Overview");
  const [tasks, setTasks] = useState([]);
  const [taskTitle, setTaskTitle] = useState("");
  const [taskDescription, setTaskDescription] = useState("");
  const [taskDueDate, setTaskDueDate] = useState("");
  const [taskError, setTaskError] = useState("");
  const [taskSaving, setTaskSaving] = useState(false);

  const [guests, setGuests] = useState([]);
  const [guestName, setGuestName] = useState("");
  const [guestRsvpStatus, setGuestRsvpStatus] = useState("Pending");
  const [guestPartySize, setGuestPartySize] = useState("1");
  const [guestNotes, setGuestNotes] = useState("");
  const [guestError, setGuestError] = useState("");
  const [guestSaving, setGuestSaving] = useState(false);
  const [budgetItems, setBudgetItems] = useState([]);
  
  const [budgetName, setBudgetName] = useState("");
  const [budgetCategory, setBudgetCategory] = useState("");
  const [budgetEstimatedCost, setBudgetEstimatedCost] = useState("");
  const [budgetActualCost, setBudgetActualCost] = useState("0");
  const [budgetError, setBudgetError] = useState("");
  const [budgetSaving, setBudgetSaving] = useState(false);

  const [event, setEvent] = useState(null);
  const [eventError, setEventError] = useState("");

  useEffect(() => {
    fetch("/api/events/1")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Could not load event");
        }

        return response.json();
      })
      .then((data) => setEvent(data))
      .catch(() => {
        setEventError(
          "Could not load event data. Check that the backend is running."
        );
      });
  }, []);

  useEffect(() => {
    fetch("/api/events/1/tasks")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Could not load planning tasks");
        }

        return response.json();
      })
      .then((data) => {
        setTasks(data);
      })
      .catch((error) => {
        console.error("Error loading planning tasks:", error);
      });
  }, []);

  useEffect(() => {
    fetch("/api/events/1/guests")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Could not load guests");
        }

        return response.json();
      })
      .then((data) => setGuests(data))
      .catch((error) => {
        console.error("Error loading guests:", error);
      });
  }, []);


useEffect(() => {
  fetch("/api/events/1/budget")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Could not load budget items");
      }

      return response.json();
    })
    .then((data) => setBudgetItems(data))
    .catch((error) => {
      console.error("Error loading budget items:", error);
    });
}, []);

  const eventDate = event
    ? new Date(`${event.date}T00:00:00`)
    : null;

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const daysUntilEvent = eventDate
    ? Math.max(
        0,
        Math.ceil((eventDate - today) / (1000 * 60 * 60 * 24))
      )
    : null;

  const totalEstimated = budgetItems.reduce(
    (total, item) => total + item.estimatedCost,
    0
  );

  const totalActual = budgetItems.reduce(
    (total, item) => total + item.actualCost,
    0
  );

  const completedTasks = tasks.filter((task) => task.completed).length;
  const totalAttendees = guests.reduce(
    (total, guest) => {
      if (guest.rsvpStatus === "Declined") {
        return total;
      }

      return total + (guest.partySize ?? 0);
    },
    0
  );

  async function toggleTask(taskId) {
    const task = tasks.find((item) => item.id === taskId);

    if (!task) return;

    const newCompletedStatus = !task.completed;

    try {
      const response = await fetch(
        `/api/events/1/tasks/${taskId}/completion`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            completed: newCompletedStatus,
          }),
        }
      );

      if (!response.ok) {
        throw new Error("Could not update task completion");
      }

      const updatedTask = await response.json();

      setTasks((currentTasks) =>
        currentTasks.map((item) =>
          item.id === taskId
            ? { ...item, completed: updatedTask.completed }
            : item
        )
      );
    } catch (error) {
      console.error("Error updating task:", error);
      alert("Couldn't save the task update. Please check that the backend is running and try again.");
    }
  }


async function handleBudgetSubmit(formEvent) {
  formEvent.preventDefault();
  setBudgetError("");

  const name = budgetName.trim();
  const estimatedCost = Number(budgetEstimatedCost);
  const actualCost = Number(budgetActualCost);

  if (
    !name ||
    budgetEstimatedCost.trim() === "" ||
    budgetActualCost.trim() === "" ||
    !Number.isFinite(estimatedCost) ||
    !Number.isFinite(actualCost) ||
    estimatedCost < 0 ||
    actualCost < 0
  ) {
    setBudgetError("Enter a name and valid, non-negative costs.");
    return;
  }


async function handleGuestSubmit(formEvent) {
  formEvent.preventDefault();
  setGuestError("");

  const name = guestName.trim();
  const partySize = Number(guestPartySize);

  if (!name || !Number.isInteger(partySize) || partySize < 1) {
    setGuestError("Enter a name and a party size of at least 1.");
    return;
  }

  setGuestSaving(true);

  try {
    const response = await fetch("/api/events/1/guests", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name,
        rsvpStatus: guestRsvpStatus,
        partySize,
        notes: guestNotes.trim(),
      }),
    });

    if (!response.ok) {
      throw new Error("Could not save guest");
    }

    const savedGuest = await response.json();

    setGuests((currentGuests) => [...currentGuests, savedGuest]);
    setGuestName("");
    setGuestRsvpStatus("Pending");
    setGuestPartySize("1");
    setGuestNotes("");
  } catch (error) {
    console.error("Error saving guest:", error);
    setGuestError(
      "Couldn't save the guest. Check that the backend is running and try again."
    );
  } finally {
    setGuestSaving(false);
  }
}

  setBudgetSaving(true);

  try {
    const response = await fetch("/api/events/1/budget", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name,
        category: budgetCategory.trim(),
        estimatedCost,
        actualCost,
      }),
    });

    if (!response.ok) {
      throw new Error("Could not save budget item");
    }

    const savedItem = await response.json();

    setBudgetItems((currentItems) => [...currentItems, savedItem]);
    setBudgetName("");
    setBudgetCategory("");
    setBudgetEstimatedCost("");
    setBudgetActualCost("0");
  } catch (error) {
    console.error("Error saving budget item:", error);
    setBudgetError(
      "Couldn't save the budget item. Check that the backend is running and try again."
    );
  } finally {
    setBudgetSaving(false);
  }
}


async function handleGuestSubmit(formEvent) {
  formEvent.preventDefault();
  setGuestError("");

  const name = guestName.trim();
  const partySize = Number(guestPartySize);

  if (!name || !Number.isInteger(partySize) || partySize < 1) {
    setGuestError("Enter a name and a party size of at least 1.");
    return;
  }

  setGuestSaving(true);

  try {
    const response = await fetch("/api/events/1/guests", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        name,
        rsvpStatus: guestRsvpStatus,
        partySize,
        notes: guestNotes.trim(),
      }),
    });

    if (!response.ok) {
      throw new Error("Could not save guest");
    }

    const savedGuest = await response.json();

    setGuests((currentGuests) => [...currentGuests, savedGuest]);

    setGuestName("");
    setGuestRsvpStatus("Pending");
    setGuestPartySize("1");
    setGuestNotes("");
  } catch (error) {
    console.error("Error saving guest:", error);
    setGuestError(
      "Couldn't save the guest. Check that the backend is running and try again."
    );
  } finally {
    setGuestSaving(false);
  }
}


async function handleGuestRsvpChange(guestId, rsvpStatus) {
  try {
    const response = await fetch(
      `/api/events/1/guests/${guestId}/rsvp`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ rsvpStatus }),
      }
    );

    if (!response.ok) {
      throw new Error("Could not update RSVP status");
    }

    const updatedGuest = await response.json();

    setGuests((currentGuests) =>
      currentGuests.map((guest) =>
        guest.id === guestId
          ? { ...guest, rsvpStatus: updatedGuest.rsvpStatus }
          : guest
      )
    );
  } catch (error) {
    console.error("Error updating RSVP:", error);
    alert("Couldn't update the RSVP. Please check that the backend is running and try again.");
  }
}


async function handleTaskSubmit(formEvent) {
  formEvent.preventDefault();
  setTaskError("");

  const title = taskTitle.trim();

  if (!title) {
    setTaskError("Please enter a task title.");
    return;
  }

  setTaskSaving(true);

  try {
    const response = await fetch("/api/events/1/tasks", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        title,
        description: taskDescription.trim(),
        dueDate: taskDueDate || null,
        completed: false,
      }),
    });

    if (!response.ok) {
      throw new Error("Could not save planning task");
    }

    const savedTask = await response.json();

    setTasks((currentTasks) => [...currentTasks, savedTask]);
    setTaskTitle("");
    setTaskDescription("");
    setTaskDueDate("");
  } catch (error) {
    console.error("Error saving planning task:", error);
    setTaskError(
      "Couldn't save the task. Check that the backend is running and try again."
    );
  } finally {
    setTaskSaving(false);
  }
}

  const navigation = [
    { name: "Overview", icon: "⌂" },
    { name: "Guests", icon: "♧" },
    { name: "Budget", icon: "$" },
    { name: "Checklist", icon: "✓" },
  ];

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">E</div>
          <span>EVENTA</span>
        </div>

        <div className="sidebar-label">WORKSPACE</div>

        <nav className="navigation">
          {navigation.map((item) => (
            <button
              key={item.name}
              className={`nav-item ${
                activePage === item.name ? "active" : ""
              }`}
              onClick={() => setActivePage(item.name)}
            >
              <span className="nav-icon">{item.icon}</span>
              <span>{item.name}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="mini-event-icon">E</div>
          <div>
            <strong>EVENTA Demo</strong>
            <p>Your special day</p>
          </div>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div className="breadcrumb">
            Workspace <span>/</span> {activePage}
          </div>
          <div className="profile">
            <div className="profile-avatar">L</div>
            <span>My workspace</span>
          </div>
        </header>

        {activePage === "Overview" && (
          <>
            <section className="welcome-section">
              <div>
                <p className="eyebrow">YOUR EVENT, BEAUTIFULLY ORGANIZED</p>
                <h1>Let’s make it memorable.</h1>
                <p className="subtitle">
                  Everything you need to plan, all in one place.
                </p>
              </div>
              <div className="event-date-chip">
                <span className="date-chip-icon">◷</span>
                {event ? formatDate(event.date) : "Loading event..."}
              </div>
            </section>

            <section className="event-banner">
              <div className="banner-content">
                <span className="banner-label">YOUR NEXT BIG MOMENT</span>
                <h2>{event?.name ?? "Loading event..."}</h2>
                <p>
                  {event
                    ? `${event.location || "Location not set"} · ${event.eventType}`
                    : "Loading event details..."}
                </p>
                <div className="banner-date">
                  <span>◷</span>{" "}
{event ? formatLongDate(event.date) : "Loading date..."}
                </div>
              </div>

              <div className="countdown">
                <strong>{daysUntilEvent ?? "—"}</strong>
                <span>DAYS TO GO</span>
              </div>
              <div className="banner-decoration" aria-hidden="true">
                <div className="decoration-circle circle-one" />
                <div className="decoration-circle circle-two" />
                <div className="decoration-circle circle-three" />
              </div>
            </section>

            <section className="stats-grid">
              <article className="stat-card">
                <div className="stat-top">
                  <span className="stat-icon guests-icon">♧</span>
                  <span className="stat-label">GUESTS</span>
                </div>
                <strong className="stat-value">{totalAttendees}</strong>                <p>Expected attendees</p>
                <button
                  className="text-link"
                  onClick={() => setActivePage("Guests")}
                >
                  View guest list →
                </button>
              </article>

              <article className="stat-card">
                <div className="stat-top">
                  <span className="stat-icon budget-icon">$</span>
                  <span className="stat-label">BUDGET</span>
                </div>
                <strong className="stat-value">
                  ${totalEstimated.toLocaleString("en-US")}
                </strong>
                <p>Estimated expenses</p>
                <button
                  className="text-link"
                  onClick={() => setActivePage("Budget")}
                >
                  View budget →
                </button>
              </article>

              <article className="stat-card">
                <div className="stat-top">
                  <span className="stat-icon tasks-icon">✓</span>
                  <span className="stat-label">CHECKLIST</span>
                </div>
                <strong className="stat-value">
                  {completedTasks}/{tasks.length}
                </strong>
                <p>Tasks completed</p>
                <button
                  className="text-link"
                  onClick={() => setActivePage("Checklist")}
                >
                  View checklist →
                </button>
              </article>
            </section>

            <section className="content-grid">
              <article className="panel">
                <div className="panel-heading">
                  <div>
                    <p className="eyebrow">STAY ON TRACK</p>
                    <h3>Planning checklist</h3>
                  </div>
                  <button
                    className="text-link"
                    onClick={() => setActivePage("Checklist")}
                  >
                    View all →
                  </button>
                </div>

                {tasks.map((task) => (
                  <label className="task-row" key={task.id}>
                    <input
                      type="checkbox"
                      checked={task.completed}
                      onChange={() => toggleTask(task.id)}
                    />
                    <span
                      className={`task-title ${
                        task.completed ? "task-completed" : ""
                      }`}
                    >
                      {task.title}
                    </span>
                    <span className="task-date">
                      {formatDate(task.dueDate)}
                    </span>
                  </label>
                ))}

                <div className="panel-footer">
                  {completedTasks} of {tasks.length} tasks completed
                </div>
              </article>

              <article className="panel">
                <div className="panel-heading">
                  <div>
                    <p className="eyebrow">THE NUMBERS</p>
                    <h3>Budget snapshot</h3>
                  </div>
                  <button
                    className="text-link"
                    onClick={() => setActivePage("Budget")}
                  >
                    Details →
                  </button>
                </div>

                <div className="budget-line">
                  <span>Estimated</span>
                  <strong>${totalEstimated.toLocaleString("en-US")}</strong>
                </div>
                <div className="budget-line">
                  <span>Actual spending</span>
                  <strong>${totalActual.toLocaleString("en-US")}</strong>
                </div>
                <div className="budget-progress">
                  <div
                    className="budget-progress-fill"
                    style={{
                      width: `${
                        totalEstimated > 0
                          ? Math.min(
                              100,
                              (totalActual / totalEstimated) * 100
                            )
                          : 0
                      }%`,
                    }}
                  />
                </div>
                <div className="budget-remaining">
                  <span>Remaining against estimates</span>
                  <strong>
                    ${(totalEstimated - totalActual).toLocaleString("en-US")}
                  </strong>
                </div>
              </article>
            </section>
          </>
        )}


        {activePage === "Guests" && (
          <section className="page-section">
            <p className="eyebrow">YOUR PEOPLE</p>
            <h1>Guest list</h1>
            <p className="subtitle">
              Keep track of invitations and responses.
            </p>

            <article className="panel page-panel">
              <div className="panel-heading">
                <h3>Add a guest</h3>
              </div>

              <form className="guest-form" onSubmit={handleGuestSubmit}>
                <label>
                  Guest name
                  <input
                    type="text"
                    value={guestName}
                    onChange={(e) => setGuestName(e.target.value)}
                    placeholder="Enter guest name"
                    required
                  />
                </label>

                <div className="guest-form-fields">
                  <label>
                    RSVP status
                    <select
                      value={guestRsvpStatus}
                      onChange={(e) => setGuestRsvpStatus(e.target.value)}
                    >
                      <option value="Pending">Pending</option>
                      <option value="Confirmed">Confirmed</option>
                      <option value="Declined">Declined</option>
                    </select>
                  </label>

                  <label>
                    Party size
                    <input
                      type="number"
                      min="1"
                      step="1"
                      value={guestPartySize}
                      onChange={(e) => setGuestPartySize(e.target.value)}
                      required
                    />
                  </label>
                </div>

                <label>
                  Notes (optional)
                  <input
                    type="text"
                    value={guestNotes}
                    onChange={(e) => setGuestNotes(e.target.value)}
                    placeholder="e.g. Bringing a plus-one"
                  />
                </label>

                {guestError && (
                  <p className="guest-form-error" role="alert">
                    {guestError}
                  </p>
                )}

                <button
                  className="guest-submit-button"
                  type="submit"
                  disabled={guestSaving}
                >
                  {guestSaving ? "Saving..." : "+ Add guest"}
                </button>
              </form>

              <div className="panel-heading">
                <h3>Guest list</h3>
                <span className="stat-label">{guests.length} RECORDS</span>
              </div>

              {guests.length === 0 ? (
                <p className="subtitle">No guests added yet.</p>
              ) : (
                guests.map((guest) => (
                  <div className="guest-row" key={guest.id}>
                    <div className="guest-avatar">
                      {guest.name?.charAt(0).toUpperCase() || "G"}
                    </div>

                    <div className="guest-info">
                      <strong>{guest.name}</strong>
                      <p>
                        {guest.partySize} expected attendees
                        {guest.notes ? ` · ${guest.notes}` : ""}
                      </p>
                    </div>


                    <select
                      className="rsvp-select"
                      value={guest.rsvpStatus}
                      onChange={(e) =>
                        handleGuestRsvpChange(guest.id, e.target.value)
                      }
                      aria-label={`RSVP status for ${guest.name}`}
                    >
                      <option value="Pending">Pending</option>
                      <option value="Confirmed">Confirmed</option>
                      <option value="Declined">Declined</option>
                    </select>
                  </div>
                ))
              )}
            </article>
          </section>
        )}


        {activePage === "Budget" && (
          <section className="page-section">
            <p className="eyebrow">PLAN WITH CONFIDENCE</p>
            <h1>Budget tracker</h1>
            <p className="subtitle">
              Keep estimated costs and actual spending in view.
            </p>
            <section className="stats-grid budget-summary-grid">
              <article className="stat-card">
                <p className="stat-label">ESTIMATED COST</p>
                <strong className="stat-value">
                  ${totalEstimated.toLocaleString("en-US")}
                </strong>
              </article>
              <article className="stat-card">
                <p className="stat-label">ACTUAL SPENDING</p>
                <strong className="stat-value">
                  ${totalActual.toLocaleString("en-US")}
                </strong>
              </article>
              <article className="stat-card">
                <p className="stat-label">REMAINING</p>
                <strong className="stat-value">
                  ${(totalEstimated - totalActual).toLocaleString("en-US")}
                </strong>
              </article>
            </section>
            <article className="panel page-panel">
              <div className="panel-heading">
                <h3>Budget items</h3>
              </div>

              <form className="budget-form" onSubmit={handleBudgetSubmit}>
                <label>
                  Item name
                  <input
                    type="text"
                    value={budgetName}
                    onChange={(e) => setBudgetName(e.target.value)}
                    placeholder="e.g. Photographer"
                    required
                  />
                </label>

                <label>
                  Category
                  <input
                    type="text"
                    value={budgetCategory}
                    onChange={(e) => setBudgetCategory(e.target.value)}
                    placeholder="e.g. Photography"
                  />
                </label>

                <div className="budget-form-fields">
                  <label>
                    Estimated cost ($)
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={budgetEstimatedCost}
                      onChange={(e) => setBudgetEstimatedCost(e.target.value)}
                      placeholder="0.00"
                      required
                    />
                  </label>

                  <label>
                    Actual cost ($)
                    <input
                      type="number"
                      min="0"
                      step="0.01"
                      value={budgetActualCost}
                      onChange={(e) => setBudgetActualCost(e.target.value)}
                      required
                    />
                  </label>
                </div>

                {budgetError && (
                  <p className="budget-form-error" role="alert">
                    {budgetError}
                  </p>
                )}

                <button
                  className="budget-submit-button"
                  type="submit"
                  disabled={budgetSaving}
                >
                  {budgetSaving ? "Saving..." : "+ Add budget item"}
                </button>
              </form>

              {budgetItems.map((item) => (
                <div className="budget-item-row" key={item.id}>
                  <div>
                    <strong>{item.name}</strong>
                    <p>{item.category}</p>
                  </div>
                  <div className="budget-item-cost">
                    <strong>
                      ${item.estimatedCost.toLocaleString("en-US")}
                    </strong>
                    <span>estimated</span>
                  </div>
                </div>
              ))}
            </article>
          </section>
        )}


        {activePage === "Checklist" && (
          <section className="page-section">
            <p className="eyebrow">ONE STEP AT A TIME</p>
            <h1>Planning checklist</h1>
            <p className="subtitle">
              Your tasks and deadlines, all together.
            </p>

            <article className="panel page-panel">
              <div className="panel-heading">
                <h3>Add a planning task</h3>
              </div>

              <form className="task-form" onSubmit={handleTaskSubmit}>
                <label>
                  Task title
                  <input
                    type="text"
                    value={taskTitle}
                    onChange={(e) => setTaskTitle(e.target.value)}
                    placeholder="e.g. Book the photographer"
                    required
                  />
                </label>

                <label>
                  Description
                  <textarea
                    value={taskDescription}
                    onChange={(e) => setTaskDescription(e.target.value)}
                    placeholder="Add details or next steps"
                    rows={3}
                  />
                </label>

                <label>
                  Due date
                  <input
                    type="date"
                    value={taskDueDate}
                    onChange={(e) => setTaskDueDate(e.target.value)}
                  />
                </label>

                {taskError && (
                  <p className="task-form-error" role="alert">
                    {taskError}
                  </p>
                )}

                <button
                  className="task-submit-button"
                  type="submit"
                  disabled={taskSaving}
                >
                  {taskSaving ? "Saving..." : "+ Add task"}
                </button>
              </form>

              <div className="panel-heading">
                <h3>Your tasks</h3>
                <span className="stat-label">
                  {tasks.length} TASKS
                </span>
              </div>

              {tasks.length === 0 ? (
                <p className="subtitle">No tasks yet. Add your first task above.</p>
              ) : (
                tasks.map((task) => (
                  <label className="task-row" key={task.id}>
                    <input
                      type="checkbox"
                      checked={task.completed}
                      onChange={() => toggleTask(task.id)}
                    />

                    <span
                      className={`task-title ${
                        task.completed ? "task-completed" : ""
                      }`}
                    >
                      {task.title}
                    </span>

                    <span className="task-date">
                      {task.dueDate
                        ? formatDate(task.dueDate)
                        : "No deadline"}
                    </span>
                  </label>
                ))
              )}

              <div className="panel-footer">
                {completedTasks} of {tasks.length} tasks completed
              </div>
            </article>
          </section>
        )}

        <footer className="app-footer">
          <span>EVENTA</span>
          <span>Made for your moments.</span>
        </footer>
      </main>
    </div>
  );
}

export default App;