package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption

abstract class BaseArchitecture {
    protected val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages("com.adsearch")
}
