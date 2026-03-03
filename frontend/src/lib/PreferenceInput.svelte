<script lang="ts">
    import { createEventDispatcher, onMount } from 'svelte';

    export type ConstraintType = 'noClassesOnDay' | 'maxClassesPerDay' | 'noClassesAfter' | 'noClassesBefore';

    export let strictness: number = 3;
    export let presenterName: string = '';
    export let constraint: ConstraintType = 'noClassesOnDay';
    export let value: any = '';

    let uid = '';
    // use a local string to bind to the <select> so the UI shows correctly
    let strictnessStr: string = String(strictness);

    // keep local string in sync when parent updates the numeric prop
    $: if (String(strictness) !== strictnessStr) strictnessStr = String(strictness);
    const dispatch = createEventDispatcher();

    onMount(() => {
        uid = (typeof crypto !== 'undefined' && 'randomUUID' in crypto)
            ? (crypto as any).randomUUID()
            : Math.random().toString(36).slice(2);
    });

    function emitChange() {
        dispatch('change', { strictness, presenterName, constraint, value });
    }

    function onConstraintChange(e: Event) {
        const sel = (e.target as HTMLSelectElement).value as ConstraintType;
        constraint = sel;
        // reset value to a sensible default for the chosen constraint
        if (constraint === 'noClassesAfter' || constraint === 'noClassesBefore') {
            value = '08:00';
        } else if (constraint === 'maxClassesPerDay') {
            value = 1;
        } else {
            value = '';
        }
        emitChange();
    }

    function onValueInput(e: Event) {
        const input = e.target as HTMLInputElement;
        if (constraint === 'maxClassesPerDay') {
            value = Number(input.value);
        } else {
            value = input.value;
        }
        emitChange();
    }

    function onStrictnessChange(e: Event) {
        const val = (e.target as HTMLSelectElement).value;
        strictnessStr = val;
        strictness = Number(val);
        emitChange();
    }

    function onPresenterInput(e: Event) {
        presenterName = (e.target as HTMLInputElement).value;
        emitChange();
    }
</script>

<div class="preference-input">
    <div class="row g-2 align-items-end">
        <div class="col-md-2">
            <label class="form-label" for={uid + '-strict'}>Strictness</label>
            <select id={uid + '-strict'} class="form-select" bind:value={strictnessStr} on:change={onStrictnessChange}>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
            </select>
        </div>

        <div class="col-md-3">
            <label class="form-label" for={uid + '-presenter'}>Presenter</label>
            <input id={uid + '-presenter'} class="form-control" type="text" bind:value={presenterName} on:input={onPresenterInput} />
        </div>

        <div class="col-md-3">
            <label class="form-label" for={uid + '-constraint'}>Constraint</label>
            <select id={uid + '-constraint'} class="form-select" bind:value={constraint} on:change={onConstraintChange}>
                <option value="noClassesOnDay">noClassesOnDay</option>
                <option value="maxClassesPerDay">maxClassesPerDay</option>
                <option value="noClassesAfter">noClassesAfter</option>
                <option value="noClassesBefore">noClassesBefore</option>
            </select>
        </div>

        <div class="col-md-4">
            <label class="form-label" for={uid + '-value'}>Value</label>

            {#if constraint === 'noClassesAfter' || constraint === 'noClassesBefore'}
                <input id={uid + '-value'} class="form-control" type="time" bind:value={value} on:input={onValueInput} />
            {:else if constraint === 'maxClassesPerDay'}
                <input id={uid + '-value'} class="form-control" type="number" min="0" bind:value={value} on:input={onValueInput} />
            {:else}
                <input id={uid + '-value'} class="form-control" type="text" bind:value={value} on:input={onValueInput} />
            {/if}
        </div>
    </div>
</div>

<style>
    .preference-input {
        padding: 0.5rem;
    }
</style>
