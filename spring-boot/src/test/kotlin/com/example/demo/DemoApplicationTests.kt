package com.example.demo

import com.example.demo.controller.Controller
import com.example.demo.persistance.Entity
import com.example.demo.persistance.EntityRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(Controller::class)
class ControllerTest {
	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `GET do thing successfully`() {
		val result = mockMvc.perform(MockMvcRequestBuilders.get("/doThing"))

		result
			.andExpect(MockMvcResultMatchers.status().isOk)
			.andExpect(MockMvcResultMatchers.content().string("nice"))
	}
}
@DataJpaTest
class EntityRepositoryTest {
	@Autowired
	lateinit var entityRepository: EntityRepository

	@Test
	fun `findById should find book`() {
		val newEntity = entityRepository.save(Entity(name = "Pride and prejudice", author = "Jane Austin"))

		val foundEntity = entityRepository.findById(newEntity.id!!)

		assertTrue { foundEntity.get() == newEntity }
	}
}