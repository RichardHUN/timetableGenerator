<svelte:head>
    <title>Timetable Result - Timetable Generator</title>
</svelte:head>

<script lang="ts">
    import { onMount } from 'svelte';
    import { page } from '$app/stores';
    import { getTimetable, renameTimetable } from '$lib/api';

    function focusOnMount(node: HTMLElement) { node.focus(); }

    interface Time { hour: number; minute: number; }
    interface Day { name: string; availableWindows: Time[][]; }
    interface Room { roomNumber: string; capacity: number; week: any; }
    interface Course {
        day: Day;
        startTime: Time;
        endTime: Time;
        name: string;
        room: Room;
        presenterName: string;
        numberOfListeners: number;
    }
    interface Result {
        week: { days: Day[] };
        courses: Course[];
    }

    let result: Result | null = null;
    let error: string | null = null;

    let timetableOpen = true;
    let scheduledOpen = true;

    // API-loaded timetable metadata
    let timetableName: string | null = null;
    let timetableId: string | null = null;
    let renamingTitle = false;
    let renameTitleValue = '';

    async function startRenameTitle() {
        renameTitleValue = timetableName ?? '';
        renamingTitle = true;
    }

    async function confirmRenameTitle() {
        const trimmed = renameTitleValue.trim();
        if (trimmed && trimmed !== timetableName && timetableId) {
            const token = localStorage.getItem('token') ?? '';
            await renameTimetable(timetableId, trimmed, token);
            timetableName = trimmed;
        }
        renamingTitle = false;
    }

    function cancelRenameTitle() {
        renamingTitle = false;
    }

    let days: string[] = [];
    let timeSlots: number[] = [];
    let courseMap: Map<string, Course[]> = new Map();
    let skippedCells: Set<string> = new Set();

    function pad(n: number): string {
        return String(n).padStart(2, '0');
    }

    function fmtTime(t: Time): string {
        return `${pad(t.hour)}:${pad(t.minute)}`;
    }

    function getCourseRowspan(course: Course): number {
        const startMins = course.startTime.hour * 60 + course.startTime.minute;
        const endMins = course.endTime.hour * 60 + course.endTime.minute;
        return Math.ceil((endMins - startMins) / 60);
    }

    function buildTimetable(r: Result): void {
        days = r.week.days.map(d => d.name);

        const courses = r.courses;
        if (courses.length === 0) {
            timeSlots = [];
            courseMap = new Map();
            skippedCells = new Set();
            return;
        }

        let minHour = Infinity;
        let maxLastRow = -Infinity;

        for (const c of courses) {
            minHour = Math.min(minHour, c.startTime.hour);
            const lastRow = c.endTime.minute > 0 ? c.endTime.hour : c.endTime.hour - 1;
            maxLastRow = Math.max(maxLastRow, lastRow);
        }

        timeSlots = [];
        for (let h = minHour; h <= maxLastRow; h++) {
            timeSlots.push(h);
        }

        courseMap = new Map();
        skippedCells = new Set();

        for (const c of courses) {
            const key = `${c.day.name}:${c.startTime.hour}`;
            const existing = courseMap.get(key) ?? [];
            courseMap.set(key, [...existing, c]);
        }

        for (const [key, coursesAtSlot] of courseMap) {
            const [dayName, hourStr] = key.split(':');
            const startHour = parseInt(hourStr);
            const maxRowspan = Math.max(...coursesAtSlot.map(getCourseRowspan));
            for (let i = 1; i < maxRowspan; i++) {
                skippedCells.add(`${dayName}:${startHour + i}`);
            }
        }
    }

    function getCellRowspan(day: string, hour: number): number {
        const coursesAtSlot = courseMap.get(`${day}:${hour}`);
        if (!coursesAtSlot || coursesAtSlot.length === 0) return 1;
        return Math.max(...coursesAtSlot.map(getCourseRowspan));
    }

    onMount(async () => {
        const idParam = $page.url.searchParams.get('id');
        if (idParam) {
            const token = localStorage.getItem('token');
            if (!token) {
                error = 'You are not authorised. Please log in and try again.';
                return;
            }
            const res = await getTimetable(idParam, token);
            if (res.error) {
                error = res.error;
            } else if (res.data) {
                timetableId = res.data.id;
                timetableName = res.data.name;
                try {
                    result = JSON.parse(res.data.timetableJson) as Result;
                    buildTimetable(result);
                } catch (e) {
                    error = 'Failed to parse timetable data.';
                }
            }
        } else {
            try {
                const raw = sessionStorage.getItem('lastGenerateResult');
                if (raw) {
                    const parsed = JSON.parse(raw);
                    // Backend now returns a TimetableEntity with timetableJson
                    if (parsed?.timetableJson) {
                        timetableId = String(parsed.id);
                        timetableName = parsed.name ?? null;
                        result = JSON.parse(parsed.timetableJson) as Result;
                    } else {
                        // Legacy: raw result object
                        result = parsed as Result;
                    }
                    buildTimetable(result);
                } else {
                    error = 'No result found. Generate something first.';
                }
            } catch (e) {
                error = 'Failed to read or parse stored result.';
            }
        }
    });
</script>

<div class="container py-5">
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <h1 class="h2 mb-4 page-title">Generated Timetable</h1>

            {#if timetableId}
                <div class="timetable-name-bar mb-4 d-flex align-items-center gap-2">
                    {#if renamingTitle}
                        <input
                            class="form-control form-control-sm rename-title-input"
                            type="text"
                            bind:value={renameTitleValue}
                            on:keydown={(e) => { if (e.key === 'Enter') confirmRenameTitle(); if (e.key === 'Escape') cancelRenameTitle(); }}
                            on:blur={confirmRenameTitle}
                            use:focusOnMount
                        />
                    {:else}
                        <span class="timetable-display-name">{timetableName}</span>
                        <button class="btn btn-sm btn-outline-secondary rename-title-btn" type="button" on:click={startRenameTitle} title="Rename timetable">✏️</button>
                    {/if}
                </div>
            {/if}

    {#if error}
        <div class="alert alert-warning">{error}</div>
    {/if}

    {#if result}
        {#if result.courses.length === 0}
            <div class="alert alert-info">No courses were scheduled.</div>
        {:else}
            <div class="card shadow-sm mb-4">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <button class="fold-section-btn" type="button" on:click={() => timetableOpen = !timetableOpen} aria-label="Toggle timetable">
                        <span class="fold-chevron" class:open={timetableOpen}>&#9654;</span>
                        <h5 class="mb-0 d-inline">Timetable</h5>
                        {#if !timetableOpen}<span class="fold-count text-muted ms-2">({result.courses.length} courses)</span>{/if}
                    </button>
                </div>
                {#if timetableOpen}
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-bordered mb-0 timetable">
                            <thead>
                                <tr>
                                    <th class="time-col">Time</th>
                                    {#each days as day}
                                        <th class="day-col">{day}</th>
                                    {/each}
                                </tr>
                            </thead>
                            <tbody>
                                {#each timeSlots as hour}
                                    <tr>
                                        <td class="time-label">{pad(hour)}:00</td>
                                        {#each days as day}
                                            {#if !skippedCells.has(`${day}:${hour}`)}
                                                {#if courseMap.has(`${day}:${hour}`)}
                                                    <td class="course-cell" rowspan={getCellRowspan(day, hour)}>
                                                        <div class="courses-wrapper">
                                                            {#each courseMap.get(`${day}:${hour}`)! as course}
                                                                <div class="course-item">
                                                                    <div class="course-name">{course.name}</div>
                                                                    <div class="course-meta">
                                                                        <span class="badge-time">{fmtTime(course.startTime)}–{fmtTime(course.endTime)}</span>
                                                                        <span class="badge-room">Room {course.room.roomNumber}</span>
                                                                    </div>
                                                                    <div class="course-presenter">{course.presenterName}</div>
                                                                </div>
                                                            {/each}
                                                        </div>
                                                    </td>
                                                {:else}
                                                    <td class="empty-cell"></td>
                                                {/if}
                                            {/if}
                                        {/each}
                                    </tr>
                                {/each}
                            </tbody>
                        </table>
                    </div>
                </div>
                {/if}
            </div>

            <div class="card shadow-sm mb-4 scheduled-courses">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <button class="fold-section-btn" type="button" on:click={() => scheduledOpen = !scheduledOpen} aria-label="Toggle scheduled courses">
                        <span class="fold-chevron" class:open={scheduledOpen}>&#9654;</span>
                        <h5 class="mb-0 d-inline">Scheduled Courses</h5>
                        {#if !scheduledOpen}<span class="fold-count ms-2">({result.courses.length})</span>{/if}
                    </button>
                </div>
                {#if scheduledOpen}
                <ul class="list-group list-group-flush">
                    {#each result.courses as course}
                        <li class="list-group-item course-list-item">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <span class="course-list-name">{course.name}</span>
                                    <span class="course-list-presenter ms-2 text-muted">{course.presenterName}</span>
                                </div>
                                <div class="text-end">
                                    <span class="badge bg-secondary me-1">Room {course.room.roomNumber}</span>
                                    <span class="badge bg-secondary">{course.numberOfListeners} listeners</span>
                                </div>
                            </div>
                            <div class="course-list-time mt-1">
                                {course.day.name} &bull; {fmtTime(course.startTime)}–{fmtTime(course.endTime)}
                            </div>
                        </li>
                    {/each}
                </ul>
                {/if}
            </div>
        {/if}
    {/if}

    <div class="d-flex justify-content-center mt-4">
        <a class="btn btn-primary shadow pop-button" href="/generate">Back to generate</a>
    </div>
        </div>
    </div>
</div>

<style>
    /* global page background (matches generate page) */
    :global(main) {
        background-color: rgba(255, 122, 0, 1) !important;
    }

    .timetable th,
    .timetable td {
        vertical-align: middle;
        text-align: center;
    }

    .time-col {
        width: 80px;
        min-width: 80px;
    }

    .timetable thead .time-col {
        background-color: rgba(255, 122, 0, 1);
        color: #fff;
        font-weight: 600;
    }

    .day-col {
        background-color: var(--accent, #4f46e5);
        color: #fff;
        font-weight: 600;
        letter-spacing: 0.03em;
    }

    .page-title {
        color: var(--accent);
        font-weight: 700;
        border-bottom: 2px solid var(--accent);
        padding-bottom: 0.4rem;
        display: inline-block;
    }

    .time-label {
        font-size: 0.85rem;
        font-weight: 500;
        white-space: nowrap;
        color: var(--text, inherit);
        background-color: rgba(0, 0, 0, 0.05);
    }

    .empty-cell {
        background-color: rgba(0, 0, 0, 0.05);
    }

    .course-cell {
        background-color: var(--accent, #4f46e5);
        color: #fff;
        padding: 0.4rem 0.5rem;
    }

    .courses-wrapper {
        display: flex;
        flex-wrap: wrap;
        gap: 0.4rem;
        justify-content: center;
    }

    .course-item {
        flex: 1 1 120px;
        min-width: 100px;
        background-color: rgba(255, 255, 255, 0.12);
        border-radius: 0.35rem;
        padding: 0.4rem 0.5rem;
    }

    .course-item + .course-item {
        border-left: 2px solid rgba(255, 255, 255, 0.25);
    }

    .course-name {
        font-weight: 700;
        font-size: 0.95rem;
        margin-bottom: 0.25rem;
    }

    .course-meta {
        display: flex;
        gap: 0.4rem;
        justify-content: center;
        flex-wrap: wrap;
        font-size: 0.78rem;
        margin-bottom: 0.2rem;
    }

    .badge-time,
    .badge-room {
        background-color: rgba(255, 255, 255, 0.2);
        border-radius: 0.25rem;
        padding: 0.1rem 0.35rem;
    }

    .course-presenter {
        font-size: 0.8rem;
        opacity: 0.9;
    }

    :global(html[data-theme="light"]) .scheduled-courses .card-header {
        background-color: rgba(255, 122, 0, 1) !important;
    }

    :global(html[data-theme="light"]) .scheduled-courses .card-header * {
        color: #ffffff !important;
    }

    :global(html[data-theme="light"]) .scheduled-courses .list-group-item {
        border-color: rgba(255, 122, 0, 1) !important;
        background-color: rgba(0, 0, 0, 0.05) !important;
        border-radius: 0.25rem;
    }

    .course-list-name {
        font-weight: 600;
    }

    .course-list-time {
        font-size: 0.85rem;
        color: #6c757d;
    }

    .pop-button {
        transition: transform .15s ease, box-shadow .15s ease;
    }

    .pop-button:hover {
        transform: scale(1.05);
        box-shadow: 0 0.5rem 1rem rgba(0,0,0,.15);
    }

    .fold-section-btn {
        background: none;
        border: none;
        padding: 0;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 0.4rem;
        color: inherit;
    }

    .fold-section-btn:focus {
        outline: 2px solid rgba(0,0,0,0.2);
        border-radius: 4px;
    }

    .fold-chevron {
        display: inline-block;
        font-size: 0.65rem;
        transition: transform 0.2s ease;
        transform: rotate(0deg);
        color: #888;
    }

    .fold-chevron.open {
        transform: rotate(90deg);
    }

    .fold-count {
        font-size: 0.85rem;
        color: #888;
    }

    /* Dark mode overrides */
    :global(html[data-theme="dark"]) .time-label {
        background-color: #07101a;
        color: #e6eef6;
    }

    :global(html[data-theme="dark"]) .empty-cell {
        background-color: #07101a;
    }

    :global(html[data-theme="dark"]) .timetable {
        color: #e6eef6;
    }

    :global(html[data-theme="dark"]) .timetable td,
    :global(html[data-theme="dark"]) .timetable th {
        border-color: rgba(255, 255, 255, 0.1);
    }

    :global(html[data-theme="dark"]) .card {
        background-color: rgba(30, 34, 41, 1) !important;
        border-color: rgba(255, 255, 255, 0.1) !important;
        color: #e6eef6 !important;
    }

    :global(html[data-theme="dark"]) .card-header {
        background-color: rgba(30, 34, 41, 1) !important;
        border-bottom: 1px solid rgba(255, 255, 255, 0.12) !important;
    }

    :global(html[data-theme="dark"]) .scheduled-courses .card-header {
        background-color: #07101a !important;
    }

    :global(html[data-theme="light"]) .scheduled-courses .card-header * {
        color: #ffffff !important;
    }

    :global(html[data-theme="dark"]) .scheduled-courses .list-group-item {
        background-color: rgba(30, 34, 41, 1) !important;
    }

    :global(html[data-theme="dark"]) .list-group-item {
        background-color: rgba(30, 34, 41, 1) !important;
        border-color: rgba(255, 255, 255, 0.1) !important;
        color: #e6eef6 !important;
    }

    :global(html[data-theme="dark"]) .course-list-time {
        color: #94a3b8;
    }

    /* Timetable name bar */
    .timetable-name-bar { border-bottom: 1px solid rgba(0,0,0,0.1); padding-bottom: 0.75rem; }
    .timetable-display-name { font-size: 1.15rem; font-weight: 600; color: var(--accent, #4f46e5); }
    .rename-title-btn { padding: 0.15rem 0.45rem; font-size: 0.85rem; }
    .rename-title-input { max-width: 400px; }

    :global(html[data-theme="dark"]) .timetable-name-bar { border-bottom-color: rgba(255,255,255,0.12); }
    :global(html[data-theme="dark"]) .timetable-display-name { color: var(--accent, #818cf8); }
</style>
