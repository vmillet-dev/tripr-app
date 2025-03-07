package com.adsearch.infrastructure.repository.entity

import com.adsearch.domain.model.User
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(unique = true, nullable = false)
    val username: String,
    
    @Column(nullable = false)
    val password: String,
    
    @ElementCollection(fetch = FetchType.EAGER)
    val roles: MutableList<String> = mutableListOf("USER")
) {
    // Default constructor required by JPA
    constructor() : this(
        id = UUID.randomUUID(),
        username = "",
        password = "",
        roles = mutableListOf()
    )
    
    fun toDomain(): User {
        return User(
            id = id,
            username = username,
            password = password,
            roles = roles
        )
    }
    
    companion object {
        fun fromDomain(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                username = user.username,
                password = user.password,
                roles = user.roles.toMutableList()
            )
        }
    }
}
