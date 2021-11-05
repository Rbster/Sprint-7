package ru.sber.orm.persistence

import org.hibernate.SessionFactory
import javax.persistence.*

@Entity(name = "movie")
data class Movie(
    @Id
    @GeneratedValue
    @Column(name = "movie_id")
    var id: Long? = null,

    @Column
    var title: String? = null,

//    @Column
    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(name = "movie_actor",
        joinColumns = [JoinColumn(name = "movie_id")],
        inverseJoinColumns = [JoinColumn(name = "actor_id")]
    )
    var cast: List<Actor> = listOf(),

//    @Column
    @ManyToOne
    var genre: Genre? = null
)

@Entity(name = "actor")
data class Actor(
    @Id
    @GeneratedValue
    @Column(name = "actor_id")
    var id: Long? = null,

    @Column
    var fullname: String? = null,

//    @Column(name = "performed_in")
    @ManyToMany(mappedBy = "cast")
    var performedIn: List<Movie> = listOf()
)

@Entity(name = "genre")
data class Genre(
    @Id
    @GeneratedValue
    @Column(name = "genre_id")
    var id: Long? = null,

    @Column
    var name: String? = null,

//    @Column
    @OneToMany(mappedBy = "genre")
    var movies: List<Movie> = listOf()
)


