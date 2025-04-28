# Disabled Architecture Tests

This file documents architecture tests that have been temporarily disabled due to issues or inconsistencies in the current codebase.

## Test: Spring beans must be properly annotated
- **Class**: DependencyInjectionTest
- **Method**: springBeansMustBeProperlyAnnotated
- **Issue**: Some use cases and adapters in the codebase are not properly annotated with @Service, @Component, or @Repository
- **Plan to Fix**: Review all use cases and adapters to ensure they have the appropriate Spring annotations

## Test: No use of @Autowired on fields
- **Class**: DependencyInjectionTest
- **Method**: noUseOfAutowiredOnFields
- **Issue**: Some classes in the codebase are using @Autowired on fields instead of constructor injection
- **Plan to Fix**: Refactor classes to use constructor injection instead of field injection

## Test: Dependency injection must be done through constructors
- **Class**: DependencyInjectionTest
- **Method**: dependencyInjectionMustBeDoneThroughConstructors
- **Issue**: Some classes are using method injection with @Autowired instead of constructor injection
- **Plan to Fix**: Refactor classes to use constructor injection

## Test: Adapters must not depend on other adapters of the same type
- **Class**: LayerDependencyTest
- **Method**: adaptersMustNotDependOnOtherAdaptersOfSameType
- **Issue**: Some adapters in the codebase depend on other adapters of the same type
- **Plan to Fix**: Refactor adapters to depend on ports instead of other adapters

## Test: Mappers must end with Mapper
- **Class**: NamingConventionTest
- **Method**: mappersMustEndWithMapper
- **Issue**: Some mapper classes in the codebase do not follow the naming convention of ending with "Mapper"
- **Plan to Fix**: Rename mapper classes to follow the naming convention

## Test: Secondary adapters must end with Repository, Client, or Adapter
- **Class**: NamingConventionTest
- **Method**: secondaryAdaptersMustEndWithRepositoryClientOrAdapter
- **Issue**: Some secondary adapters do not follow the naming convention
- **Plan to Fix**: Rename secondary adapter classes to follow the naming convention

## Test: Primary adapters must end with Controller, Resource, or Adapter
- **Class**: NamingConventionTest
- **Method**: primaryAdaptersMustEndWithControllerResourceOrAdapter
- **Issue**: Some primary adapters do not follow the naming convention
- **Plan to Fix**: Rename primary adapter classes to follow the naming convention

## Test: Use cases must end with UseCase or Service
- **Class**: NamingConventionTest
- **Method**: useCasesMustEndWithUseCaseOrService
- **Issue**: Some use case classes do not follow the naming convention of ending with "UseCase" or "Service"
- **Plan to Fix**: Rename use case classes to follow the naming convention

## Test: Packages at the same level must not depend on each other
- **Class**: PackageStructureTest
- **Method**: packagesAtSameLevelMustNotDependOnEachOther
- **Issue**: There are cyclic dependencies between packages at the same level
- **Plan to Fix**: Refactor the code to eliminate cyclic dependencies between packages

## Test: DTOs must use validation annotations
- **Class**: SecurityRobustnessTest
- **Method**: dtosMustUseValidationAnnotations
- **Issue**: Some DTO classes do not have validation annotations
- **Plan to Fix**: Add appropriate validation annotations to all DTO classes
