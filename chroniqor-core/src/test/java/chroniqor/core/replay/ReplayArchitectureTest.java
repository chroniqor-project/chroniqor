/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.replay;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Replay architecture")
class ReplayArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void importClasses() {
        importedClasses = new ClassFileImporter().importPackages("chroniqor.core");
    }

    @Test
    @DisplayName("replay does not depend on Spring or persistence frameworks")
    void shouldNotDependOnFrameworks() {
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("chroniqor.core.replay..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "org.hibernate..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("replay does not depend on infrastructure APIs")
    void shouldNotDependOnInfrastructureApis() {
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("chroniqor.core.replay..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("java.sql..", "java.net..", "java.nio.file..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("replay does not depend on runtime")
    void shouldNotDependOnRuntime() {
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("chroniqor.core.replay..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("chroniqor.runtime..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("replay does not depend on the system clock")
    void shouldNotDependOnRealTimeApis() {
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("chroniqor.core.replay..")
                .should()
                .dependOnClassesThat()
                .haveFullyQualifiedName("java.time.Clock");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("replay does not depend on System")
    void shouldNotDependOnSystem() {
        ArchRule rule = noClasses()
                .that()
                .resideInAPackage("chroniqor.core.replay..")
                .should()
                .dependOnClassesThat()
                .haveFullyQualifiedName("java.lang.System");

        rule.check(importedClasses);
    }
}
