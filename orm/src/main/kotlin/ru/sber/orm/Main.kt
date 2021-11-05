//import enteties.HomeAddress

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import ru.sber.orm.persistence.Actor
import ru.sber.orm.persistence.Genre
import ru.sber.orm.persistence.Movie
import java.time.LocalDate

fun main() {
//    val sessionFactory = Configuration().configure()
//        .addAnnotatedClass(Movie::class.java)
//        .addAnnotatedClass(Actor::class.java)
//        .addAnnotatedClass(Genre::class.java)
//        .buildSessionFactory()

//    sessionFactory.use { sessionFactory ->
//        val dao = StudentDAO(sessionFactory)
//
//        val student1 = Student(
//            name = "Petr",
//            email = "petr@student.ru",
//            studyType = StudyType.FULL_TIME,
//            birthDate = LocalDate.now().minusYears(20),
//            personalData = PersonalData("123", "74839"),
//            homeAddress = HomeAddress(street = "Кутузовский пр-т")
//        )
//        val student2 = Student(
//            name = "Ivan",
//            email = "ivan@student.ru",
//            studyType = StudyType.PART_TIME,
//            birthDate = LocalDate.now().minusYears(24),
//            personalData = PersonalData("543", "341444"),
//            homeAddress = HomeAddress(street = "Ленина")
//        )
//
//        dao.save(student1)
//
//        dao.save(student2)
//
//        var found = dao.find(student1.id)
//        println("Найден студент: $found \n")
//
//        found = dao.find(student2.email)
//        println("Найден студент: $found \n")
//
//        val allStudents = dao.findAll()
//        println("все студенты: $allStudents")
//
//    }



}

//class StudentDAO(
//    private val sessionFactory: SessionFactory
//) {
//    fun save(student: Student) {
//        sessionFactory.openSession().use { session ->
//            session.beginTransaction()
//            session.save(student)
//            session.transaction.commit()
//        }
//    }
//
//    fun find(id: Long): Student? {
//        val result: Student?
//        sessionFactory.openSession().use { session ->
//            session.beginTransaction()
//            result = session.get(Student::class.java, id)
//            session.transaction.commit()
//        }
//        return result
//    }
//
//    fun find(email: String): Student? {
//        val result: Student?
//        sessionFactory.openSession().use { session ->
//            session.beginTransaction()
//            result =
//                session.byNaturalId(Student::class.java).using("email", email).loadOptional().orElse(null)
//            session.transaction.commit()
//        }
//        return result
//    }
//
//    fun findAll(): List<Student> {
//        val result: List<Student>
//        sessionFactory.openSession().use { session ->
//            session.beginTransaction()
//            result = session.createQuery("from Student").list() as List<Student>
//            session.transaction.commit()
//        }
//        return result
//    }
//}