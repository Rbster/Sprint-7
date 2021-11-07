package ru.sber.mvcExample

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.WebApplicationContext
import ru.sber.mvcExample.repository.AddressBookRepository
import ru.sber.mvcExample.repository.AddressInfo
import javax.servlet.http.Cookie

@SpringBootTest
class MvcExampleApplicationTests {
	@Autowired
	private val context: WebApplicationContext? = null
	private var mockMvc: MockMvc? = null

	@Autowired
	private lateinit var repository: AddressBookRepository

	@BeforeEach
	fun setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context!!)
			.apply<DefaultMockMvcBuilder>(springSecurity())
			.build()
		repository.add(AddressInfo("Bob", "Toronto"))
		repository.add(AddressInfo("Angela", "New-York"))
		repository.add(AddressInfo("Mathew", "Los-Angeles"))
	}

	@WithMockUser(
		username = "user",
		authorities = ["IS_AUTHORISED"]
	)
	@Test
	fun `test list page`() {
		mockMvc?.perform(get("/app/list"))
//			?.andDo(print())
			?.andExpect(status().isOk)
			?.andExpect(content().contentType("text/html;charset=UTF-8"))
	}

	@WithMockUser(
		username = "user",
		authorities = ["IS_AUTHORISED"]
	)
	@Test
	fun `test list page with search`() {
		mockMvc?.perform(
			get("/app/list")
				.queryParam("name", "Bob")
				.queryParam("address", "Toronto"))
			?.andDo(print())
			?.andExpect(status().isOk)
			?.andExpect(content().contentType("text/html;charset=UTF-8"))
	}
	@WithMockUser(
		username = "user"
	)
	@Test
	fun `test add request`() {
		mockMvc
			?.perform(
				post("/app/add")
					.queryParam("name", "Vasya")
					.queryParam("address", "Moscow")
					.with(csrf())
			)
			?.andExpect(status().is3xxRedirection)
			?.andExpect(header().string("Location", "/app/list"))
	}

	@WithMockUser(
		username = "user"
	)
	@Test
	fun `test edit request`() {
		mockMvc
			?.perform(
				post("/app/id2/edit")
					.queryParam("name", "Vasya")
					.queryParam("address", "Moscow")
					.with(csrf())
					)
			?.andDo(print())
			?.andExpect(status().is3xxRedirection)
			?.andExpect(header().string("Location", "/app/list"))
	}

	@WithMockUser(
		username = "user"
	)
	@Test
	fun `test edit form`() {
		mockMvc
			?.perform(
				get("/app/id2/edit/form")
					)
			?.andDo(print())
			?.andExpect(status().isOk)
			?.andExpect(content().contentType("text/html;charset=UTF-8"))
	}

	@WithMockUser(
		username = "user"
	)
	@Test
	fun `test delete request by user forbidden`() {
		mockMvc
			?.perform(
				get("/app/id2/delete")
					.with(csrf())
					)
			?.andDo(print())
			?.andExpect(status().isForbidden)
//			?.andExpect(header().string("Location", "/app/list"))
	}

	@WithMockUser(
		username = "admin",
		roles = ["ADMIN"]
	)
	@Test
	fun `test delete request by admin`() {
		mockMvc
			?.perform(
				get("/app/id2/delete")
					.with(csrf())
			)
			?.andDo(print())
			?.andExpect(status().is3xxRedirection)
			?.andExpect(header().string("Location", "/app/list"))
	}

	@WithMockUser(
		username = "user"
	)
	@Test
	fun `test view page`() {
		mockMvc
			?.perform(
				get("/app/id2/view")
			)
			?.andDo(print())
			?.andExpect(status().isOk)
//			.andExpect(content().contentType("text/html;charset=UTF-8"))
	}




}
