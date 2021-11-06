package ru.sber.mvcExample.servlet

import java.net.URLClassLoader
import java.nio.file.Paths
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.servlet.ServletContext
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.io.path.inputStream
import kotlin.streams.asSequence


class LoginServlet(private val clock: Clock) : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        var cookie: Cookie? = null

        if (req.cookies != null) {
            for (q in req.cookies) {
                if (q.name == "auth") {
                    cookie = q
                }
            }
        }

        println("-------------->" + clock.instant().toString())

        if (cookie == null ||
                    Instant.from(
                        DateTimeFormatter.ISO_INSTANT.parse(cookie.value)
                    ) >= clock.instant() ) {

            resp.contentType = "text/html"

            val outputStream = resp.outputStream
//            println(Paths.get("").toAbsolutePath())
//            for (url in (Thread.currentThread().contextClassLoader as URLClassLoader).urLs) {
//                println("----->ClassPAth $url")
//            }
//            println("------>ClassPath size = ${(Thread.currentThread().contextClassLoader as URLClassLoader).urLs.size}")
            Paths.get("spring-mvc/src/main/resources/templates/login.html").inputStream().use { inputStream ->
                inputStream.transferTo(outputStream)
            }
            outputStream.flush()
            outputStream.close() // should I?


/*
            println("---> resourcePaths size ${servletContext.getResourcePaths("/alt/login")}")
            println(servletContext.getRealPath("/login.html"))
            val reqDispatcher = req.getRequestDispatcher("/login.html")
            println("---> Dispatcher $reqDispatcher")

            reqDispatcher.forward(req, resp)
            */
        } else {
            resp.sendRedirect("/app/list")
        }
    }


    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val login = "admin"
        val password = "admin"
        val body = req.reader.lines().asSequence().filterNotNull().joinToString("").filter { !it.isWhitespace() }
        val isToRest = req.getHeader("Content-Type") == "application/json"
        val isToWeb = req.getHeader("Content-Type")
            .split("; ")[0] == "application/x-www-form-urlencoded"

        val isRightLogin = if (isToRest) {
            body == "{\"log\":\"${login}\",\"password\":\"${password}\"}"
        } else if (isToWeb) {
            body == "log=${login}&password=${password}"
        } else false

        var cookie: Cookie? = null

        println("------------>${req.attributeNames.toList()}")
        println("------------>${body}")
        println("------------>${req.getHeader("Content-Type")}")


        if (req.cookies != null) {
            for (q in req.cookies) {
                if (q.name == "auth") {
                    cookie = q
                }
            }
        }
        println("------------> verify password = ${
            (req.getHeader("Content-Type") == "application/x-www-form-urlencoded" )
                && (body == "log=${login}&password=${password}")
                || (req.getHeader("Content-Type") == "application/json")
                && (body == "{\"log\":\"${login}\",\"password\":\"${password}\"}")}")
        println("-------------->" + clock.instant().toString())

        if (cookie == null ||
            Instant.from(
                DateTimeFormatter.ISO_INSTANT.parse(cookie.value)
            ) >= clock.instant() ) {

            if (isRightLogin) {
                println("----------> LOG IN")
                val newCookie = Cookie("auth", clock.instant().toString())
                newCookie.path = "/"
                resp.addCookie(newCookie)
                resp.sendRedirect(if (isToWeb) "/app/list" else "/api/app/list")
            } else {
                if (isToWeb) {
                    resp.sendRedirect("/alt/login/form")
                } else {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN)
                }
            }
        } else {
            resp.sendRedirect("/app/list")
        }
    }
}