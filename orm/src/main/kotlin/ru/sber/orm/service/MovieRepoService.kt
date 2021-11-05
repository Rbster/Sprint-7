package ru.sber.orm.service

import org.hibernate.cfg.Configuration
import ru.sber.orm.persistence.Actor
import ru.sber.orm.persistence.Genre
import ru.sber.orm.persistence.Movie
import ru.sber.orm.persistence.MovieDAO

//@Service
class MovieRepoService {
    val sessionFactory = Configuration().configure()
        .addAnnotatedClass(Movie::class.java)
        .addAnnotatedClass(Actor::class.java)
        .addAnnotatedClass(Genre::class.java)
        .buildSessionFactory()
    val dao = MovieDAO(sessionFactory)
}