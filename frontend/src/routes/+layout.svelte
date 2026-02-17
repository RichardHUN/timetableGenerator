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
</style>
