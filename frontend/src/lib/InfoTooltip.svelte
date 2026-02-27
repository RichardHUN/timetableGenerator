<script lang="ts">
  let visible = false;
</script>

<span
  class="info-icon"
  role="button"
  tabindex="0"
  aria-label="Help"
  on:mouseenter={() => (visible = true)}
  on:mouseleave={() => (visible = false)}
  on:focusin={() => (visible = true)}
  on:focusout={() => (visible = false)}
>ℹ
  {#if visible}
    <div class="info-card card shadow-sm">
      <div class="card-body p-3">
        <slot />
      </div>
    </div>
  {/if}
</span>

<style>
  .info-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 1.35rem;
    height: 1.35rem;
    border-radius: 50%;
    background: rgba(13, 110, 253, 0.12);
    color: #0d6efd;
    font-size: 0.78rem;
    font-style: normal;
    font-weight: 700;
    cursor: default;
    user-select: none;
    border: 1px solid rgba(13, 110, 253, 0.25);
    position: relative;
    flex-shrink: 0;
  }

  :global(html[data-theme="dark"]) .info-icon {
    background: rgba(100, 160, 255, 0.12);
    color: #7db7ff;
    border-color: rgba(100, 160, 255, 0.25);
  }

  .info-card {
    position: absolute;
    z-index: 200;
    min-width: 16rem;
    max-width: 22rem;
    top: calc(100% + 0.4rem);
    left: 0;
    font-size: 0.85rem;
    pointer-events: none;
    white-space: normal;
  }

  :global(html[data-theme="dark"]) .info-card {
    background: #1a2233;
    color: #c8d8ee;
    border-color: rgba(255, 255, 255, 0.1);
  }
</style>
