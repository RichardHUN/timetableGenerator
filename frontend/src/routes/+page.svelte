<script lang="ts">
</script>

<svelte:head>
	<title>Timetable Generator - Schedule Your School or University Timetable</title>
</svelte:head>

<div class="gradient-hero">
	<section class="container py-5 text-center">
		<div class="row justify-content-center">
			<div class="col-lg-10">
				<h1 class="display-3 fw-bold mb-3">Automated Timetable Generation</h1>
				<p class="lead fs-4 mb-3">
					Create perfect schedules for your school or university in minutes, not hours.
				</p>
				<p class="fs-5 mb-4 opacity-90">
					The Timetable Generator automatically creates conflict-free timetables by considering
					room availability, course requirements, and presenter preferences. Say goodbye to manual
					scheduling headaches.
				</p>
				<div class="d-flex gap-3 justify-content-center flex-wrap mb-5">
					<a href="/register" class="btn btn-light btn-lg px-4">Get Started</a>
					<a href="/login" class="btn btn-light btn-lg px-4">Log In</a>
				</div>
			</div>
		</div>
	</section>

	<section class="features-section py-5">
		<div class="container">
			<h2 class="text-center display-5 fw-bold mb-5 section-title">Why Choose Timetable Generator?</h2>
			<div class="row g-4">
				<div class="col-md-6 col-lg-4">
					<div class="card h-100 border-0 shadow-sm hover-lift">
						<div class="card-body text-center p-4">
							<div class="feature-icon mb-3">🏫</div>
							<h3 class="h5 mb-3">Built for Education</h3>
							<p class="text-muted mb-0">Specifically designed for schools and universities to handle complex scheduling needs.</p>
						</div>
					</div>
				</div>

				<div class="col-md-6 col-lg-4">
					<div class="card h-100 border-0 shadow-sm hover-lift">
						<div class="card-body text-center p-4">
							<div class="feature-icon mb-3">⚡</div>
							<h3 class="h5 mb-3">Fast & Efficient</h3>
							<p class="text-muted mb-0">Generate complete timetables in seconds while avoiding conflicts and constraints.</p>
						</div>
					</div>
				</div>

				<div class="col-md-6 col-lg-4">
					<div class="card h-100 border-0 shadow-sm hover-lift">
						<div class="card-body text-center p-4">
							<div class="feature-icon mb-3">🎯</div>
							<h3 class="h5 mb-3">Smart Optimization</h3>
							<p class="text-muted mb-0">Respects presenter preferences, room availability, and course requirements.</p>
						</div>
					</div>
				</div>

				<div class="col-md-6 col-lg-4">
					<div class="card h-100 border-0 shadow-sm hover-lift">
						<div class="card-body text-center p-4">
							<div class="feature-icon mb-3">🔒</div>
							<h3 class="h5 mb-3">Secure & Private</h3>
							<p class="text-muted mb-0">Your data is encrypted and stored securely with user authentication.</p>
						</div>
					</div>
				</div>

				<div class="col-md-6 col-lg-4">
					<div class="card h-100 border-0 shadow-sm hover-lift">
						<div class="card-body text-center p-4">
							<div class="feature-icon mb-3">📊</div>
							<h3 class="h5 mb-3">Easy Management</h3>
							<p class="text-muted mb-0">Intuitive interface for managing rooms, courses, and presenter preferences.</p>
						</div>
					</div>
				</div>

				<div class="col-md-6 col-lg-4">
					<div class="card h-100 border-0 shadow-sm hover-lift">
						<div class="card-body text-center p-4">
							<div class="feature-icon mb-3">✅</div>
							<h3 class="h5 mb-3">Conflict-Free</h3>
							<p class="text-muted mb-0">No double bookings, overlapping classes, or resource conflicts.</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section>

	<section class="gradient-cta py-5 text-center">
		<div class="container py-4">
			<h2 class="display-5 fw-bold mb-4">Ready to streamline your scheduling?</h2>
			<a href="/register" class="btn btn-light btn-lg px-5">Create Your Account Today</a>
		</div>
	</section>
</div>

<style>
	.gradient-hero {
		background: linear-gradient(135deg, var(--accent), var(--accent-2));
		color: white;
		min-height: 100vh;
	}

	.gradient-cta {
		background: linear-gradient(135deg, var(--accent), var(--accent-2));
		color: white;
	}

	.features-section {
		background-color: var(--bg) !important;
	}

	.section-title {
		color: var(--text) !important;
	}

	.feature-icon {
		font-size: 3rem;
	}

	.hover-lift {
		transition: transform 0.3s ease, box-shadow 0.3s ease;
	}

	.hover-lift:hover {
		transform: translateY(-5px);
		box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15) !important;
	}
</style>
