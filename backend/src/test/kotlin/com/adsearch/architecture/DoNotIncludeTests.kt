package com.adsearch.architecture

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location

class DoNotIncludeTests : ImportOption {
    override fun includes(location: Location): Boolean {
        return !location.contains("/test/")
    }
}
