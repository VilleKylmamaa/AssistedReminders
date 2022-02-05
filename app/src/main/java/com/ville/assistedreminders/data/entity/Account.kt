package com.ville.assistedreminders.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [
        Index(value = ["id", "username"], unique = true)
    ]
)

data class Account(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val accountId: Long = 0,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") var password: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "isLoggedIn") var isLoggedIn: Boolean = false
)