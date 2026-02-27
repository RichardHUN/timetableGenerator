<script lang="ts">
  import { createEventDispatcher } from 'svelte';
  import InfoTooltip from './InfoTooltip.svelte';

  export let times: string[] = [];
  const dispatch = createEventDispatcher();

  function addTime() {
    let newTime = '08:00';
    if (times && times.length) {
      const last = times[times.length - 1] || '08:00';
      const parts = (last || '08:00').split(':').map((x) => Number(x));
      const h = Number.isFinite(parts[0]) ? parts[0] : 8;
      const m = Number.isFinite(parts[1]) ? parts[1] : 0;
      const nextH = (h + 1) % 24;
      newTime = String(nextH).padStart(2, '0') + ':' + String(m).padStart(2, '0');
    }
    times = [...times, newTime];
    dispatch('change', { times });
  }

  function updateTime(index: number, value: string) {
    times = times.map((t, i) => (i === index ? value : t));
    dispatch('change', { times });
  }

  function removeTime(index: number) {
    times = times.filter((_, i) => i !== index);
    dispatch('change', { times });
  }
</script>

<div class="time-window-component timewindow-frame p-2">
  <div class="time-list">
    {#each times as time, i}
      <div class="time-item d-flex align-items-center mb-1">
        <input
          class="form-control form-control-sm"
          type="time"
          value={time}
          on:input={(e) => updateTime(i, (e.target as HTMLInputElement).value)}
          aria-label="Start time"
          title="Format: HH:MM (e.g., 08:00)"
        />
        <button class="btn btn-sm btn-outline-danger ms-2" on:click={() => removeTime(i)} aria-label="Remove time">−</button>
      </div>
    {/each}
  </div>

  <div class="mt-2">
    <button class="btn btn-sm btn-outline-secondary" on:click={addTime} aria-label="Add start time">+ Add Time</button>
    <InfoTooltip>
      <h6 class="card-title mb-2">How to use Times</h6>
      <ul class="mb-0 ps-3">
        <li>Times represent start time</li>
        <li>The end of the day is one hour after the last start time</li>
      </ul>
    </InfoTooltip>
  </div>
</div>

<style>
  :global(html[data-theme="dark"]) .time-window-component input.form-control {
    background: #0f1720;
    color: #e6eef6;
    border-color: rgba(255,255,255,0.06);
  }

  :global(html[data-theme="light"]) .time-window-component input.form-control {
    background: #fff;
    color: #0b1220;
  }

  .time-list {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
  }

  .time-item input.form-control-sm {
    width: 6.5rem;
  }

  .timewindow-frame {
    border: 1px solid rgba(16,24,40,0.06);
    border-radius: 6px;
    background: var(--bs-body-bg, transparent);
  }

  :global(html[data-theme="dark"]) .timewindow-frame {
    border-color: rgba(255,255,255,0.04);
    background: rgba(255,255,255,0.01);
  }
</style>
