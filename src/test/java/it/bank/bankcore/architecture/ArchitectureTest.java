package it.bank.bankcore.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.CacheMode;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import it.bank.bankcore.shared.application.UseCase;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(
        packages = "it.bank.bankcore",
        importOptions = ImportOption.DoNotIncludeTests.class,
        cacheMode = CacheMode.PER_CLASS
)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_layer_should_not_depend_on_outer_layers = noClasses()
            .that().resideInAPackage("..domain..").and().resideOutsideOfPackage("..domain.mapper..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..api..", "..application..", "..infrastructure..");

    @ArchTest
    static final ArchRule application_layer_should_not_depend_on_api_layer = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat()
            .resideInAPackage("..api..");

    @ArchTest
    static final ArchRule api_layer_should_not_depend_on_infrastructure_layer = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule use_cases_should_implement_usecase_contract = classes()
            .that().resideInAPackage("..application.usecase..").and().haveSimpleNameEndingWith("UseCase")
            .should().implement(UseCase.class);

    @ArchTest
    static final ArchRule package_slices_should_be_cycle_free = slices()
            .matching("it.bank.bankcore.(*)..")
            .should().beFreeOfCycles();
}


