<svelte:head>
    <title>My Timetables - Timetable Generator</title>
</svelte:head>

<script lang="ts">
    import { onMount } from 'svelte';
    import { getTimetables, deleteTimetable, renameTimetable } from '$lib/api';
    import type { TimetableListItem } from '$lib/api';

    function focusOnMount(node: HTMLElement) { node.focus(); }

    let timetables: TimetableListItem[] = [];
    let loading = true;
    let pageError: string | null = null;
    let actionError: string | null = null;

    // Delete modal
    let pendingDeleteId: string | null = null;
    let pendingDeleteName = '';

    // Rename state: id of the item currently being renamed
    let renamingId: string | null = null;
    let renameValue = '';

    function formatDate(iso: string): string {
        try {
            return new Date(iso).toLocaleString(undefined, {
                year: 'numeric', month: 'short', day: 'numeric',
                hour: '2-digit', minute: '2-digit'
            });
        } catch {
            return iso;
        }
    }

    onMount(async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            pageError = 'You must be logged in to view your timetables. Please log in and try again.';
            loading = false;
            return;
        }
        const res = await getTimetables(token);
        if (res.error) {
            pageError = res.error;
        } else {
            timetables = res.data ?? [];
        }
        loading = false;
    });

    function startRename(item: TimetableListItem) {
        renamingId = item.id;
        renameValue = item.name;
    }

    async function confirmRename(item: TimetableListItem) {
        const trimmed = renameValue.trim();
        if (!trimmed || trimmed === item.name) {
            renamingId = null;
            return;
        }
        const token = localStorage.getItem('token') ?? '';
        const res = await renameTimetable(item.id, trimmed, token);
        if (res.error) {
            actionError = res.error;
        } else {
            timetables = timetables.map(t => t.id === item.id ? { ...t, name: trimmed } : t);
        }
        renamingId = null;
    }

    function cancelRename() {
        renamingId = null;
    }

    function openDeleteModal(item: TimetableListItem) {
        pendingDeleteId = item.id;
        pendingDeleteName = item.name;
    }

    function closeDeleteModal() {
        pendingDeleteId = null;
        pendingDeleteName = '';
    }

    async function confirmDelete() {
        if (!pendingDeleteId) return;
        const token = localStorage.getItem('token') ?? '';
        const res = await deleteTimetable(pendingDeleteId, token);
        if (res.error) {
            actionError = res.error;
        } else {
            timetables = timetables.filter(t => t.id !== pendingDeleteId);
        }
        closeDeleteModal();
    }
</script>

<div class="container py-5">
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <h1 class="h2 mb-4 page-title">My Timetables</h1>

            {#if pageError}
                <div class="alert alert-warning">{pageError}</div>
            {:else if loading}
                <div class="text-center py-5">
                    <div class="spinner-border text-accent" role="status">
                        <span class="visually-hidden">Loading…</span>
                    </div>
                </div>
            {:else}
                {#if actionError}
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        {actionError}
                        <button type="button" class="btn-close" aria-label="Close" on:click={() => actionError = null}></button>
                    </div>
                {/if}

                {#if timetables.length === 0}
                    <div class="alert alert-info">No timetables yet. <a href="/generate">Generate one!</a></div>
                {:else}
                    <ul class="list-group list-group-flush">
                        {#each timetables as item (item.id)}
                            <li class="list-group-item timetable-row">
                                <div class="d-flex align-items-center gap-3 flex-wrap">
                                    <!-- Name / rename input -->
                                    <div class="flex-grow-1 min-w-0">
                                        {#if renamingId === item.id}
                                            <input
                                                class="form-control form-control-sm rename-input"
                                                type="text"
                                                bind:value={renameValue}
                                                on:keydown={(e) => { if (e.key === 'Enter') confirmRename(item); if (e.key === 'Escape') cancelRename(); }}
                                                on:blur={() => confirmRename(item)}
                                                use:focusOnMount
                                            />
                                        {:else}
                                            <span class="timetable-name">{item.name}</span>
                                            <span class="timetable-date text-muted ms-2">{formatDate(item.createdAt)}</span>
                                        {/if}
                                    </div>
                                    <!-- Actions -->
                                    <div class="d-flex gap-2 flex-shrink-0">
                                        <a class="btn btn-sm btn-primary" href="/result?id={item.id}">Open</a>
                                        <button class="btn btn-sm btn-outline-secondary" type="button" on:click={() => startRename(item)} title="Rename">
                                            ✏️
                                        </button>
                                        <button class="btn btn-sm btn-outline-danger" type="button" on:click={() => openDeleteModal(item)} title="Delete">
                                            🗑️
                                        </button>
                                    </div>
                                </div>
                            </li>
                        {/each}
                    </ul>
                {/if}
            {/if}
        </div>
    </div>
</div>

<!-- Delete confirmation modal -->
{#if pendingDeleteId}
    <!-- svelte-ignore a11y-click-events-have-key-events a11y-no-static-element-interactions -->
    <div class="modal-backdrop-custom" on:click={closeDeleteModal}></div>
    <div class="modal d-block" tabindex="-1" role="dialog" aria-modal="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Delete Timetable</h5>
                    <button type="button" class="btn-close" aria-label="Close" on:click={closeDeleteModal}></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete <strong>{pendingDeleteName}</strong>? This action cannot be undone.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" on:click={closeDeleteModal}>Cancel</button>
                    <button type="button" class="btn btn-danger" on:click={confirmDelete}>Delete</button>
                </div>
            </div>
        </div>
    </div>
{/if}

<style>
    :global(main) { background-color: rgba(255, 122, 0, 1) !important; }

    .page-title { color: var(--accent); font-weight: 700; border-bottom: 2px solid var(--accent); padding-bottom: 0.4rem; display: inline-block; }

    .timetable-row { transition: background-color 0.15s ease; }
    .timetable-row:hover { background-color: rgba(0,0,0,0.03); }

    .timetable-name { font-weight: 600; font-size: 1rem; }
    .timetable-date { font-size: 0.82rem; }

    .rename-input { max-width: 360px; }

    .modal-backdrop-custom {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.5);
        z-index: 1040;
    }

    .modal { z-index: 1050; }

    /* Dark mode */
    :global(html[data-theme="dark"]) .card { background-color: rgba(30,34,41,1) !important; border-color: rgba(255,255,255,0.1) !important; color: #e6eef6 !important; }
    :global(html[data-theme="dark"]) .list-group-item { background-color: rgba(30,34,41,1) !important; border-color: rgba(255,255,255,0.1) !important; color: #e6eef6 !important; }
    :global(html[data-theme="dark"]) .timetable-row:hover { background-color: rgba(255,255,255,0.04) !important; }
    :global(html[data-theme="dark"]) .modal-content { background-color: rgba(30,34,41,1) !important; color: #e6eef6 !important; border-color: rgba(255,255,255,0.15) !important; }
    :global(html[data-theme="dark"]) .modal-header { border-bottom-color: rgba(255,255,255,0.12) !important; }
    :global(html[data-theme="dark"]) .modal-footer { border-top-color: rgba(255,255,255,0.12) !important; }
    :global(html[data-theme="dark"]) .timetable-date { color: #94a3b8 !important; }
</style>
