plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

extra["springModulithVersion"] = "2.1.0"

dependencies {
    implementation(project(":chroniqor-core"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.apache.commons:commons-csv:1.14.1")

    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.modulith:spring-modulith-starter-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
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
    options.compilerArgs.add("-Xlint:deprecation")
}
