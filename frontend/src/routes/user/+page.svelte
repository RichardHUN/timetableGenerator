<script lang="ts">
    import { onMount } from 'svelte';
    import { PUBLIC_API_URL } from '$env/static/public';

    let loading = true;
    let error: string | null = null;
    let data: any = null;
    let username: string | null = null;
    let email: string | null = null;
    let roles: string[] = [];

    onMount(async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            // no token -> go to login
            window.location.href = '/login';
            return;
        }

        try {
            const res = await fetch(`${PUBLIC_API_URL}/api/user`, {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });

            if (!res.ok) {
                const body = await res.json().catch(() => null);
                error = body?.message || `Request failed: ${res.status}`;
                loading = false;
                return;
            }

            data = await res.json().catch(() => null);
            if (data) {
                // extract common fields returned by backend
                username = data.username ?? data.userName ?? null;
                email = data.email ?? null;
                // roles can be an array of objects like { authority: 'ROLE_USER' }
                if (Array.isArray(data.roles)) {
                    roles = data.roles.map((r: any) => r.authority ?? r.role ?? String(r));
                } else if (Array.isArray(data.authorities)) {
                    roles = data.authorities.map((r: any) => r.authority ?? String(r));
                }
            }
        } catch (err) {
            error = err instanceof Error ? err.message : 'Network error';
        } finally {
            loading = false;
        }
    });

    function logout() {
        localStorage.removeItem('token');
        window.location.href = '/login';
    }
</script>

<svelte:head>
    <title>User - Timetable Generator</title>
</svelte:head>

<div class="min-vh-100 gradient-bg py-5">
    <div class="container">
        <div class="card shadow-lg" style="max-width: 900px; margin: 0 auto;">
            <div class="card-body p-4">
                {#if loading}
                    <div class="text-center py-4">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Loading...</span>
                        </div>
                        <p class="mt-2">Loading user data...</p>
                    </div>
                {:else}
                    {#if error}
                        <div class="alert alert-danger" role="alert">{error}</div>
                        <div class="mt-3"><a href="/login" class="btn btn-primary">Go to login</a></div>
                    {:else}
                        <h1 class="h3 mb-4">User Information</h1>
                        {#if username || email}
                            <div class="mb-4">
                                {#if username}
                                    <div class="mb-2"><strong>Username:</strong> {username}</div>
                                {/if}
                                {#if email}
                                    <div class="mb-2"><strong>Email:</strong> {email}</div>
                                {/if}
                                {#if roles.length}
                                    <div class="mt-3">
                                        <strong>Roles:</strong>
                                        <ul class="list-group list-group-flush mt-2">
                                            {#each roles as r}
                                                <li class="list-group-item">{r}</li>
                                            {/each}
                                        </ul>
                                    </div>
                                {/if}
                            </div>
                        {:else}
                            <div class="alert alert-info">No user fields found in response.</div>
                            <pre class="bg-light p-3 rounded">{JSON.stringify(data, null, 2)}</pre>
                        {/if}

                        <button class="btn btn-primary mt-3" onclick={logout}>Logout</button>
                    {/if}
                {/if}
            </div>
        </div>
    </div>
</div>

<style>
    .gradient-bg {
        background: linear-gradient(180deg, #ff9b3d 0%, #ff7a00 100%);
    }
</style>
