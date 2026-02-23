<svelte:head>
    <title>Generate Result - Timetable Generator</title>
</svelte:head>

<script lang="ts">
    import { onMount } from 'svelte';
    let result: any = null;
    let error: string | null = null;

    onMount(() => {
        try {
            const raw = sessionStorage.getItem('lastGenerateResult');
            if (raw) {
                result = JSON.parse(raw);
            } else {
                error = 'No result found. Generate something first.';
            }
        } catch (e) {
            error = 'Failed to read or parse stored result.';
        }
    });
</script>

<div class="container py-5">
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <h1 class="h2 mb-3">Generate Result</h1>

            {#if error}
                <div class="alert alert-warning">{error}</div>
            {/if}

            {#if result}
                <div class="mb-3">
                    <h6>Raw JSON response</h6>
                    <pre style="white-space:pre-wrap;word-break:break-word">{JSON.stringify(result, null, 2)}</pre>
                </div>
            {/if}

            <div>
                <a class="btn btn-outline-secondary" href="/generate">Back to generate</a>
            </div>
        </div>
    </div>
</div>
