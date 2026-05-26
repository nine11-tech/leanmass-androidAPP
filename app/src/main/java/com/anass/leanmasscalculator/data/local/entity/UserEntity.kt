package com.anass.leanmasscalculator.data.local.entity

data class UserEntity(
    val id: Long,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long
)
