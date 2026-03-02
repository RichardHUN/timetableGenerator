<script lang="ts">
	import { register } from '$lib/api';

	let email = '';
	let name = '';
	let password = '';
	let confirmPassword = '';
	let loading = false;
	let error = '';
	let success = false;

	async function handleRegister() {
		error = '';
		success = false;

		if (!email || !name || !password || !confirmPassword) {
			error = 'Please fill in all fields';
			return;
		}

		if (password !== confirmPassword) {
			error = 'Passwords do not match';
			return;
		}

		if (password.length < 6) {
			error = 'Password must be at least 6 characters';
			return;
		}

		loading = true;
		const result = await register(email, name, password);

		if (result.error) {
			error = result.error;
			loading = false;
			return;
		}

		success = true;
		// Clear form and optionally redirect
		email = '';
		name = '';
		password = '';
		confirmPassword = '';

		setTimeout(() => {
			window.location.href = '/login';
		}, 2000);
	}

	function handleKeydown(e: KeyboardEvent) {
		if (e.key === 'Enter') {
			handleRegister();
		}
	}
</script>

<svelte:head>
	<title>Register - Timetable Generator</title>
</svelte:head>

<div class="d-flex justify-content-center align-items-center min-vh-100 gradient-bg">
	<div class="card shadow-lg" style="width: 100%; max-width: 400px;">
		<div class="card-body p-4">
			<h1 class="h3 text-center mb-4">Register</h1>

			{#if error}
				<div class="alert alert-danger">
					{error}
				</div>
			{/if}

			{#if success}
				<div class="alert alert-success">
					Registration successful! Redirecting to login...
				</div>
			{/if}

			<form on:submit|preventDefault={handleRegister}>
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
					<label for="name" class="form-label">Name</label>
					<input
						type="text"
						id="name"
						class="form-control"
						bind:value={name}
						placeholder="Enter your full name"
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

				<div class="mb-3">
					<label for="confirmPassword" class="form-label">Confirm Password</label>
					<input
						type="password"
						id="confirmPassword"
						class="form-control"
						bind:value={confirmPassword}
						placeholder="Confirm your password"
						disabled={loading}
						on:keydown={handleKeydown}
					/>
				</div>

				<button type="submit" disabled={loading} class="btn btn-primary w-100">
					{loading ? 'Registering...' : 'Register'}
				</button>
			</form>

			<div class="text-center mt-3 text-muted small">
				Already have an account? <a href="/login" class="text-decoration-none">Login here</a>
			</div>
		</div>
	</div>
</div>

<style>
	.gradient-bg {
		background: linear-gradient(135deg, var(--accent), var(--accent-2));
	}
</style>
