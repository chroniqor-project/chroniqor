import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.Test

val verboseTestLogging = providers.gradleProperty("verboseTestLogging").isPresent

dependencies {
	compileOnlyApi("org.jspecify:jspecify:1.0.0")

	testImplementation(platform("org.junit:junit-bom:5.13.4"))
	testImplementation("org.junit.jupiter:junit-jupiter")

	testImplementation("com.tngtech.archunit:archunit-junit5:1.4.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	testLogging {
		events(
			TestLogEvent.FAILED,
			TestLogEvent.SKIPPED,
		)

		if (verboseTestLogging) {
			events(TestLogEvent.PASSED)
		}

		exceptionFormat = TestExceptionFormat.FULL
	}
}
