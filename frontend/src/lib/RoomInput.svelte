<script lang="ts">
	import DayInput from '$lib/DayInput.svelte';
	import { createEventDispatcher, onMount } from 'svelte';

	export let roomNumber: string = '';
	export let capacity: number | '' = '';
	// optional initial days passed from parent (array of strings)
	export let initialDays: string[] = [];

	// each day now includes textual `value` and `timeWindows` array
	let days: Array<{ id: number; value: string; timeWindows: string[][] }> = [];
	const dispatch = createEventDispatcher();
	const uid = Math.random().toString(36).slice(2, 9);
	const roomId = `room-${uid}`;
	const capacityId = `capacity-${uid}`;

	onMount(() => {
		if (initialDays && initialDays.length) {
			days = initialDays.map((v, i) => ({ id: Date.now() + i, value: v, timeWindows: [] }));
		} else {
			days = [{ id: Date.now(), value: '', timeWindows: [] }];
		}
	});

	function addDay() {
		days = [...days, { id: Date.now(), value: '', timeWindows: [] }];
		dispatch('change', { roomNumber, capacity, days });
	}

	function removeDay(id: number) {
		days = days.filter((d) => d.id !== id);
		dispatch('change', { roomNumber, capacity, days });
	}

	// notify parent when basic fields change
	$: dispatch('change', { roomNumber, capacity, days });
</script>

<div class="room-input card p-3 mb-3">
	<div class="row g-2 align-items-end">
		<div class="col-sm-6">
			<label class="form-label" for={roomId}>Room number</label>
			<input
				id={roomId}
				class="form-control"
				type="text"
				bind:value={roomNumber}
				placeholder="e.g., A101"
				aria-label="Room number"
			/>
		</div>

		<div class="col-sm-3">
			<label class="form-label" for={capacityId}>Capacity</label>
			<input
				id={capacityId}
				class="form-control"
				type="number"
				min="0"
				bind:value={capacity}
				aria-label="Capacity"
			/>
		</div>

		<div class="col-sm-3 text-end">
			<button class="btn btn-outline-primary" type="button" on:click={addDay} aria-label="Add day">
				+ Add Day
			</button>
		</div>
	</div>

	<div class="mt-3">
        <h5 class="mb-0">Days</h5>
		{#each days as day, i (day.id)}
			<div class="d-flex align-items-start gap-2 mb-2">
				<DayInput bind:value={days[i].value} bind:timeWindows={days[i].timeWindows} label={`Day ${i + 1}`} placeholder="Describe the day (e.g., Mon 9–11: Math)" on:change={() => dispatch('change', { roomNumber, capacity, days })} />
				<button class="btn btn-outline-danger btn-sm" type="button" on:click={() => removeDay(day.id)} aria-label="Remove day">
					✖
				</button>
			</div>
		{/each}
	</div>
</div>

<style>
	/* Card background adapts to project's data-theme */
	:global(html[data-theme="dark"]) .room-input.card {
		background: rgba(10,12,15,0.6);
		border: 1px solid rgba(255,255,255,0.04);
		color: #e6eef6;
	}

	:global(html[data-theme="light"]) .room-input.card {
		background: #ffffff;
		border: 1px solid rgba(0,0,0,0.06);
		color: #0b1220;
	}

	.form-label {
		font-size: 0.9rem;
		margin-bottom: 0.25rem;
	}

	.btn-outline-primary {
		min-width: 110px;
	}
</style>
