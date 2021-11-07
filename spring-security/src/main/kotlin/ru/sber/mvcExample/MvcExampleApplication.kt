package ru.sber.mvcExample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import ru.sber.mvcExample.servlet.LoginServlet
import java.time.Clock
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.security.config.web.servlet.invoke
import javax.servlet.Filter

@EnableWebSecurity
class SecurityConfig : WebSecurityConfigurerAdapter() {
	@Autowired
	private var authenticationEntryPoint: MyBasicAuthenticationEntryPoint? = null

	@Bean
	fun users(): UserDetailsService {
		// The builder will ensure the passwords are encoded before saving in memory
		val user = User.builder()
			.username("user")
			.password(passwordEncoder().encode("user"))
			.roles("USER")
			.build()
		val admin = User.builder()
			.username("admin")
			.password(passwordEncoder().encode("user"))
			.roles("USER", "ADMIN")
			.build()
		return InMemoryUserDetailsManager(user, admin)
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}

	override fun configure(http: HttpSecurity) {
//		http.authorizeRequests()
//			.antMatchers("/securityNone").permitAll()
//			.anyRequest().authenticated()
//			.and()
//			.httpBasic()
//			.authenticationEntryPoint(authenticationEntryPoint)
		http {
			authorizeRequests {
				authorize("/app/**", authenticated)
				authorize("/api/**", "hasRole('ADMIN') or hasRole('API')")
				authorize("/login", anonymous)
				authorize(anyRequest, denyAll)
			}
			formLogin {
				loginPage = "/login"
			}
		}
	}
}


@Component
class MyBasicAuthenticationEntryPoint : BasicAuthenticationEntryPoint() {
	override fun afterPropertiesSet() {
		realmName = "app"
		super.afterPropertiesSet()
	}
}



//@Configuration
//@EnableWebSecurity
//class CustomWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {
//	@Autowired
//	private val authenticationEntryPoint: MyBasicAuthenticationEntryPoint? = null
//	@Autowired
//	@Throws(Exception::class)
//	fun configureGlobal(auth: AuthenticationManagerBuilder) {
//		auth.inMemoryAuthentication()
//			.withUser("user1").password(passwordEncoder().encode("user1Pass"))
//			.authorities("ROLE_USER")
//	}
//
//	@Throws(Exception::class)
//	override fun configure(http: HttpSecurity) {
//		http.authorizeRequests()
//			.antMatchers("/securityNone").permitAll()
//			.anyRequest().authenticated()
//			.and()
//			.httpBasic()
//			.authenticationEntryPoint(authenticationEntryPoint)
////		http.addFilterAfter(
////			CustomFilter(),
////			BasicAuthenticationFilter::class.java
////		)
//	}
//
//	@Bean
//	fun passwordEncoder(): PasswordEncoder {
//		return BCryptPasswordEncoder()
//	}
//}


@SpringBootApplication
@EnableWebMvc
class MvcExampleApplication {
	@Bean
	fun loginServletBean(clock: Clock): ServletRegistrationBean<*> {
		val bean: ServletRegistrationBean<*> = ServletRegistrationBean(
			LoginServlet(clock),
			"/alt/login/*"
		)
		bean.setLoadOnStartup(1)
		println("----->" + bean.servletName)
		return bean
	}

	@Bean
	@Scope("singleton")
	fun clock(): Clock {
		return Clock.systemDefaultZone()
	}
}

fun main(args: Array<String>) {
	runApplication<MvcExampleApplication>(*args)
}