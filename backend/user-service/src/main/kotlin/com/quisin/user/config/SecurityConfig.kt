package com.quisin.user.config

import com.quisin.user.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.disable() }
            .sessionManagement { 
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            }
            .authorizeHttpRequests { auth -> 
                auth
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**"
                    ).permitAll()
                    .requestMatchers("/users/admin/**").hasRole("SUPER_ADMIN")
                    .requestMatchers("/users/staff/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
