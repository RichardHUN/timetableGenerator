<svelte:head>
    <title>Docs - Timetable Generator</title>
</svelte:head>

<div class="container py-5">
    <div class="card shadow-sm">
        <div class="card-body p-4 p-md-5">
            <h1 class="h2 mb-1">How to Use the Generator</h1>
            <p class="text-muted mb-4">A quick guide to filling in the inputs on the <a href="/generate">Generate</a> page.</p>

            <!-- Rooms -->
            <section class="mb-5">
                <h2 class="h4 mb-3 section-heading">
                    <span class="section-icon">🏛️</span> Rooms
                </h2>
                <p>Each room represents a physical space where courses can be held. Add one entry per room.</p>
                <div class="table-responsive">
                    <table class="table table-bordered align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Field</th>
                                <th>Description</th>
                                <th>Example</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Room Number</strong></td>
                                <td>The name of the room.</td>
                                <td><code>101</code>, <code>Lab A</code>, <code>IF-204</code></td>
                            </tr>
                            <tr>
                                <td><strong>Capacity</strong></td>
                                <td>Maximum number of people the room can seat.</td>
                                <td><code>30</code></td>
                            </tr>
                            <tr>
                                <td><strong>Availability</strong></td>
                                <td>
                                    Represents the timetable of the room. Each room has a seperate `week`.
                                    For each day the room is usable, add the day name and one or more time windows.
                                    A <em>time window</em> is a list of HH:MM possible <strong>start times</strong>, which follow eachother, without a break.
                                    You can add multiple windows per day (e.g. morning and afternoon slots).
                                </td>
                                <td>Monday · 08:00, 9:00, 10:00, 11:00 - 13:00, 14:00, 15:00, 16:00 17:00, 18:00, 19:00</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="alert alert-info mb-0">
                    <strong>Tip:</strong> A course can only be scheduled in a room if the room has a time window long enough to fit the course duration, 
                    and the room's capacity is at least equal to the number of listeners. The end of the day is on hour after the last start time.
                </div>
            </section>

            <!-- Planned Courses -->
            <section class="mb-5">
                <h2 class="h4 mb-3 section-heading">
                    <span class="section-icon">📚</span> Planned Courses
                </h2>
                <p>Each planned course is a lecture or class that needs to be placed in the timetable.</p>
                <div class="table-responsive">
                    <table class="table table-bordered align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Field</th>
                                <th>Description</th>
                                <th>Example</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Course Name</strong></td>
                                <td>The title of the course.</td>
                                <td><code>Introduction to Algebra</code></td>
                            </tr>
                            <tr>
                                <td><strong>Presenter Name</strong></td>
                                <td>The name of the lecturer or instructor. Used to link preferences to courses and for conflict checking.</td>
                                <td><code>Dr. Smith</code></td>
                            </tr>
                            <tr>
                                <td><strong>Number of Listeners</strong></td>
                                <td>Expected student attendance. The generator will only assign rooms with sufficient capacity.</td>
                                <td><code>25</code></td>
                            </tr>
                            <tr>
                                <td><strong>Duration (Hours &amp; Minutes)</strong></td>
                                <td>Total length of a single session. Both fields together define the duration.</td>
                                <td><code>1</code> h <code>30</code> min</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- Preferences -->
            <section class="mb-5">
                <h2 class="h4 mb-3 section-heading">
                    <span class="section-icon">⚙️</span> Preferences
                </h2>
                <p>
                    Preferences are optional constraints or wishes attached to a presenter.
                    They influence how the generator schedules that presenter's courses.
                    Each preference has a <em>strictness</em> level — higher means the constraint is harder to ignore.
                </p>
                    <div class="alert alert-info mb-0">
                        <em>The generator generates multiple candidates, and chooses the one which breaks the least constraint points.</em>
                    </div>
                <div class="table-responsive mb-3">
                    <table class="table table-bordered align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Field</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><strong>Strictness</strong></td>
                                <td>
                                    How strongly the constraint should be enforced.
                                    <br />
                                    <span class="badge bg-success">1</span> softest preference &nbsp;
                                    <span class="badge bg-danger">5</span> hardest preference
                                </td>
                            </tr>
                            <tr>
                                <td><strong>Presenter Name</strong></td>
                                <td>Who this preference applies to. Must match the presenter name used in a Planned Course.</td>
                            </tr>
                            <tr>
                                <td><strong>Constraint Type</strong></td>
                                <td>The kind of constraint (see table below).</td>
                            </tr>
                            <tr>
                                <td><strong>Value</strong></td>
                                <td>The parameter for the constraint (e.g. a day name or time). Depends on the constraint type.</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <h3 class="h6 mb-2">Available Constraint Types</h3>
                <div class="table-responsive">
                    <table class="table table-bordered align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>Constraint</th>
                                <th>Meaning</th>
                                <th>Value format</th>
                                <th>Example</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td><code>noClassesOnDay</code></td>
                                <td>Presenter should have no classes on a specific day.</td>
                                <td>Day name <br/>
                                    <em>(Must match day name in the room's availability)</em>
                                </td>
                                <td><code>Friday</code></td>
                            </tr>
                            <tr>
                                <td><code>noClassesBeforeTime</code></td>
                                <td>Presenter should not be scheduled before a given time.</td>
                                <td><code>HH:MM</code></td>
                                <td><code>09:00</code></td>
                            </tr>
                            <tr>
                                <td><code>noClassesAfterTime</code></td>
                                <td>Presenter should not be scheduled after a given time.</td>
                                <td><code>HH:MM</code></td>
                                <td><code>17:00</code></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </section>

            <!-- JSON Upload -->
            <section class="mb-5">
                <h2 class="h4 mb-3 section-heading">
                    <span class="section-icon">📂</span> Uploading a JSON File
                </h2>
                <p>
                    Instead of filling in everything manually, you can upload a JSON file that pre-fills all the fields.
                    This is useful for saving and reloading a previous configuration.
                    Use the <strong>Export JSON</strong> button to download the current form state as a file, then upload it later to restore it.
                </p>
                <p>The expected JSON structure is:</p>
                <pre class="bg-body-secondary rounded p-3"><code>{`{
  {
  "rooms": [
    {
      "roomNumber": "IF01",
      "capacity": 200,
      "week": {
        "days": [
          {
            "name": "Monday",
            "availableWindows": [
              [
                {
                  "hour": 8,
                  "minute": 0
                },
                {
                  "hour": 9,
                  "minute": 0
                },
                {
                  "hour": 10,
                  "minute": 0
                },
                {
                  "hour": 11,
                  "minute": 0
                }
              ],
              [
                {
                  "hour": 13,
                  "minute": 0
                },
                {
                  "hour": 14,
                  "minute": 0
                },
                {
                  "hour": 15,
                  "minute": 0
                },
                {
                  "hour": 16,
                  "minute": 0
                },
                {
                  "hour": 17,
                  "minute": 0
                },
                {
                  "hour": 18,
                  "minute": 0
                },
                {
                  "hour": 19,
                  "minute": 0
                }
              ]
            ]
          }
        ]
      }
    },
    {
      "roomNumber": "IF204",
      "capacity": 32,
      "week": {
        "days": [
          {
            "name": "Monday",
            "availableWindows": [
              [
                {
                  "hour": 8,
                  "minute": 0
                },
                {
                  "hour": 9,
                  "minute": 0
                },
                {
                  "hour": 10,
                  "minute": 0
                },
                {
                  "hour": 11,
                  "minute": 0
                }
              ],
              [
                {
                  "hour": 13,
                  "minute": 0
                },
                {
                  "hour": 14,
                  "minute": 0
                },
                {
                  "hour": 15,
                  "minute": 0
                },
                {
                  "hour": 16,
                  "minute": 0
                },
                {
                  "hour": 17,
                  "minute": 0
                },
                {
                  "hour": 18,
                  "minute": 0
                },
                {
                  "hour": 19,
                  "minute": 0
                }
              ]
            ]
          }
        ]
      }
    }
  ],
  "plannedCourses": [
    {
      "name": "Statistics",
      "presenterName": "John Doe",
      "numberOfListeners": 180,
      "durationHours": 1,
      "durationMinutes": 40
    },
    {
      "name": "Programming Languages",
      "presenterName": "Jane Doe",
      "numberOfListeners": 180,
      "durationHours": 1,
      "durationMinutes": 40
    },
    {
      "name": "Math",
      "presenterName": "James Doe",
      "numberOfListeners": 28,
      "durationHours": 1,
      "durationMinutes": 30
    },
    {
      "name": "Operating Systems",
      "presenterName": "Jessica Doe",
      "numberOfListeners": 24,
      "durationHours": 1,
      "durationMinutes": 30
    },
    {
      "name": "Web Development",
      "presenterName": "Josh Doe",
      "numberOfListeners": 18,
      "durationHours": 1,
      "durationMinutes": 30
    }
  ],
  "preferences": []
}`}</code></pre>
            </section>

            <!-- Generate -->
            <section class="mb-4">
                <h2 class="h4 mb-3 section-heading">
                    <span class="section-icon">🚀</span> Generating the Timetable
                </h2>
                <p>
                    Once you have filled in at least one room and one planned course, click the <strong>Generate!</strong> button.
                    The server will attempt to find a conflict-free schedule and redirect you to the result page.
                    If the inputs are invalid or the server cannot find a solution, an error message will be shown.
                </p>
                <div class="alert alert-warning mb-0">
                    <strong>Note:</strong> You need to be <a href="/login">logged in</a> to generate and save timetables. Generation itself may still work without an account, but saving requires authentication.
                </div>
            </section>

            <div class="text-center mt-4">
                <a href="/generate" class="btn btn-primary px-4">Go to Generate</a>
            </div>
        </div>
    </div>
</div>

<style>
    :global(main) {
        background-color: rgba(255, 122, 0, 1) !important;
    }

    .section-heading {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        border-bottom: 2px solid rgba(0,0,0,0.08);
        padding-bottom: 0.4rem;
    }

    :global(html[data-theme="dark"]) .section-heading {
        border-color: rgba(255,255,255,0.1);
    }

    .section-icon {
        font-size: 1.2rem;
    }

    pre {
        font-size: 0.85rem;
        overflow-x: auto;
    }

    :global(html[data-theme="dark"]) pre {
        background-color: rgba(255,255,255,0.05) !important;
        color: #e6eef6;
    }

    :global(html[data-theme="dark"]) .table {
        color: #e6eef6;
    }

    :global(html[data-theme="dark"]) .table-light th {
        background-color: rgba(255,255,255,0.07) !important;
        color: #e6eef6;
    }

    :global(html[data-theme="dark"]) .table-bordered td,
    :global(html[data-theme="dark"]) .table-bordered th {
        border-color: rgba(255,255,255,0.1);
    }

    :global(html[data-theme="dark"]) .table tbody tr,
    :global(html[data-theme="dark"]) .table tbody td {
        background-color: rgba(30, 34, 41, 1) !important;
        color: #e6eef6;
    }

    :global(html[data-theme="dark"]) .card {
        background-color: rgba(30, 34, 41, 1);
        border-color: rgba(255,255,255,0.08);
        color: #e6eef6;
    }
</style>
