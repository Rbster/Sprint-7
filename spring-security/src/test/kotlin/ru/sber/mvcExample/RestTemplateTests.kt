package ru.sber.mvcExample



import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import ru.sber.mvcExample.repository.AddressBookRepository
import ru.sber.mvcExample.repository.AddressInfo
import ru.sber.mvcExample.repository.AddressSearchForm
import ru.sber.mvcExample.repository.LoginFormModel



//
//@Configuration
//class TestRestTemplateConfiguration {
//    @Bean
//    fun restTemplate(): RestTemplate {
//        val restTemplate = RestTemplate()
//        val factory = HttpComponentsClientHttpRequestFactory()
//        val build: CloseableHttpClient = HttpClientBuilder.create().disableRedirectHandling().build()
//        factory.setHttpClient(build)
//        restTemplate.setRequestFactory(factory)
//        return restTemplate
//    }
//}


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestTemplateTests {
    @LocalServerPort
    private var port: Int = 0

    private val restTemplateBuilder = RestTemplateBuilder().requestFactory(HttpComponentsClientHttpRequestFactory::class.java)
    private var restTemplate: TestRestTemplate = TestRestTemplate(restTemplateBuilder)


    @Autowired
    private lateinit var repository: AddressBookRepository

    private fun url(s: String): String = "http://localhost:${port}/${s}"

    private val initialList = listOf(
        AddressInfo("Bob", "Toronto"),
        AddressInfo("Angela", "New-York"),
        AddressInfo("Mathew", "Los-Angeles")
    )

    @BeforeEach
    fun onStart() {


        for (elem in initialList) {
            repository.add(elem)
        }
    }

    @AfterEach
    fun onFinish() {
        repository.clearRepository()
    }

//    @Test
//    fun `post auth with wrong credentials`() {
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val credentials = LoginFormModel("login","admin")
//
//        val resp = restTemplate.exchange(url("alt/login/auth"),
//            HttpMethod.POST,
//            HttpEntity(credentials, headers),
//            Any::class.java)
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
//    }
//
//    @Test
//    fun `post auth with right credentials`() {
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val credentials = LoginFormModel("admin","admin")
//
//        val resp = restTemplate.exchange(url("alt/login/auth"),
//            HttpMethod.POST,
//            HttpEntity(credentials, headers),
//            Any::class.java)
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.FOUND)
//        assertThat(resp?.headers?.get("Location")?.get(0)).isEqualTo(url("api/app/list"))
//        assertThat(resp?.headers?.get("Set-Cookie")?.find { it.contains("auth=")}).isNotNull
//    }
//
//    @Test
//    fun `post add request`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//        val newElem = AddressInfo("Ivan", "Moscow")
//
//
//        val resp = restTemplate.exchange(url("api/app/add"),
//            HttpMethod.POST,
//            HttpEntity(newElem, headers),
//            object : ParameterizedTypeReference<Pair<String, AddressInfo>>() {})
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(resp.body?.second).isEqualTo(newElem)
//    }
//
//    @Test
//    fun `get list all`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//
//        val resp = restTemplate.exchange(url("api/app/list"),
//            HttpMethod.GET,
//            HttpEntity(null, headers),
//            object : ParameterizedTypeReference<Map<String, AddressInfo>>() {})
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
//        for (i in initialList.indices) {
//            assertThat(resp.body?.get("id${i + 1}")).isEqualTo(initialList[i])
//        }
//    }


// ok !
    @Test
    fun `get list result in redirect to login form`() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val resp = restTemplate.exchange(url("api/app/list"),
            HttpMethod.GET,
            HttpEntity(null, headers),
            Any::class.java)
        assertThat(resp.statusCode).isEqualTo(HttpStatus.FOUND)
        assertThat(resp.headers.location?.path).isEqualTo("/login")
    }


//
//    @Test
//    fun `get list find one`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
//        val searchForm = AddressSearchForm(null, initialList[0].address)
//        val willNotBeListed: AddressInfo? = initialList.find { it.address != initialList[0].address}
//
//        val resp = restTemplate.exchange(url("api/app/list?address=${searchForm.address}"),
//            HttpMethod.GET,
//            HttpEntity(null, headers),
//            object : ParameterizedTypeReference<Map<String, AddressInfo>>() {})
//
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(resp.body?.containsValue(initialList[0])).isTrue
//        assertThat(resp.body?.containsValue(willNotBeListed)).isFalse
//    }
//
//
//    @Test
//    fun `get view request`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//
//        val responses = mutableListOf<ResponseEntity<Pair<String, AddressInfo>>>()
//        for (i in 1..initialList.size) {
//            val resp = restTemplate.exchange(url("api/app/id$i/view"),
//                HttpMethod.GET,
//                HttpEntity(null, headers),
//                object : ParameterizedTypeReference<Pair<String, AddressInfo>>() {})
//            responses.add(resp)
//        }
//
//        for (i in 0 until responses.size) {
//            assertThat(responses[i].statusCode).isEqualTo(HttpStatus.OK)
//            assertThat(responses[i].body?.first).isEqualTo("id${i + 1}")
//            assertThat(responses[i].body?.second?.address).isEqualTo(initialList[i].address)
//            assertThat(responses[i].body?.second?.name).isEqualTo(initialList[i].name)
//        }
//    }
//
//    @Test
//    fun `get view request not found`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//
//        val resp = restTemplate.exchange(url("api/app/id0/view"),
//            HttpMethod.GET,
//            HttpEntity(null, headers),
//            object : ParameterizedTypeReference<Pair<String, AddressInfo>>() {})
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
//    }
//
//    @Test
//    fun `put edit request`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//        val newElem = AddressSearchForm("Ivan", "Moscow")
//
//
//        val resp = restTemplate.exchange(url("api/app/id2/edit"),
//            HttpMethod.PUT,
//            HttpEntity(newElem, headers),
//            object : ParameterizedTypeReference<Pair<String, AddressInfo>>() {})
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
//        assertThat(resp.body?.first).isEqualTo("id2")
//    }
//
//    @Test
//    fun `put edit request not found`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//        val newElem = AddressSearchForm("Ivan", "Moscow")
//
//
//        val resp = restTemplate.exchange(url("api/app/id0/edit"),
//            HttpMethod.PUT,
//            HttpEntity(newElem, headers),
//            object : ParameterizedTypeReference<Pair<String, AddressInfo>>() {})
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
//    }
//
//    @Test
//    fun `delete delete request`() {
//        val headers = HttpHeaders()
//        headers.set("Cookie", "auth=2021-10-22T09:50:55.985703Z")
//        headers.contentType = MediaType.APPLICATION_JSON
//
//
//        val resp = restTemplate.exchange(url("api/app/id1/delete"),
//            HttpMethod.DELETE,
//            HttpEntity(null, headers),
//            Any::class.java)
//
//        assertThat(resp.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
//    }
}