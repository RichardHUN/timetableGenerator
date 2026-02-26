<script lang="ts">
    import { createEventDispatcher, onMount } from 'svelte';

    export let name: string = '';
    export let presenterName: string = '';
    export let numberOfListeners: number = 0;
    export let durationHours: number = 0;
    export let durationMinutes: number = 0;

    let uid: string = '';

    onMount(() => {
        uid = (typeof crypto !== 'undefined' && 'randomUUID' in crypto)
            ? (crypto as any).randomUUID()
            : Math.random().toString(36).slice(2);
    });

    const dispatch = createEventDispatcher();

    function emitChange() {
        dispatch('change', {
            name,
            presenterName,
            numberOfListeners: Number(numberOfListeners) || 0,
            durationHours: Number(durationHours) || 0,
            durationMinutes: Number(durationMinutes) || 0
        });
    }
</script>

<div class="planned-course">
    <div class="row g-2 align-items-end">
        <div class="col-md-4">
              <label class="form-label" for={uid + '-name'}>Course name</label>
            <input id={uid + '-name'} class="form-control" type="text" bind:value={name} on:input={emitChange} />
        </div>

        <div class="col-md-4">
              <label class="form-label" for={uid + '-presenter'}>Presenter</label>
            <input id={uid + '-presenter'} class="form-control" type="text" bind:value={presenterName} on:input={emitChange} />
        </div>

        <div class="col-md-2">
              <label class="form-label" for={uid + '-listeners'}>Listeners</label>
            <input id={uid + '-listeners'} class="form-control" type="number" min="0" bind:value={numberOfListeners} on:input={emitChange} />
        </div>

        <div class="col-md-1">
              <label class="form-label" for={uid + '-hours'}>Hours</label>
            <input id={uid + '-hours'} class="form-control" type="number" min="0" bind:value={durationHours} on:input={emitChange} />
        </div>

        <div class="col-md-1">
              <label class="form-label" for={uid + '-mins'}>Mins</label>
            <input id={uid + '-mins'} class="form-control" type="number" min="0" max="59" bind:value={durationMinutes} on:input={emitChange} />
        </div>
    </div>
</div>

<style>
    .planned-course {
        padding: 0.5rem;
    }
</style>
