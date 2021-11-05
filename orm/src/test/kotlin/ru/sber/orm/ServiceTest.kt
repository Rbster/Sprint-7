package ru.sber.orm

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.sber.orm.persistence.Actor
import ru.sber.orm.persistence.Genre
import ru.sber.orm.persistence.Movie
import ru.sber.orm.service.MovieRepoService

@SpringBootTest(classes = [MovieRepoService::class])
class ServiceTest {

    lateinit var movieRepoService: MovieRepoService
    @Autowired set

    @Test
    fun `some test`() {
        val movie = Movie(title = "Silence")
        movieRepoService.dao.save(movie)
        println(movieRepoService.dao.findAll())
        assert(movieRepoService.dao.findAll().filter { it.id != movie.id }.isNotEmpty())
    }
}