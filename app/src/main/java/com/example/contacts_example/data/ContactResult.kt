package com.example.contacts_example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.contacts_example.data.source.local.Contact

data class ContactResults(
    var resultCount: Int,
    var results: List<Contact>
)

@Entity(tableName = "contact_results") // You can optionally specify a table name
data class ContactResult(
    @PrimaryKey(autoGenerate = true) // It's common to have a primary key
    val id: Int = 0,
    // Add other fields relevant to your ContactResult data
    val name: String,
    val phoneNumber: String
    // ... other properties
)