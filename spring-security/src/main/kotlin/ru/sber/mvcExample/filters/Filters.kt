package ru.sber.mvcExample.filters

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
@Order(1)
class LoggingFilter : Filter {
    companion object {
        var logger: Log = LogFactory.getLog(this::class.java)
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        println("-----> Filtering in CheckAuthFilter")
        val httpRequest = request as HttpServletRequest
        logger.info(httpRequest.method + " request from " + httpRequest.remoteAddr + " to " + httpRequest.requestURI)

        chain!!.doFilter(request, response)
        println("<----- out CheckAuthFilter")
    }
}

@Component
@Order(2)
class AuthFilter : Filter {
    lateinit var clock: Clock
    @Autowired set

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        println("-----> Filtering in AuthFilter")
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse
        var cookie: Cookie? = null
        val loginURI = request.contextPath + "/alt/login/form"
        val authURI = request.contextPath + "/alt/login/auth"

        val requestURI = request.requestURI

        if (httpRequest.cookies != null) {
            for (q in httpRequest.cookies) {
                if (q.name == "auth") {
                    cookie = q
                }
            }
        }
        println("---------- cookie = ${cookie?.name} : ${cookie?.value} ")
        if (requestURI != loginURI
            && requestURI != authURI
            && (cookie == null ||
                    Instant.from(
                        DateTimeFormatter.ISO_INSTANT.parse(cookie.value)
                    ) >= clock.instant() )) {
            // redirect

            println("<----- redirect from AuthFilter")
            httpResponse.sendRedirect(loginURI)

        } else {
            chain!!.doFilter(request, response)
        }
        println("<----- out AuthFilter")
    }

}

