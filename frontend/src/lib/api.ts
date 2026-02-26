import { PUBLIC_API_URL } from '$env/static/public';

export interface AuthRequest {
	email: string;
	password: string;
	name?: string;
}

export interface AuthResponse {
	token?: string;
	message?: string;
	error?: string;
}

export async function register(email: string, name: string, password: string): Promise<AuthResponse> {
	try {
		const response = await fetch(`${PUBLIC_API_URL}/api/auth/register`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ email, name, password })
		});

		if (!response.ok) {
			const error = await response.json().catch(() => ({ message: 'Registration failed' }));
			return { error: error.message || 'Registration failed' };
		}

		return await response.json();
	} catch (err) {
		return { error: `Network error: ${err instanceof Error ? err.message : 'Unknown error'}` };
	}
}

export async function login(email: string, password: string): Promise<AuthResponse> {
	try {
		const response = await fetch(`${PUBLIC_API_URL}/api/auth/login`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ email, password })
		});

		if (!response.ok) {
			const error = await response.json().catch(() => ({ message: 'Login failed' }));
			return { error: error.message || 'Login failed' };
		}

		return await response.json();
	} catch (err) {
		return { error: `Network error: ${err instanceof Error ? err.message : 'Unknown error'}` };
	}
}
