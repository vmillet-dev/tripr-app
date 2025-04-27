package com.adsearch.domain.enum


enum class UserRoleEnum(val id: Long, val type: String)  {
    ADMIN(1, "ADMIN"), USER(2, "USER")
}
