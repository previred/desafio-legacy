package com.desafio.empleados.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Límites entre capas (servicio y persistencia sin acoplamiento al servlet HTTP).
 */
@AnalyzeClasses(
        packages = "com.desafio.empleados",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class PackageDependencyRulesTest {

    @ArchTest
    static final ArchRule serviciosNoDependenDelServlet = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..servlet..");

    @ArchTest
    static final ArchRule repositorioNoDependeDelServlet = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..servlet..");

    @ArchTest
    static final ArchRule repositorioNoDependeDeDto = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..dto..");

    @ArchTest
    static final ArchRule servicioNoDependeDeDto = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..dto..");

    @ArchTest
    static final ArchRule repositorioNoDependeDeApi = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat().resideInAPackage("..api..");

    @ArchTest
    static final ArchRule servicioNoDependeDeApi = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..api..");

    @ArchTest
    static final ArchRule sinCiclosPorPaqueteSuperior = slices()
            .matching("com.desafio.empleados.(*)..")
            .should().beFreeOfCycles();
}
