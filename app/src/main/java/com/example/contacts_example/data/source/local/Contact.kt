package com.example.contacts_example.data.source.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val id: String, // Assuming 'id' is a unique identifier
    val name: String,
    val phoneNumber: String,
    val avatarUrl: String? = null, // Optional field for avatar URL
)

