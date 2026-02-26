<svelte:head>
    <title>Generate - Timetable Generator</title>
</svelte:head>

<script lang="ts">
    import RoomInput from '$lib/RoomInput.svelte';
    import PlannedCourse from '$lib/PlannedCourse.svelte';
    import PreferenceInput from '$lib/PreferenceInput.svelte';
    import { PUBLIC_API_URL } from '$env/static/public';
    import { goto } from '$app/navigation';

    type RoomState = { id: number; open: boolean; data: any };
    type PlannedCourseState = { id: number; open: boolean; data: { name: string; presenterName: string; numberOfListeners: number; durationHours: number; durationMinutes: number } };
    type PreferenceState = { id: number; open: boolean; data: { strictness: number; presenterName: string; constraint: string; value: any } };

    let rooms: RoomState[] = [];
    let plannedCourses: PlannedCourseState[] = [];
    let preferences: PreferenceState[] = [];

    let coursesOpen = true;
    let preferencesOpen = true;
    let roomsOpen = true;

    let generating = false;
    let generateResult: any = null;
    let generateError: string | null = null;
    let loadSuccess: string | null = null;

    function formatHHMM(hour: number, minute: number): string {
        return `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
    }

    function parsePreference(raw: any): { strictness: number; presenterName: string; constraint: string; value: any } {
        if (typeof raw === 'string') {
            const parts = raw.split(':');
            return {
                strictness: Number(parts[0]) || 3,
                presenterName: parts[1] ?? '',
                constraint: parts[2] ?? 'noClassesOnDay',
                value: parts.slice(3).join(':') ?? ''
            };
        }
        return {
            strictness: Number(raw?.strictness) || 3,
            presenterName: raw?.presenterName ?? '',
            constraint: raw?.constraint ?? 'noClassesOnDay',
            value: raw?.value ?? ''
        };
    }

    function friendlyError(raw: string): string {
        if (!raw) return '';
        const lower = raw.toLowerCase();
        if (lower.includes('invalid json') || lower.includes('json file'))
            return 'The uploaded file is not valid JSON. Please check the file and try again.';
        if (lower.includes('network error') || lower.includes('failed to fetch') || lower.includes('networkerror'))
            return 'Could not reach the server. Please check your connection and try again.';
        if (lower.includes('401'))
            return 'You are not authorised. Please log in and try again.';
        if (lower.includes('403'))
            return 'Access denied. You do not have permission to generate a timetable.';
        if (lower.includes('400'))
            return 'Invalid input: the data could not be processed. Please check your input and try again.';
        if (lower.includes('404') || lower.includes('503'))
            return 'The generation service is unavailable. Please try again later.';
        if (/50[0-9]/.test(raw))
            return 'The server encountered an internal error. Please try again later.';
        return raw;
    }

    function addRoom() {
        rooms = [...rooms, { id: Date.now(), open: true, data: {} }];
    }

    function addPlannedCourse() {
        plannedCourses = [...plannedCourses, { id: Date.now(), open: true, data: { name: '', presenterName: '', numberOfListeners: 0, durationHours: 0, durationMinutes: 0 } }];
    }

    function addPreference() {
        preferences = [...preferences, { id: Date.now(), open: true, data: { strictness: 3, presenterName: '', constraint: 'noClassesOnDay', value: '' } }];
    }

    function toggleCourse(id: number) {
        plannedCourses = plannedCourses.map(c => c.id === id ? { ...c, open: !c.open } : c);
    }

    function togglePreference(id: number) {
        preferences = preferences.map(p => p.id === id ? { ...p, open: !p.open } : p);
    }

    function toggleRoom(id: number) {
        rooms = rooms.map(r => r.id === id ? { ...r, open: !r.open } : r);
    }

    function removePlannedCourse(id: number) {
        plannedCourses = plannedCourses.filter((c) => c.id !== id);
    }

    function onPlannedCourseChange(i: number, e: CustomEvent) {
        plannedCourses[i].data = e.detail;
        plannedCourses = plannedCourses.slice();
    }

    function onPreferenceChange(i: number, e: CustomEvent) {
        preferences[i].data = e.detail;
        preferences = preferences.slice();
    }

    function removeRoom(id: number) {
        rooms = rooms.filter((r) => r.id !== id);
    }

    function removePreference(id: number) {
        preferences = preferences.filter((p) => p.id !== id);
    }

    function onRoomChange(i: number, e: CustomEvent) {
        rooms[i].data = e.detail;
        // trigger reactivity
        rooms = rooms.slice();
    }

    async function handleFileChange(e: Event) {
        const input = e.target as HTMLInputElement;
        const file = input?.files?.[0];
        if (!file) return;

        generateError = null;
        loadSuccess = null;

        try {
            const text = await file.text();

            let payload: any = null;
            try {
                payload = JSON.parse(text);
            } catch (parseErr) {
                generateError = `Invalid JSON file: ${parseErr instanceof Error ? parseErr.message : String(parseErr)}`;
                if (input) input.value = '';
                return;
            }

            // Map rooms
            rooms = (payload.rooms ?? []).map((r: any, ri: number) => {
                const initialDaysData = (r.week?.days ?? []).map((d: any) => ({
                    value: d.name ?? '',
                    timeWindows: (d.availableWindows ?? []).map((win: any[]) =>
                        win.map((t: any) => formatHHMM(t.hour ?? 0, t.minute ?? 0))
                    )
                }));
                return {
                    id: Date.now() + ri,
                    open: true,
                    data: {
                        roomNumber: r.roomNumber ?? '',
                        capacity: r.capacity ?? '',
                        initialDaysData,
                        days: initialDaysData
                    }
                };
            });

            // Map planned courses
            plannedCourses = (payload.plannedCourses ?? []).map((c: any, ci: number) => ({
                id: Date.now() + 10000 + ci,
                open: true,
                data: {
                    name: c.name ?? '',
                    presenterName: c.presenterName ?? '',
                    numberOfListeners: Number(c.numberOfListeners) || 0,
                    durationHours: Number(c.durationHours) || 0,
                    durationMinutes: Number(c.durationMinutes) || 0
                }
            }));

            // Map preferences
            preferences = (payload.preferences ?? []).map((p: any, pi: number) => ({
                id: Date.now() + 20000 + pi,
                open: true,
                data: parsePreference(p)
            }));

            loadSuccess = `Loaded ${rooms.length} room${rooms.length !== 1 ? 's' : ''}, ${plannedCourses.length} course${plannedCourses.length !== 1 ? 's' : ''}, ${preferences.length} preference${preferences.length !== 1 ? 's' : ''}. Review and edit below, then click Generate!`;
        } catch (err) {
            generateError = `Could not parse file: ${err instanceof Error ? err.message : String(err)}`;
        } finally {
            if (input) input.value = '';
        }
    }

    function exportJSON() {
        const payload = {
            rooms: rooms.map((r) => {
                const data = r.data || {};
                const days = (data.days || []).map((d: any) => {
                    const name = typeof d === 'string' ? d : d.value || d.name || '';
                    const availableWindows = (d.timeWindows || []).map((tw: string[]) =>
                        tw.map((t: string) => {
                            const parts = (t || '').split(':').map((x: string) => Number(x));
                            return { hour: Number.isFinite(parts[0]) ? parts[0] : 0, minute: Number.isFinite(parts[1]) ? parts[1] : 0 };
                        })
                    );
                    return { name, availableWindows };
                });
                return {
                    roomNumber: String(data.roomNumber ?? ''),
                    capacity: Number(data.capacity) || 0,
                    week: { days }
                };
            }),
            plannedCourses: plannedCourses.map((p) => {
                const d = p.data || {};
                return {
                    name: d.name || '',
                    presenterName: d.presenterName || '',
                    numberOfListeners: Number(d.numberOfListeners) || 0,
                    durationHours: Number(d.durationHours) || 0,
                    durationMinutes: Number(d.durationMinutes) || 0
                };
            }),
            preferences: preferences.map((pref) => {
                const d = pref.data || {};
                return {
                    strictness: Number(d.strictness) || 0,
                    presenterName: d.presenterName || '',
                    constraint: d.constraint || '',
                    value: d.value === undefined || d.value === null ? '' : d.value
                };
            })
        };

        const blob = new Blob([JSON.stringify(payload, null, 2)], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'timetable-input.json';
        a.click();
        URL.revokeObjectURL(url);
    }

    async function generateFromInputs() {
        generating = true;
        generateResult = null;
        generateError = null;

        try {
            const payload: any = {
                rooms: rooms.map((r) => {
                    const data = r.data || {};
                    const days = (data.days || []).map((d: any) => {
                        const name = typeof d === 'string' ? d : d.value || d.name || '';
                        const availableWindows = (d.timeWindows || []).map((tw: string[]) =>
                            tw.map((t: string) => {
                                const parts = (t || '').split(':').map((x) => Number(x));
                                return { hour: Number.isFinite(parts[0]) ? parts[0] : 0, minute: Number.isFinite(parts[1]) ? parts[1] : 0 };
                            })
                        );
                        return { name, availableWindows };
                    });
                    return {
                        roomNumber: String(data.roomNumber ?? ''),
                        capacity: Number(data.capacity) || 0,
                        week: { days }
                    };
                }),
                plannedCourses: plannedCourses.map((p) => {
                    const d = p.data || {};
                    return {
                        name: d.name || '',
                        presenterName: d.presenterName || '',
                        numberOfListeners: Number(d.numberOfListeners) || 0,
                        durationHours: Number(d.durationHours) || 0,
                        durationMinutes: Number(d.durationMinutes) || 0
                    };
                }),
                preferences: preferences.map((pref) => {
                    const d = pref.data || {};
                    const val = d.value === undefined || d.value === null ? '' : String(d.value);
                    return `${Number(d.strictness) || 0}:${d.presenterName || ''}:${d.constraint || ''}:${val}`;
                })
            };

            const token = localStorage.getItem('token');
            const headers: Record<string, string> = { 'Content-Type': 'application/json' };
            if (token) headers['Authorization'] = `Bearer ${token}`;

            console.debug('Sending /api/generate/simple payload:', payload);

            const res = await fetch(`${PUBLIC_API_URL}/api/generate/simple`, {
                method: 'POST',
                headers,
                body: JSON.stringify(payload)
            });

            const contentType = res.headers.get('content-type') || '';

            if (!res.ok) {
                let errText = '';
                try {
                    const jsonErr = await res.json();
                    errText = typeof jsonErr === 'string' ? jsonErr : JSON.stringify(jsonErr);
                } catch (_) {
                    errText = await res.text().catch(() => res.statusText || '');
                }
                console.warn('Server responded with error', res.status, errText);
                generateError = `Server error: ${res.status} ${errText}`;
            } else if (contentType.includes('application/json')) {
                generateResult = await res.json();
            } else {
                generateResult = await res.text();
            }
        } catch (err) {
            generateError = `Network error: ${err instanceof Error ? err.message : String(err)}`;
        } finally {
            generating = false;
        }

        try {
            if (generateResult) {
                sessionStorage.setItem('lastGenerateResult', JSON.stringify(generateResult));
            }
        } catch (e) {}

        try {
            if (generateResult) {
                await goto('/generate/result');
            }
        } catch (e) {}
    }
</script>

<div class="container py-5">
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <h1 class="h2 mb-3">Generate Timetable</h1>

            {#if generateError}
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    {friendlyError(generateError)}
                    <button type="button" class="btn-close" aria-label="Close" on:click={() => generateError = null}></button>
                </div>
            {/if}

            <p>Provide input data and generate <b>YOUR timetable.</b></p>

            
            <div class="upload-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="mb-0">Upload JSON</h5>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="upload-json">Upload JSON input</label>
                    <input id="upload-json" class="form-control" type="file" accept="application/json" on:change={handleFileChange} />

                    {#if loadSuccess}
                        <div class="alert alert-success alert-dismissible fade show mt-2" role="alert">
                            {loadSuccess}
                            <button type="button" class="btn-close" aria-label="Close" on:click={() => loadSuccess = null}></button>
                        </div>
                    {/if}
                </div>
            </div>

            <p><i><strong>OR</strong></i></p>

            <p class="text-muted mb-4">Manually add input details.</p>
            
            <div class="planned-courses-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <button class="fold-section-btn" type="button" on:click={() => coursesOpen = !coursesOpen} aria-label="Toggle planned courses">
                        <span class="fold-chevron" class:open={coursesOpen}>&#9654;</span>
                        <h5 class="mb-0 d-inline">Planned Courses</h5>
                        {#if !coursesOpen}<span class="fold-count text-muted ms-2">({plannedCourses.length})</span>{/if}
                    </button>
                    <div>
                        <button class="btn btn-sm btn-primary me-2" type="button" on:click={addPlannedCourse}>Add course</button>
                    </div>
                </div>

                {#if coursesOpen}
                    {#if plannedCourses.length === 0}
                        <div class="text-muted mb-2">No planned courses yet. Click "Add course" to create one.</div>
                    {/if}

                    {#each plannedCourses as course, i (course.id)}
                        <div class="planned-course-entry mb-2 border rounded">
                            <div class="item-fold-header d-flex justify-content-between align-items-center" role="button" tabindex="0" on:click={() => toggleCourse(course.id)} on:keydown={(e) => e.key === 'Enter' && toggleCourse(course.id)}>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="fold-chevron" class:open={course.open}>&#9654;</span>
                                    <span class="item-summary">{course.data.name || 'New Course'}{course.data.presenterName ? ' — ' + course.data.presenterName : ''}</span>
                                </div>
                                <button class="btn btn-sm btn-outline-danger" type="button" on:click|stopPropagation={() => removePlannedCourse(course.id)} aria-label="Remove course">Remove</button>
                            </div>
                            {#if course.open}
                                <div class="p-2">
                                    <PlannedCourse
                                        name={course.data.name}
                                        presenterName={course.data.presenterName}
                                        numberOfListeners={course.data.numberOfListeners}
                                        durationHours={course.data.durationHours}
                                        durationMinutes={course.data.durationMinutes}
                                        on:change={(e) => onPlannedCourseChange(i, e)}
                                    />
                                </div>
                            {/if}
                        </div>
                    {/each}
                {/if}
            </div>

            <div class="preferences-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <button class="fold-section-btn" type="button" on:click={() => preferencesOpen = !preferencesOpen} aria-label="Toggle preferences">
                        <span class="fold-chevron" class:open={preferencesOpen}>&#9654;</span>
                        <h5 class="mb-0 d-inline">Preferences</h5>
                        {#if !preferencesOpen}<span class="fold-count text-muted ms-2">({preferences.length})</span>{/if}
                    </button>
                    <div>
                        <button class="btn btn-sm btn-primary me-2" type="button" on:click={addPreference}>Add preference</button>
                    </div>
                </div>

                {#if preferencesOpen}
                    {#if preferences.length === 0}
                        <div class="text-muted mb-2">No preferences yet. Click "Add preference" to create one.</div>
                    {/if}

                    {#each preferences as pref, i (pref.id)}
                        <div class="preference-entry mb-2 border rounded">
                            <div class="item-fold-header d-flex justify-content-between align-items-center" role="button" tabindex="0" on:click={() => togglePreference(pref.id)} on:keydown={(e) => e.key === 'Enter' && togglePreference(pref.id)}>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="fold-chevron" class:open={pref.open}>&#9654;</span>
                                    <span class="item-summary">{pref.data.presenterName || 'New Preference'}{pref.data.constraint ? ' — ' + pref.data.constraint : ''}</span>
                                </div>
                                <button class="btn btn-sm btn-outline-danger" type="button" on:click|stopPropagation={() => removePreference(pref.id)} aria-label="Remove preference">Remove</button>
                            </div>
                            {#if pref.open}
                                <div class="p-2">
                                    <PreferenceInput
                                        strictness={pref.data.strictness}
                                        presenterName={pref.data.presenterName}
                                        constraint={pref.data.constraint as any}
                                        value={pref.data.value}
                                        on:change={(e) => onPreferenceChange(i, e)}
                                    />
                                </div>
                            {/if}
                        </div>
                    {/each}
                {/if}
            </div>

            <div class="rooms-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <button class="fold-section-btn" type="button" on:click={() => roomsOpen = !roomsOpen} aria-label="Toggle rooms">
                        <span class="fold-chevron" class:open={roomsOpen}>&#9654;</span>
                        <h5 class="mb-0 d-inline">Rooms</h5>
                        {#if !roomsOpen}<span class="fold-count text-muted ms-2">({rooms.length})</span>{/if}
                    </button>
                    <div>
                        <button class="btn btn-sm btn-primary me-2" type="button" on:click={addRoom} aria-label="Add rooms">Add room</button>
                    </div>
                </div>

                {#if roomsOpen}
                    {#if rooms.length === 0}
                        <div class="text-muted">No rooms yet. Click "Add room" to add one.</div>
                    {/if}

                    {#each rooms as room, i (room.id)}
                        <div class="room-entry mb-3 border rounded">
                            <div class="item-fold-header d-flex justify-content-between align-items-center" role="button" tabindex="0" on:click={() => toggleRoom(room.id)} on:keydown={(e) => e.key === 'Enter' && toggleRoom(room.id)}>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="fold-chevron" class:open={room.open}>&#9654;</span>
                                    <span class="item-summary">{room.data?.roomNumber || 'New Room'}{room.data?.capacity ? ' — capacity: ' + room.data.capacity : ''}</span>
                                </div>
                                <button class="btn btn-sm btn-outline-danger" type="button" on:click|stopPropagation={() => removeRoom(room.id)} aria-label="Remove room">Remove</button>
                            </div>
                            {#if room.open}
                                <div class="p-2">
                                    <RoomInput
                                        roomNumber={room.data?.roomNumber || ''}
                                        capacity={room.data?.capacity ?? ''}
                                        initialDays={room.data?.days || []}
                                        initialDaysData={room.data?.initialDaysData || []}
                                        on:change={(e) => onRoomChange(i, e)}
                                    />
                                </div>
                            {/if}
                        </div>
                    {/each}
                {/if}
            </div>

            <div class="mt-4 d-flex justify-content-center gap-3">
                <button class="btn btn-outline-secondary shadow pop-button" type="button" on:click={exportJSON}>Export JSON</button>
                <button class="btn btn-primary shadow pop-button" type="button" on:click={generateFromInputs} disabled={generating}>Generate!</button>
            </div>

        </div>
    </div>
</div>

<style>
    :global(main) {
        background-color: rgba(255, 122, 0, 1) !important;
    }
    .rooms-frame {
        border: 1px dashed rgba(0,0,0,0.06);
        padding-top: 1.25rem;
    }

    /* Dark mode adjustments */
    :global(html[data-theme="dark"]) .rooms-frame,
    :global(html[data-theme="dark"]) .planned-courses-frame,
    :global(html[data-theme="dark"]) .preferences-frame,
    :global(html[data-theme="dark"]) .upload-frame {
        background-color: rgba(30, 34, 41, 1) !important;
        border-color: rgba(255,255,255,0.08) !important;
        color: #e6eef6;
    }

    :global(html[data-theme="light"]) .rooms-frame,
    :global(html[data-theme="light"]) .planned-courses-frame,
    :global(html[data-theme="light"]) .preferences-frame,
    :global(html[data-theme="light"]) .upload-frame {
        background-color: rgba(0,0,0,0.05) !important;
        border-color: rgba(0,0,0,0.06) !important;
    }

    .room-entry {
        background: transparent;
    }

    .pop-button {
    transition: transform .15s ease, box-shadow .15s ease;
    }

    .pop-button:hover {
        transform: scale(1.05);
        box-shadow: 0 0.5rem 1rem rgba(0,0,0,.15);
    }

    /* Section fold button */
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

    /* Item fold header */
    .item-fold-header {
        padding: 0.5rem 0.75rem;
        cursor: pointer;
        border-radius: 4px;
        user-select: none;
        transition: background-color 0.15s ease;
    }

    .item-fold-header:hover {
        background-color: rgba(0,0,0,0.04);
    }

    :global(html[data-theme="dark"]) .item-fold-header:hover {
        background-color: rgba(255,255,255,0.05);
    }

    .item-summary {
        font-size: 0.9rem;
        font-weight: 500;
    }

    .fold-count {
        font-size: 0.85rem;
    }

    /* Chevron animation */
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
</style>
