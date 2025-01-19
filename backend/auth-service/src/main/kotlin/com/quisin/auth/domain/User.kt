package com.quisin.auth.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    private var email: String,

    private var password: String,

    @Enumerated(EnumType.STRING)
    var role: Role,

    var firstName: String,
    var lastName: String,

    @Column(unique = true)
    var restaurantId: UUID? = null,

    var enabled: Boolean = true,
    var locked: Boolean = false,

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null,

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedBy
    var lastModifiedBy: String? = null,

    @LastModifiedDate
    var lastModifiedAt: LocalDateTime? = null
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${role.name}"))

    override fun getPassword(): String = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = !locked

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = enabled

    fun updatePassword(newPassword: String) {
        password = newPassword
    }
}
