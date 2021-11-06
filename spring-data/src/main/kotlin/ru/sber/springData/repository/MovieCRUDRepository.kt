package ru.sber.springData.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.sber.springData.persistence.Movie

@Repository
interface MovieCRUDRepository : CrudRepository<Movie, Long> {
//    throws Exception because of restrictions in movie_actor table. I suppose...
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from movie m")
    fun deleteInBulkAll()

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from movie m where m.id = ?1")
    fun deleteInBulkById(id: Long)

}