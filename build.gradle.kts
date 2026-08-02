import com.diffplug.spotless.LineEnding
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "8.9.0"
}

group = "io.github.cvcg11.chroniqor"
version = "0.1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")

    group = rootProject.group
    version = rootProject.version

    the<JavaPluginExtension>().toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

tasks.named("check") {
    dependsOn("spotlessCheck")
    dependsOn(subprojects.map { it.tasks.named("check") })
}

spotless {
    lineEndings = LineEnding.UNIX

    java {
        target("**/src/**/*.java")

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
        target("**/*.gradle.kts")
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

        trimTrailingWhitespace()
        endWithNewline()
    }
}
