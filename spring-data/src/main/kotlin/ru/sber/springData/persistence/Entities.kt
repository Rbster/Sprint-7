package ru.sber.springData.persistence

import org.hibernate.annotations.*
import org.hibernate.annotations.CascadeType
import javax.persistence.*
import javax.persistence.Entity


@Entity(name = "movie")
data class Movie(
    @Id
    @GeneratedValue
    @Column(name = "movie_id")
    var id: Long? = null,

    @Column(unique = true)
    var title: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "movie_actor",
        joinColumns = [JoinColumn(name = "movie_id")],
        inverseJoinColumns = [JoinColumn(name = "actor_id")]
    )
    @Fetch(FetchMode.SUBSELECT)  // cure N + 1
    @Cascade(CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST)
    var cast: List<Actor> = listOf(),

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST)
    var genre: Genre? = null
)

@Entity(name = "actor")
data class Actor(
    @Id
    @GeneratedValue
    @Column(name = "actor_id")
    var id: Long? = null,

    @NaturalId
    var fullname: String? = null,

    @ManyToMany(mappedBy = "cast", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST)
    var performedIn: List<Movie> = listOf()
)

@Entity(name = "genre")
data class Genre(
    @Id
    @GeneratedValue
    @Column(name = "genre_id")
    var id: Long? = null,

    @NaturalId
    var name: String? = null,

    @OneToMany(mappedBy = "genre", fetch = FetchType.EAGER)
    @Cascade(CascadeType.SAVE_UPDATE, CascadeType.MERGE, CascadeType.PERSIST)
    var movies: List<Movie> = listOf()
)


