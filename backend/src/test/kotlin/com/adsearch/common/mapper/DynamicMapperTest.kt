package com.adsearch.common.mapper

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DynamicMapperTest {
    
    data class SourceClass(
        val id: Long = 1,
        val name: String = "Test",
        val value: Double = 10.5,
        val tags: List<String> = listOf("tag1", "tag2")
    )
    
    data class TargetClass(
        val id: Long,
        val name: String,
        val value: Double,
        val tags: List<String>,
        val extraField: String? = null
    )
    
    data class PartialTargetClass(
        val id: Long,
        val name: String
    )
    
    @Test
    fun `test mapping between classes with same properties`() {
        val source = SourceClass()
        val target = DynamicMapper.map<SourceClass, TargetClass>(source)
        
        assertEquals(source.id, target.id)
        assertEquals(source.name, target.name)
        assertEquals(source.value, target.value)
        assertEquals(source.tags, target.tags)
        assertEquals(null, target.extraField)
    }
    
    @Test
    fun `test mapping between classes with subset of properties`() {
        val source = SourceClass()
        val target = DynamicMapper.map<SourceClass, PartialTargetClass>(source)
        
        assertEquals(source.id, target.id)
        assertEquals(source.name, target.name)
    }
    
    @Test
    fun `test mapping list of objects`() {
        val sourceList = listOf(
            SourceClass(id = 1, name = "Test1"),
            SourceClass(id = 2, name = "Test2")
        )
        
        val targetList = DynamicMapper.mapList<SourceClass, TargetClass>(sourceList)
        
        assertEquals(2, targetList.size)
        assertEquals(1L, targetList[0].id)
        assertEquals("Test1", targetList[0].name)
        assertEquals(2L, targetList[1].id)
        assertEquals("Test2", targetList[1].name)
    }
    
    @Test
    fun `test mapping with extension function`() {
        val source = SourceClass()
        val target = source.mapTo<TargetClass>()
        
        assertEquals(source.id, target.id)
        assertEquals(source.name, target.name)
        assertEquals(source.value, target.value)
        assertEquals(source.tags, target.tags)
    }
    
    @Test
    fun `test mapping with custom property mappings`() {
        val source = SourceClass()
        val target = DynamicMapper.mapWithCustomMappings<SourceClass, TargetClass>(
            source,
            mapOf(
                "name" to { it.name.uppercase() },
                "extraField" to { "Custom value" }
            )
        )
        
        assertEquals(source.id, target.id)
        assertEquals(source.name.uppercase(), target.name)
        assertEquals(source.value, target.value)
        assertEquals(source.tags, target.tags)
        assertEquals("Custom value", target.extraField)
    }
}
