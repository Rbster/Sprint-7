package ru.sber.mvcExample

import org.h2.value.Value.JSON
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

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
class MockMvcRestTest {
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
    fun `test list page by user fails`() {
        mockMvc?.perform(get("/api/app/list"))
//			?.andDo(print())
            ?.andExpect(status().isForbidden)
    }

    @WithMockUser(
        username = "user",
        authorities = ["IS_AUTHORISED"]
    )
    @Test
    fun `test list page with search by user fails`() {
        mockMvc?.perform(
            get("/api/app/list")
                .queryParam("name", "Bob")
                .queryParam("address", "Toronto"))
            ?.andDo(print())
            ?.andExpect(status().isForbidden)
    }
    @WithMockUser(
        username = "user"
    )
    @Test
    fun `test add request by user fails`() {
        mockMvc
            ?.perform(
                post("/api/app/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Vasya\",\"address\":\"Moscow\"}")
                    .with(csrf())
            )
            ?.andExpect(status().isForbidden)
    }

    @WithMockUser(
        username = "user"
    )
    @Test
    fun `test edit request by user fails`() {
        mockMvc
            ?.perform(
                put("/api/app/id2/edit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Vasya\",\"address\":\"Moscow\"}")
                    .with(csrf())
            )
            ?.andDo(print())
            ?.andExpect(status().isForbidden)
    }


    @WithMockUser(
        username = "user"
    )
    @Test
    fun `test delete request by user fails`() {
        mockMvc
            ?.perform(
                delete("/api/app/id2/delete")
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
                delete("/api/app/id2/delete")
                    .with(csrf())
            )
            ?.andDo(print())
            ?.andExpect(status().isNoContent)
    }

    @WithMockUser(
        username = "user"
    )
    @Test
    fun `test view page by user fails`() {
        mockMvc
            ?.perform(
                get("/api/app/id2/view")
            )
            ?.andDo(print())
            ?.andExpect(status().isForbidden)
    }




    ////

    @WithMockUser(
        username = "apiUser",
        roles = ["API"]
    )
    @Test
    fun `test list page by apiUser`() {
        mockMvc?.perform(get("/api/app/list"))
//			?.andDo(print())
            ?.andExpect(status().isOk)
    }

    @WithMockUser(
        username = "apiUser",
        roles = ["API"]
    )
    @Test
    fun `test list page with search by apiUser`() {
        mockMvc?.perform(
            get("/api/app/list")
                .queryParam("name", "Bob")
                .queryParam("address", "Toronto"))
            ?.andDo(print())
            ?.andExpect(status().isOk)
    }
    @WithMockUser(
        username = "apiUser",
        roles = ["API"]
    )
    @Test
    fun `test add request by apiUser`() {
        mockMvc
            ?.perform(
                post("/api/app/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Vasya\",\"address\":\"Moscow\"}")
                    .with(csrf())
            )
            ?.andExpect(status().isOk)
    }

    @WithMockUser(
        username = "apiUser",
        roles = ["API"]
    )
    @Test
    fun `test edit request by apiUser`() {
        mockMvc
            ?.perform(
                put("/api/app/id2/edit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"Vasya\",\"address\":\"Moscow\"}")
                    .with(csrf())
            )
            ?.andDo(print())
            ?.andExpect(status().isOk)
    }


    @WithMockUser(
        username = "apiUser",
        roles = ["API"]
    )
    @Test
    fun `test delete request by apiUser fails`() {
        mockMvc
            ?.perform(
                delete("/api/app/id2/delete")
                    .with(csrf())
            )
            ?.andDo(print())
            ?.andExpect(status().isForbidden)
//			?.andExpect(header().string("Location", "/app/list"))
    }


    @WithMockUser(
        username = "apiUser",
        roles = ["API"]
    )
    @Test
    fun `test view page by apiUser`() {
        mockMvc
            ?.perform(
                get("/api/app/id2/view")
            )
            ?.andDo(print())
            ?.andExpect(status().isOk)
    }




}
