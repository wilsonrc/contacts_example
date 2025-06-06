package com.example.contacts.data.sources

import com.example.contacts_example.data.Contact
import com.example.contacts_example.data.ContactResults

interface IContactsDataSource {
    suspend fun getContacts(): Result<ContactResults>
    suspend fun getContactById(id: String): Result<Contact>
    suspend fun addContact(contact: Contact): Result<Unit>
    suspend fun updateContact(contact: Contact): Result<Unit>
    suspend fun deleteContact(id: String): Result<Unit>
}