package ru.sber.orm

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

    val genre = Genre(name = "Comedy")
    val cast = listOf(Actor(fullname = "Angelina Joly"), Actor(fullname = "Bred Pit"))
    val movie = Movie(title = "Mr and Mrs Smith",
        genre = genre,
        cast = cast)


    @BeforeEach
    fun setUp() {
        movieRepoService.dao.save(movie)
    }

    @AfterEach
    fun atLast() {
        for (movie in movieRepoService.dao.findAll()) {
            movieRepoService.dao.delete(movie)
        }

        movieRepoService.sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.delete(genre)

            for (actor in cast) {
                session.delete(actor)
            }
            session.transaction.commit()
        }
    }

    @Test
    fun `save test`() {
        val movie = Movie(title = "Fight club", genre = genre, cast = listOf(cast[0]))

        movieRepoService.dao.save(movie)

        assert(movieRepoService.dao.findAll().any { it.title == movie.title })
    }



    @Test
    fun `update test`() {
        movie.cast = listOf(cast[0])

        movieRepoService.dao.update(movie)

        assert(movieRepoService.dao.findAll().any { it.title == movie.title &&
                it.cast.map { it.fullname }.toSet() == movie.cast.map { it.fullname }.toSet()})
    }
}