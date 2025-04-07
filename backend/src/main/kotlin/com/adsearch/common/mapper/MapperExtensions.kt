package com.adsearch.common.mapper

/**
 * Extension functions for mapping between objects using the DynamicMapper.
 */

/**
 * Maps this object to an instance of the target class [T].
 * Properties with matching names will be copied from this object to the target.
 *
 * @return An instance of the target class with properties copied from this object
 */
inline fun <reified T : Any> Any.mapTo(): T {
    return DynamicMapper.map(this)
}

/**
 * Maps this list of objects to a list of instances of the target class [T].
 * Properties with matching names will be copied from each source object to the corresponding target.
 *
 * @return A list of instances of the target class with properties copied from the source objects
 */
inline fun <reified T : Any> List<Any>.mapToList(): List<T> {
    return this.map { it.mapTo<T>() }
}

/**
 * Maps this object to an instance of the target class [R] with custom property mappings.
 * This allows for mapping properties with different names or applying transformations.
 *
 * @param propertyMap A map of target property names to lambda functions that extract values from this object
 * @return An instance of the target class with properties mapped according to the propertyMap
 */
inline fun <T : Any, reified R : Any> T.mapToWithCustomMappings(
    propertyMap: Map<String, (T) -> Any?>
): R {
    return DynamicMapper.mapWithCustomMappings(this, propertyMap)
}
