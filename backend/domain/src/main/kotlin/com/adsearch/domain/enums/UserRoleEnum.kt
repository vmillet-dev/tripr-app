package com.adsearch.domain.enums


enum class UserRoleEnum(val id: Long, val type: String) {
    ROLE_ADMIN(1, "ROLE_ADMIN"), ROLE_USER(2, "ROLE_USER")
}
