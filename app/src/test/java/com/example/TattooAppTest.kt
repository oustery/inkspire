package com.example

import com.example.data.model.BodyZone
import com.example.data.model.TattooStyle
import com.example.data.repository.TattooRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TattooAppTest {

    @Test
    fun testInitialTattoosCatalogNotEmpty() {
        val tattoos = TattooRepository.tattoos.value
        assertTrue("Catalog should contain initial tattoos", tattoos.isNotEmpty())
        assertTrue("Catalog should have at least 10 designs", tattoos.size >= 10)
    }

    @Test
    fun testGetTattooById() {
        val tattoo = TattooRepository.getTattooById("tat-1")
        assertNotNull("tat-1 must exist", tattoo)
        assertEquals("Кибер-сигил Дракон", tattoo?.title)
        assertEquals(TattooStyle.CYBER_SIGILISM, tattoo?.style)
    }

    @Test
    fun testToggleLikeAndSave() {
        val initialItem = TattooRepository.getTattooById("tat-1")!!
        val initialLikes = initialItem.likesCount
        val initialSaved = initialItem.isSaved

        TattooRepository.toggleLike("tat-1")
        val updatedLikesItem = TattooRepository.getTattooById("tat-1")!!
        assertEquals(initialLikes + 1, updatedLikesItem.likesCount)

        TattooRepository.toggleSave("tat-1")
        val updatedSavedItem = TattooRepository.getTattooById("tat-1")!!
        assertEquals(!initialSaved, updatedSavedItem.isSaved)
    }

    @Test
    fun testAddGeneratedTattoo() {
        val beforeCount = TattooRepository.tattoos.value.size
        val newTattoo = TattooRepository.addGeneratedTattoo(
            title = "Тестовый Дракон",
            style = TattooStyle.JAPANESE,
            zone = BodyZone.FOREARM,
            description = "Описание тестового эскиза",
            prompt = "Дракон в туши"
        )

        assertNotNull(newTattoo.id)
        assertEquals(beforeCount + 1, TattooRepository.tattoos.value.size)
        assertTrue(newTattoo.id.startsWith("gen-"))
    }
}
