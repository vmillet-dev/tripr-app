package com.adsearch.common.mapper

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * A utility class for dynamically mapping between data classes using Kotlin reflection.
 * This mapper can convert objects of different types as long as they have properties with the same names.
 */
object DynamicMapper {
    
    /**
     * Maps an object of type [T] to an object of type [R].
     * Properties with matching names will be copied from the source to the target.
     *
     * @param source The source object to map from
     * @param targetClass The target class to map to
     * @return An instance of the target class with properties copied from the source
     */
    fun <T : Any, R : Any> map(source: T, targetClass: KClass<R>): R {
        try {
            // Get the primary constructor of the target class
            val constructor = targetClass.primaryConstructor
                ?: throw IllegalArgumentException("Target class ${targetClass.simpleName} must have a primary constructor")
            
            // Prepare the arguments for the constructor
            val arguments = mutableMapOf<String, Any?>()
            
            // Get all properties of the source object
            val sourceProps = source::class.memberProperties
            
            // For each parameter in the constructor, find a matching property in the source object
            constructor.parameters.forEach { parameter ->
                val paramName = parameter.name ?: return@forEach
                
                // Find a matching property in the source object
                val sourceProp = sourceProps.find { it.name == paramName }
                
                if (sourceProp != null) {
                    // Get the value from the source object
                    @Suppress("UNCHECKED_CAST")
                    val value = (sourceProp as KProperty1<T, Any?>).get(source)
                    arguments[paramName] = value
                }
            }
            
            // Create a new instance of the target class with the mapped properties
            return constructor.callBy(constructor.parameters.associateWith { param ->
                arguments[param.name]
            })
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to map ${source::class.simpleName} to ${targetClass.simpleName}", e)
        }
    }
    
    /**
     * Maps an object of type [T] to an object of type [R].
     * Properties with matching names will be copied from the source to the target.
     *
     * @param source The source object to map from
     * @return An instance of the target class with properties copied from the source
     */
    inline fun <T : Any, reified R : Any> map(source: T): R {
        return map(source, R::class)
    }
    
    /**
     * Maps a list of objects of type [T] to a list of objects of type [R].
     *
     * @param sourceList The list of source objects to map from
     * @return A list of instances of the target class with properties copied from the source objects
     */
    inline fun <T : Any, reified R : Any> mapList(sourceList: List<T>): List<R> {
        return sourceList.map { map(it, R::class) }
    }
    
    /**
     * Maps an object of type [T] to an object of type [R] with custom property mappings.
     * This allows for mapping properties with different names or applying transformations.
     *
     * @param source The source object to map from
     * @param propertyMap A map of target property names to lambda functions that extract values from the source
     * @return An instance of the target class with properties mapped according to the propertyMap
     */
    fun <T : Any, R : Any> mapWithCustomMappings(
        source: T,
        targetClass: KClass<R>,
        propertyMap: Map<String, (T) -> Any?>
    ): R {
        try {
            val constructor = targetClass.primaryConstructor
                ?: throw IllegalArgumentException("Target class ${targetClass.simpleName} must have a primary constructor")
            
            // Prepare the arguments for the constructor
            val arguments = mutableMapOf<String, Any?>()
            
            // Get all properties of the source object
            val sourceProps = source::class.memberProperties
            
            // For each parameter in the constructor, find a matching property in the source object or use custom mapping
            constructor.parameters.forEach { parameter ->
                val paramName = parameter.name ?: return@forEach
                
                // Check if there's a custom mapping for this parameter
                val customMapping = propertyMap[paramName]
                if (customMapping != null) {
                    arguments[paramName] = customMapping(source)
                } else {
                    // Find a matching property in the source object
                    val sourceProp = sourceProps.find { it.name == paramName }
                    if (sourceProp != null) {
                        // Get the value from the source object
                        @Suppress("UNCHECKED_CAST")
                        val value = (sourceProp as KProperty1<T, Any?>).get(source)
                        arguments[paramName] = value
                    }
                }
            }
            
            // Create a new instance of the target class with the mapped properties
            return constructor.callBy(constructor.parameters.associateWith { param ->
                arguments[param.name]
            })
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to map ${source::class.simpleName} to ${targetClass.simpleName} with custom mappings", e)
        }
    }
    
    /**
     * Maps an object of type [T] to an object of type [R] with custom property mappings.
     * This allows for mapping properties with different names or applying transformations.
     *
     * @param source The source object to map from
     * @param propertyMap A map of target property names to lambda functions that extract values from the source
     * @return An instance of the target class with properties mapped according to the propertyMap
     */
    inline fun <T : Any, reified R : Any> mapWithCustomMappings(
        source: T,
        propertyMap: Map<String, (T) -> Any?>
    ): R {
        return mapWithCustomMappings(source, R::class, propertyMap)
    }
}
