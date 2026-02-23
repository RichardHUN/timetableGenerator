<svelte:head>
    <title>Generate - Timetable Generator</title>
</svelte:head>

<script lang="ts">
    import RoomInput from '$lib/RoomInput.svelte';
    import PlannedCourse from '$lib/PlannedCourse.svelte';
    import PreferenceInput from '$lib/PreferenceInput.svelte';
    import { PUBLIC_API_URL } from '$env/static/public';
    import { goto } from '$app/navigation';

    type RoomState = { id: number; data: any };
    type PlannedCourseState = { id: number; data: { name: string; presenterName: string; numberOfListeners: number; durationHours: number; durationMinutes: number } };
    type PreferenceState = { id: number; data: { strictness: number; presenterName: string; constraint: string; value: any } };

    let rooms: RoomState[] = [];
    let plannedCourses: PlannedCourseState[] = [];
    let preferences: PreferenceState[] = [];

    let generating = false;
    let generateResult: any = null;
    let generateError: string | null = null;

    function addRoom() {
        rooms = [...rooms, { id: Date.now(), data: {} }];
    }

    function addPlannedCourse() {
        plannedCourses = [...plannedCourses, { id: Date.now(), data: { name: '', presenterName: '', numberOfListeners: 0, durationHours: 0, durationMinutes: 0 } }];
    }

    function addPreference() {
        preferences = [...preferences, { id: Date.now(), data: { strictness: 3, presenterName: '', constraint: 'noClassesOnDay', value: '' } }];
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

        generating = true;
        generateResult = null;
        generateError = null;

            try {
            const text = await file.text();

            // validate JSON locally first to catch parse errors before sending
            let payload: any = null;
            try {
                payload = JSON.parse(text);
            } catch (parseErr) {
                generateError = `Invalid JSON file: ${parseErr instanceof Error ? parseErr.message : String(parseErr)}`;
                generating = false;
                if (input) input.value = '';
                return;
            }

            const token = localStorage.getItem('token');
            const headers: Record<string, string> = { 'Content-Type': 'application/json' };
            if (token) headers['Authorization'] = `Bearer ${token}`;

            // helpful debug log for developer console
            console.debug('Sending /api/generate payload:', payload);

            const res = await fetch(`${PUBLIC_API_URL}/api/generate`, {
                method: 'POST',
                headers,
                body: JSON.stringify(payload)
            });

            const contentType = res.headers.get('content-type') || '';

            if (!res.ok) {
                // try to parse response body as JSON for better error details
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
            if (input) input.value = '';
        }

        // persist last result so the /generate/result page can read it
        try {
            if (generateResult) {
                sessionStorage.setItem('lastGenerateResult', JSON.stringify(generateResult));
            }
        } catch (e) {
            // ignore storage errors
        }

        // automatically navigate to the result page when a result is available
        try {
            if (generateResult) {
                await goto('/generate/result');
            }
        } catch (e) {
            // ignore navigation errors
        }
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
            <p>Provide input data and generate <b>YOUR timetable.</b></p>

            
            <div class="upload-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="mb-0">Upload JSON</h5>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="upload-json">Upload JSON input</label>
                    <input id="upload-json" class="form-control" type="file" accept="application/json" on:change={handleFileChange} />

                    {#if generating}
                        <div class="text-muted mt-2">Sending JSON to server...</div>
                    {/if}

                    {#if generateError}
                        <div class="alert alert-danger mt-2">{generateError}</div>
                    {/if}

                    {#if generateResult}
                        <div class="card mt-2">
                            <div class="card-body">
                                <h6 class="mb-2">Server response</h6>
                                <pre style="white-space:pre-wrap;word-break:break-word">{typeof generateResult === 'string' ? generateResult : JSON.stringify(generateResult, null, 2)}</pre>
                                <div class="mt-3">
                                    <a class="btn btn-sm btn-outline-secondary" href="/generate/result">Open result page</a>
                                </div>
                            </div>
                        </div>
                    {/if}
                </div>
            </div>

            <p><i><strong>OR</strong></i></p>

            <p class="text-muted mb-4">Manually add input details.</p>
            
            <div class="planned-courses-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="mb-0">Planned Courses</h5>
                    <div>
                        <button class="btn btn-sm btn-primary me-2" type="button" on:click={addPlannedCourse}>Add course</button>
                    </div>
                </div>

                {#if plannedCourses.length === 0}
                    <div class="text-muted mb-2">No planned courses yet. Click "Add course" to create one.</div>
                {/if}

                {#each plannedCourses as course, i (course.id)}
                    <div class="planned-course-entry mb-2 p-2 border rounded">
                        <div class="d-flex justify-content-end mb-2">
                            <button class="btn btn-sm btn-outline-danger" type="button" on:click={() => removePlannedCourse(course.id)} aria-label="Remove course">Remove</button>
                        </div>
                        <PlannedCourse
                            name={course.data.name}
                            presenterName={course.data.presenterName}
                            numberOfListeners={course.data.numberOfListeners}
                            durationHours={course.data.durationHours}
                            durationMinutes={course.data.durationMinutes}
                            on:change={(e) => onPlannedCourseChange(i, e)}
                        />
                    </div>
                {/each}
            </div>

            <div class="preferences-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <h5 class="mb-0">Preferences</h5>
                    <div>
                        <button class="btn btn-sm btn-primary me-2" type="button" on:click={addPreference}>Add preference</button>
                    </div>
                </div>

                {#if preferences.length === 0}
                    <div class="text-muted mb-2">No preferences yet. Click "Add preference" to create one.</div>
                {/if}

                {#each preferences as pref, i (pref.id)}
                    <div class="preference-entry mb-2 p-2 border rounded">
                        <div class="d-flex justify-content-end mb-2">
                            <button class="btn btn-sm btn-outline-danger" type="button" on:click={() => removePreference(pref.id)} aria-label="Remove preference">Remove</button>
                        </div>
                        <PreferenceInput
                            strictness={pref.data.strictness}
                            presenterName={pref.data.presenterName}
                            constraint={pref.data.constraint}
                            value={pref.data.value}
                            on:change={(e) => onPreferenceChange(i, e)}
                        />
                    </div>
                {/each}
            </div>

            <div class="rooms-frame card mt-3 p-3">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="mb-0">Rooms</h5>
                    <div>
                        <button class="btn btn-sm btn-primary me-2" type="button" on:click={addRoom} aria-label="Add rooms">Add room</button>
                    </div>
                </div>

                {#if rooms.length === 0}
                    <div class="text-muted">No rooms yet. Click "Add room" to add one.</div>
                {/if}

                {#each rooms as room, i (room.id)}
                    <div class="room-entry mb-3 p-2 border rounded">
                        <RoomInput
                            roomNumber={room.data?.roomNumber || ''}
                            capacity={room.data?.capacity ?? ''}
                            initialDays={room.data?.days || []}
                            on:change={(e) => onRoomChange(i, e)}
                        />
                        <div class="d-flex justify-content-end mb-2">
                            <button class="btn btn-sm btn-outline-danger" type="button" on:click={() => removeRoom(room.id)} aria-label="Remove room">Remove</button>
                        </div>
                    </div>
                {/each}
            </div>

            <div class="mt-4 d-flex justify-content-center">
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
</style>
