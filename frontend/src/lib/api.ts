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

// ─── Timetable interfaces ────────────────────────────────────────────────────

export interface TimetableListItem {
	id: string;
	name: string;
	createdAt: string;
}

export interface TimetableDetail {
	id: string;
	name: string;
	createdAt: string;
	timetableJson: string;
}

export interface TimetableResponse {
	data?: TimetableListItem[] | TimetableDetail;
	error?: string;
}

// ─── Timetable API helpers ───────────────────────────────────────────────────

export async function getTimetables(token: string): Promise<{ data?: TimetableListItem[]; error?: string; unauthorized?: boolean }> {
	try {
		const response = await fetch(`${PUBLIC_API_URL}/api/timetables`, {
			headers: { Authorization: `Bearer ${token}` }
		});
		if (!response.ok) {
			if (response.status === 401 || response.status === 403) {
				return { error: 'Your session has expired or is invalid. Please log in again.', unauthorized: true };
			}
			const err = await response.json().catch(() => ({ message: 'Failed to fetch timetables' }));
			return { error: err.message || `Server error ${response.status}` };
		}
		return { data: await response.json() };
	} catch (err) {
		return { error: `Network error: ${err instanceof Error ? err.message : 'Unknown error'}` };
	}
}

export async function getTimetable(id: string, token: string): Promise<{ data?: TimetableDetail; error?: string }> {
	try {
		const response = await fetch(`${PUBLIC_API_URL}/api/timetables/${id}`, {
			headers: { Authorization: `Bearer ${token}` }
		});
		if (!response.ok) {
			const err = await response.json().catch(() => ({ message: 'Failed to fetch timetable' }));
			return { error: err.message || `Server error ${response.status}` };
		}
		return { data: await response.json() };
	} catch (err) {
		return { error: `Network error: ${err instanceof Error ? err.message : 'Unknown error'}` };
	}
}

export async function deleteTimetable(id: string, token: string): Promise<{ error?: string }> {
	try {
		const response = await fetch(`${PUBLIC_API_URL}/api/timetables/${id}`, {
			method: 'DELETE',
			headers: { Authorization: `Bearer ${token}` }
		});
		if (!response.ok) {
			const err = await response.json().catch(() => ({ message: 'Failed to delete timetable' }));
			return { error: err.message || `Server error ${response.status}` };
		}
		return {};
	} catch (err) {
		return { error: `Network error: ${err instanceof Error ? err.message : 'Unknown error'}` };
	}
}

export async function renameTimetable(id: string, newName: string, token: string): Promise<{ error?: string }> {
	try {
		const url = `${PUBLIC_API_URL}/api/timetables/${id}?name=${encodeURIComponent(newName)}`;
		const response = await fetch(url, {
			method: 'PATCH',
			headers: { Authorization: `Bearer ${token}` }
		});
		if (!response.ok) {
			const err = await response.json().catch(() => ({ message: 'Failed to rename timetable' }));
			return { error: err.message || `Server error ${response.status}` };
		}
		return {};
	} catch (err) {
		return { error: `Network error: ${err instanceof Error ? err.message : 'Unknown error'}` };
	}
}
