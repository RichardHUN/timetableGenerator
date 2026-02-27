<script lang="ts">
  import TimeWindow from './TimeWindow.svelte';
  import InfoTooltip from './InfoTooltip.svelte';
  import { createEventDispatcher } from 'svelte';

  export let label: string = 'Day';
  // textual value that parent components can bind to
  export let value: string = '';
  export let placeholder: string = '';
  export let timeWindows: string[][] = [];

  const dispatch = createEventDispatcher();

  function addTimeWindow() {
    // default new window: a single time = previous window's last time + 1 hour
    const prev = timeWindows.length ? timeWindows[timeWindows.length - 1] : null;
    let defaultTime = '08:00';
    if (prev && prev.length) {
      const last = prev[prev.length - 1] || '08:00';
      const parts = (last || '08:00').split(':').map((x) => Number(x));
      const h = Number.isFinite(parts[0]) ? parts[0] : 8;
      const m = Number.isFinite(parts[1]) ? parts[1] : 0;
      const nextH = (h + 1) % 24;
      defaultTime = String(nextH).padStart(2, '0') + ':' + String(m).padStart(2, '0');
    }

    timeWindows = [...timeWindows, [defaultTime]];
  }

  function updateWindow(index: number, times: string[]) {
    timeWindows = timeWindows.map((tw, i) => (i === index ? times : tw));
  }

  function removeWindow(index: number) {
    timeWindows = timeWindows.filter((_, i) => i !== index);
  }

  // notify parent when value or timeWindows change
  $: dispatch('change', { value, timeWindows });

</script>

<div class="day-input day-frame mb-2 p-3">
  <label class="form-label">{label} name</label>
  <input
    class="form-control mb-2"
    type="text"
    bind:value={value}
    placeholder={placeholder || 'Day name (e.g., Monday)'}
    aria-label="Day name"
  />

  <div class="time-windows">
    {#each timeWindows as times, i}
      <div class="time-window d-flex align-items-start mb-2">
        <TimeWindow {times} on:change={(e) => updateWindow(i, e.detail.times)} />
        <button class="btn btn-sm btn-outline-danger ms-2" on:click={() => removeWindow(i)} aria-label="Remove time window">Remove</button>
      </div>
    {/each}
  </div>

  <div class="mt-2 d-flex align-items-center gap-2" style="position: relative;">
    <button class="btn btn-sm btn-outline-primary" on:click={addTimeWindow}>+ Time Window</button>
    <InfoTooltip>
      <h6 class="card-title mb-2">How to use Time Windows</h6>
      <ul class="mb-0 ps-3">
        <li>Time Windows represent a continuous time range</li>
        <li>If there are no breaks in the day, define only one Time Window</li>
      </ul>
    </InfoTooltip>
  </div>
</div>

<style>
  :global(html[data-theme="dark"]) .day-input input.form-control {
    background: #0f1720;
    color: #e6eef6;
    border-color: rgba(255,255,255,0.06);
  }

  :global(html[data-theme="light"]) .day-input input.form-control {
    background: #fff;
    color: #0b1220;
  }

  .form-label {
    font-size: 0.9rem;
    margin-bottom: 0.25rem;
  }

  .time-window :global(.time-list) {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
  }

  .time-window button[aria-label="Remove time window"] {
    height: 2.25rem;
  }

  .day-frame {
    border: 1px solid rgba(16,24,40,0.08);
    border-radius: 8px;
    /*background: var(--bs-body-bg, transparent);*/
    background: rgba(7, 16, 26, 0.1);
    box-shadow: 0 1px 2px rgba(16,24,40,0.03);
  }

  :global(html[data-theme="dark"]) .day-frame {
    border-color: rgba(255,255,255,0.06);
    background: #07101a;
  }

</style>
