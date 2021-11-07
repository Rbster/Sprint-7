package ru.sber.mvcExample

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.sber.mvcExample.repository.AddressBookRepository
import ru.sber.mvcExample.repository.AddressInfo
import javax.servlet.http.Cookie

@SpringBootTest
@AutoConfigureMockMvc
class MvcExampleApplicationTests {
	@Autowired
	private lateinit var mockMvc: MockMvc

	@Autowired
	private lateinit var repository: AddressBookRepository

	@Test
	fun `test list page`() {
		mockMvc.perform(
			get("/app/list").cookie(Cookie("auth", "2021-10-22T09:50:55.985703Z")))
			.andExpect(status().isOk)
			.andExpect(content().contentType("text/html;charset=UTF-8"))
	}
	@Test
	fun `test list page with search`() {
		mockMvc.perform(
			get("/app/list")
				.cookie(Cookie("auth", "2021-10-22T09:50:55.985703Z"))
				.queryParam("name", "Vasya")
				.queryParam("address", "Moscow"))
			.andDo(print())
			.andExpect(status().isOk)
			.andExpect(content().contentType("text/html;charset=UTF-8"))
	}

	@Test
	fun `test add request`() {
		mockMvc
			.perform(
				post("/app/add")
					.queryParam("name", "Vasya")
					.queryParam("address", "Moscow")
					.cookie(Cookie("auth", "2021-10-22T09:50:55.985703Z")))
//			.andDo(print())
			.andExpect(status().is3xxRedirection)
			.andExpect(header().string("Location", "/app/list"))
	}

	@Test
	fun `test edit request`() {
		mockMvc
			.perform(
				post("/app/id2/edit")
					.queryParam("name", "Vasya")
					.queryParam("address", "Moscow")
					.cookie(Cookie("auth", "2021-10-22T09:50:55.985703Z")))
			.andDo(print())
			.andExpect(status().is3xxRedirection)
			.andExpect(header().string("Location", "/app/list"))
	}

	@Test
	fun `test edit form`() {
		mockMvc
			.perform(
				get("/app/id2/edit/form")
					.cookie(Cookie("auth", "2021-10-22T09:50:55.985703Z")))
			.andDo(print())
			.andExpect(status().isOk)
			.andExpect(content().contentType("text/html;charset=UTF-8"))
	}
	@Test
	fun `test delete request`() {
		mockMvc
			.perform(
				get("/app/id2/delete")
					.cookie(Cookie("auth", "2021-10-22T09:50:55.985703Z")))
			.andDo(print())
			.andExpect(status().is3xxRedirection)
			.andExpect(header().string("Location", "/app/list"))
	}

	@Test
	fun `test view page`() {
		mockMvc
			.perform(
				get("/app/id2/view")
			)
			.andDo(print())
			.andExpect(status().isOk)
//			.andExpect(content().contentType("text/html;charset=UTF-8"))
	}


	@BeforeEach
	fun onStart() {
		repository.add(AddressInfo("Bob", "Toronto"))
		repository.add(AddressInfo("Angela", "New-York"))
		repository.add(AddressInfo("Mathew", "Los-Angeles"))
	}

}
