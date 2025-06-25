package com.adeem.stockflow;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.type;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.adeem.stockflow.audit.EntityAuditEventListener;
import com.adeem.stockflow.domain.AbstractAuditingEntity;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = StockflowApiApp.class, importOptions = DoNotIncludeTests.class)
class TechnicalStructureTest {

    // prettier-ignore
    @ArchTest
    static final ArchRule respectsTechnicalArchitectureLayers = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Config").definedBy("..config..")
        .layer("Web").definedBy("..web..")
        .optionalLayer("Service").definedBy("..service..")
        .layer("Batch").definedBy("..batch.listener..", "..batch.reader..", "..batch.writer..", "..batch.processor..", "..batch.config..")
        .layer("Security").definedBy("..security..")
        .optionalLayer("Persistence").definedBy("..repository..")
        .layer("Domain").definedBy("..domain..")

        .whereLayer("Config").mayNotBeAccessedByAnyLayer()
        .whereLayer("Web").mayOnlyBeAccessedByLayers("Config")
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Web", "Config", "Batch")  // Allow batch to access service
        .whereLayer("Batch").mayOnlyBeAccessedByLayers("Config", "Web", "Service")  // Allow service and web to access batch
        .whereLayer("Batch").mayOnlyBeAccessedByLayers("Config", "Web")  // Allow web to access batch
        .whereLayer("Security").mayOnlyBeAccessedByLayers("Config", "Service", "Web", "Batch")  // Allow batch to access security
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service", "Security", "Web", "Config", "Batch")  // Allow batch to access persistence
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Persistence", "Service", "Security", "Web", "Config", "Batch")  // Allow batch to access domain

        .ignoreDependency(resideInAPackage("com.adeem.stockflow.audit"), alwaysTrue())
        .ignoreDependency(type(AbstractAuditingEntity.class), type(EntityAuditEventListener.class))
        .ignoreDependency(belongToAnyOf(StockflowApiApp.class), alwaysTrue())
        .ignoreDependency(alwaysTrue(), belongToAnyOf(
            com.adeem.stockflow.config.Constants.class,
            com.adeem.stockflow.config.ApplicationProperties.class
        ));
}
