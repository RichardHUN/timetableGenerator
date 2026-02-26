<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import Nav from '$lib/Nav.svelte';

	let { children } = $props();
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<Nav />

<main>
	{@render children()}
</main>

<style>
	main {
		min-height: 100vh;
	}

	:global(html[data-theme="dark"]) main {
		background: linear-gradient(180deg, rgba(10,12,15,1), rgba(8,10,12,0.95));
		color: #e6eef6;
	}

	:global(html[data-theme="light"]) main {
		background: #f8fafc;
		color: initial;
	}
</style>
