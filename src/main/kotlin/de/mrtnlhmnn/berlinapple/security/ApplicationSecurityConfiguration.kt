package de.mrtnlhmnn.berlinapple.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter //TODO deprecated

@Configuration
@EnableWebSecurity
open class ApplicationSecurityConfiguration(val userRepo: UserRepo) : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder?) {
        val users = arrayOf(User("1", "martin"), User("2", "kristine"))
        for (user in users) {
            userRepo.put(user.id, user)
            auth!!
                    .inMemoryAuthentication()
                    .withUser(user.name)
                    .password("{bcrypt}\$2a\$10\$OLdoPrOcYLcTQLXQAPH6E.xI2zQ9Slw6BYQCVojvV0G0CDVltaYsu")
                    .roles("USER");
        }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
    }
}
