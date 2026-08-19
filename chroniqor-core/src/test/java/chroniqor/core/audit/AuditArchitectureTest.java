/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.audit;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "chroniqor.core.audit")
public class AuditArchitectureTest {

    @ArchTest
    static final ArchRule auditMustRemainFrameworkIndependent = noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage(
                    "org.springframework..", "jakarta.persistence..", "org.hibernate..", "chroniqor.runtime..");

    @ArchTest
    static final ArchRule auditRecorderMustNotDependOnReplay = noClasses()
            .that()
            .haveSimpleName("AuditRecorder")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("chroniqor.core.replay..");
}
