package com.example.contacts_example.data.source

import com.example.contacts_example.data.ContactResults
import com.example.contacts_example.ui.Contact

interface IContactsRepository {
    suspend fun getContacts(): Result<ContactResults>
    suspend fun getContactById(id: String): Result<Contact>
    suspend fun addContact(contact: Contact): Result<Unit>
    suspend fun updateContact(contact: Contact): Result<Unit>
    suspend fun deleteContact(id: String): Result<Unit>
}
