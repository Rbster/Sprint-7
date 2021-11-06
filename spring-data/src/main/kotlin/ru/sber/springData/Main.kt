package ru.sber.springData

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.sber.springData.persistence.Actor
import ru.sber.springData.persistence.Genre
import ru.sber.springData.persistence.Movie
import ru.sber.springData.repository.MovieCRUDRepository

@SpringBootApplication
class SpringJpaDemoApplication(
    private val movieCRUDRepository: MovieCRUDRepository
) : CommandLineRunner {
    override fun run(vararg args: String?) {

        fun printFoundMovies(found: Iterable<Movie>) {
            val resultMovieNames = found.map { it.title }.toList()
            val resultCasts = found.map { it.cast.map { actor -> actor.fullname } }.toList()
            val resultGenres = found.map { it.genre?.name }.toList()
            println("----------")
            println(resultMovieNames)
            println(resultCasts)
            println(resultGenres)
            println("----------")
        }

        val genres = listOf(Genre(name = "Comedy"), Genre(name = "Drama"))
        val actors = listOf(Actor(fullname = "Morgan Freeman"), Actor(fullname = "Tim Robbins"), Actor(fullname = "Jim Carry"))
        val titles = listOf("The Shawshank Redemption", "Bruce Almighty")

        val movies = listOf(
            Movie(title = titles[0], cast = actors.subList(0, 2), genre = genres[1]),
            Movie(title = titles[1], cast = actors.subList(0, 1), genre = genres[0])
        )
        // insert example
        movieCRUDRepository.saveAll(movies)
        printFoundMovies(movieCRUDRepository.findAll())

        // update example
        movies[1].cast = listOf(actors[0], actors[2])
        movieCRUDRepository.save(movies[1])
        printFoundMovies(movieCRUDRepository.findAll())

        // delete example
//        movieCRUDRepository.deleteAll()

        // doesn't do anything
        // found https://stackoverflow.com/questions/63452499/spring-data-problem-derived-delete-doesnt-work
        // but movieCRUDRepository.deleteInBulkAll throws Exception , because of movie_actor restriction I suppose...

        // https://stackoverflow.com/questions/44322974/invaliddataaccessapiusageexception-executing-an-update-delete-query-spring-xml
        // @Transactional fixed the problem

        val ids = movieCRUDRepository.findAll().map { it.id }.toList()
        ids[0]?.let { movieCRUDRepository.deleteInBulkById(it) }
        printFoundMovies(movieCRUDRepository.findAll())

    }
}

fun main(args: Array<String>) {
    runApplication<SpringJpaDemoApplication>(*args)
}
