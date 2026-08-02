import com.diffplug.spotless.LineEnding

plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "8.9.0"
}

group = "io.github.cvcg11.chroniqor"
version = "0.1.0-SNAPSHOT"
description = "Motor local, modular y determinista de backtesting para Forex"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springModulithVersion"] = "2.1.0"

dependencies {
	// API REST local
	implementation("org.springframework.boot:spring-boot-starter-webmvc")

	// Persistencia relacional
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")

	// Migraciones de base de datos
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")

	// Validacion de entradas y configuraciones
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Arquitectura de monolito modular
	implementation("org.springframework.modulith:spring-modulith-starter-core")

	// Metadatos para propiedades de configuracion
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// Reduccion de codigo repetitivo
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Pruebas unitarias y de integracion
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.modulith:spring-modulith-starter-test")

	// PostgreSQL real para pruebas de integracion
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:testcontainers-junit-jupiter")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testRuntimeOnly("com.h2database:h2")

	// Ejecucion de pruebas con JUnit Platform
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom(
			"org.springframework.modulith:spring-modulith-bom:" +
					property("springModulithVersion")
		)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()

	testLogging {
		events("passed", "skipped", "failed")
	}
}

spotless {
	lineEndings = LineEnding.UNIX

	java {
		target("src/**/*.java")
		targetExclude(
			"**/build/**",
			"**/generated/**",
			"**/vendor/**"
		)
		importOrder()
		removeUnusedImports()
		palantirJavaFormat()
		licenseHeaderFile(rootProject.file("config/license-header.txt"))
	}

	kotlinGradle {
		target("*.gradle.kts")
		trimTrailingWhitespace()
		endWithNewline()
	}

	format("misc") {
		target(
			"**/*.md",
			"**/*.yml",
			"**/*.yaml",
			"**/*.properties",
			".gitignore",
			".gitattributes"
		)

		targetExclude(
			"**/build/**",
			"**/.gradle/**"
		)

		trimTrailingWhitespace()
		endWithNewline()
	}
}

springBoot {
	buildInfo()
}
