package com.example.contacts_example.data

data class ContactResults(
    var resultCount: Int,
    var results: List<Contact>
)

data class Contact(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)