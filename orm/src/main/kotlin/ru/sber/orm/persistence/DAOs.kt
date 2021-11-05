package ru.sber.orm.persistence

import org.hibernate.SessionFactory

class MovieDAO(private val sessionFactory: SessionFactory) {
    fun save(movie: Movie) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.save(movie)
            session.transaction.commit()
        }
    }

    fun update(movie: Movie) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.saveOrUpdate(movie)
            session.transaction.commit()
        }
    }

    fun delete(movie: Movie) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.delete(movie)
            session.transaction.commit()
        }
    }
    fun find(id: Long): Movie? {
        val result: Movie?
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            result = session.get(Movie::class.java, id)
            session.transaction.commit()
        }
        return result
    }

    fun findAll(): List<Movie> {
        val result: List<Movie>
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            result = session.createQuery("from movie").list() as List<Movie>
            session.transaction.commit()
        }
        return result
    }

}