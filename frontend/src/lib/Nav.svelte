<script lang="ts">
    import { onMount } from 'svelte';
    import { browser } from '$app/environment';

    let theme = $state<'dark' | 'light'>('dark');

    function applyTheme(t: 'dark' | 'light') {
        theme = t;
        if (browser) {
            document.documentElement.setAttribute('data-theme', t);
            localStorage.setItem('theme', t);
        }
    }

    function toggleTheme() {
        applyTheme(theme === 'dark' ? 'light' : 'dark');
    }

    onMount(() => {
        const saved = localStorage.getItem('theme') as 'dark' | 'light' | null;
        if (saved) {
            applyTheme(saved);
        } else if (window.matchMedia('(prefers-color-scheme: light)').matches) {
            applyTheme('light');
        } else {
            applyTheme('dark');
        }
    });
</script>

<nav class="navbar navbar-expand-lg navbar-themed py-3">
    <div class="container">
        <a class="navbar-brand fw-bold" href="/">TimeTable Generator</a>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-2">
                <li class="nav-item">
                    <a href="/" class="nav-link">Home</a>
                </li>
                <li class="nav-item">
                    <a href="/generate" class="nav-link">Generate</a>
                </li>
                <li class="nav-item">
                    <a href="/login" class="nav-link">Login</a>
                </li>
                <li class="nav-item">
                    <a href="/register" class="btn btn-primary">Register</a>
                </li>
                <li class="nav-item">
                    <button class="btn btn-outline-secondary" aria-label="Toggle theme" onclick={toggleTheme}>
                        {#if theme === 'dark'}
                            ☀️
                        {:else}
                            🌙
                        {/if}
                    </button>
                </li>
                <li class="nav-item">
                    <a href="/user" class="btn btn-outline-secondary" aria-label="User">👤</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<style>
    .navbar-themed {
        backdrop-filter: blur(6px);
    }

    /* Dark mode navbar */
    :global(html[data-theme="dark"]) nav.navbar.navbar-themed {
        /* slightly lighter than the page background for visual separation */
        background: linear-gradient(180deg, rgba(30,34,40,0.95), rgba(255, 122, 0,0.2));
        background-color: rgba(40,44,52,0.98) !important;
        border-bottom: 1px solid rgba(255,255,255,0.06);
        color: #e6eef6;
    }

    /* Light mode navbar */
    :global(html[data-theme="light"]) .navbar-themed {
        background: linear-gradient(180deg, rgba(255, 122, 0,0.225), rgba(40,44,52,0.15));
        border-bottom: 1px solid rgba(0,0,0,0.1);
    }

    .navbar-brand {
        color: var(--accent) !important;
        letter-spacing: 0.4px;
    }

    /* Fix navbar toggler icon for light mode */
    .navbar-toggler-icon {
        filter: invert(var(--bs-body-color-rgb));
    }

    :global(html[data-theme="light"]) .navbar-toggler-icon {
        filter: invert(1);
    }
</style>
