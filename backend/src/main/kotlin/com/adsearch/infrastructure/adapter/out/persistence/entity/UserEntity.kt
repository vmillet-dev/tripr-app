package com.adsearch.infrastructure.adapter.out.persistence.entity

import com.adsearch.domain.model.User
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    val roles: MutableList<String> = mutableListOf("USER")
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        password = password,
        roles = roles.toList()
    )

    companion object {
        fun fromDomain(user: User): UserEntity = with(user) {
            UserEntity(
                id = id,
                username = username,
                password = password,
                roles = roles.toMutableList()
            )
        }
    }
}
