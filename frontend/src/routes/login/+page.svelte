<script lang="ts">
	import { login } from '$lib/api';

	let email = '';
	let password = '';
	let loading = false;
	let error = '';
	let success = false;

	async function handleLogin() {
		error = '';
		success = false;

		if (!email || !password) {
			error = 'Please fill in all fields';
			return;
		}

		loading = true;
		const result = await login(email, password);

		if (result.error) {
			error = result.error;
			loading = false;
			return;
		}

		if (result.token) {
			localStorage.setItem('token', result.token);
			success = true;
			// Redirect to frontend root route (no /api prefix)
			window.location.href = '/';
		} else {
			error = 'Login successful but no token received';
			loading = false;
		}
	}

	function handleKeydown(e: KeyboardEvent) {
		if (e.key === 'Enter') {
			handleLogin();
		}
	}
</script>

<svelte:head>
	<title>Login - Timetable Generator</title>
</svelte:head>

<div class="d-flex justify-content-center align-items-center min-vh-100 gradient-bg">
	<div class="card shadow-lg" style="width: 100%; max-width: 400px;">
		<div class="card-body p-4">
			<h1 class="h3 text-center mb-4">Login</h1>

			{#if error}
				<div class="alert alert-danger">
					{error}
				</div>
			{/if}

			{#if success}
				<div class="alert alert-success">
					Login successful! Redirecting...
				</div>
			{/if}

			<form on:submit|preventDefault={handleLogin}>
				<div class="mb-3">
					<label for="email" class="form-label">Email</label>
					<input
						type="email"
						id="email"
						class="form-control"
						bind:value={email}
						placeholder="Enter your email"
						disabled={loading}
						on:keydown={handleKeydown}
					/>
				</div>

				<div class="mb-3">
					<label for="password" class="form-label">Password</label>
					<input
						type="password"
						id="password"
						class="form-control"
						bind:value={password}
						placeholder="Enter your password"
						disabled={loading}
						on:keydown={handleKeydown}
					/>
				</div>

				<button type="submit" disabled={loading} class="btn btn-primary w-100">
					{loading ? 'Logging in...' : 'Login'}
				</button>
			</form>

			<div class="text-center mt-3 text-muted small">
				Don't have an account? <a href="/register" class="text-decoration-none">Register here</a>
			</div>
		</div>
	</div>
</div>

<style>
	.gradient-bg {
		background: linear-gradient(135deg, var(--accent), var(--accent-2));
	}
</style>
