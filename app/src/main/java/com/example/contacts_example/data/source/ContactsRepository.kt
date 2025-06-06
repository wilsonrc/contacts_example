package com.example.contacts_example.data.source

import com.example.contacts.data.sources.IContactsDataSource
import com.example.contacts.data.sources.IContactsRepository
import com.example.contacts_example.data.Contact
import com.example.contacts_example.data.ContactResults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    private val localDataSource: IContactsDataSource,
    private val dispatcher: CoroutineDispatcher
) : IContactsRepository {
    override suspend fun getContacts(): Result<ContactResults> {
        return withContext(dispatcher) {
            localDataSource.getContacts()
        }
    }

    override suspend fun getContactById(id: String): Result<Contact> {
        return withContext(dispatcher) {
            localDataSource.getContactById(id)
        }
    }

    override suspend fun addContact(contact: Contact): Result<Unit> {
        return withContext(dispatcher) {
            localDataSource.addContact(contact)
        }
    }

    override suspend fun updateContact(contact: Contact): Result<Unit> {
        return withContext(dispatcher) {
            localDataSource.updateContact(contact)
        }
    }


    override suspend fun deleteContact(id: String): Result<Unit> {
        return withContext(dispatcher) {
            localDataSource.deleteContact(id)
        }
    }
}