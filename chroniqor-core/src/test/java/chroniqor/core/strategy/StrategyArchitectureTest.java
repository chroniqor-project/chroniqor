/*
 * Copyright 2026 Chroniqor contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package chroniqor.core.strategy;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "chroniqor.core.strategy")
public class StrategyArchitectureTest {

    @ArchTest
    static final ArchRule strategyMustNotUseApplicationFrameworks = noClasses()
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.springframework..", "jakarta.persistence..", "org.hibernate..");

    @ArchTest
    static final ArchRule strategyMustNotUseExternalIo =
            noClasses().should().dependOnClassesThat().resideInAnyPackage("java.io..", "java.net..", "java.nio.file..");

    @ArchTest
    static final ArchRule strategyMustNotUseSystemTime =
            noClasses().should().dependOnClassesThat().haveFullyQualifiedName("java.time.Clock");

    @ArchTest
    static final ArchRule strategyMustNotUseSystemClass =
            noClasses().should().dependOnClassesThat().haveFullyQualifiedName("java.lang.System");
}
