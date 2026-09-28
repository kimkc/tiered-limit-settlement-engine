package com.tieredlimit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureTest {
    private static JavaClasses classes;          // ① 임포트 결과

    @BeforeAll
    static void importClasses() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.tieredlimit");
    }

    @Test
    void 도메인은_바깥_레이어를_의존하지_않는다() {
        ArchRule rule = noClasses()                                   // ② 규칙
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..adapter..", "..config..");

        rule.check(classes);                                          // ③ 적용
    }

    @Test
    void 어플리케이션은_어댑터를_의존하지_않는다(){
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..adapter..");

        rule.check(classes);
    }

    @Test
    void 도메인은_스프링프레임워크를_의존하지_않는다(){
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..");

        rule.check(classes);
    }

    @Test
    void 도메인은_롬복을_의존하지_않는다(){
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("lombok..");

        rule.check(classes);
    }
}
