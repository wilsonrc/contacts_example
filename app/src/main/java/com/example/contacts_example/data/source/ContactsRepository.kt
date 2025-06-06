package com.example.contacts.data.sources

import com.example.contacts_example.data.Contact
import com.example.contacts_example.data.ContactResults
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsRepository @Inject constructor(
    private val remoteDataSource: IContactsDataSource,
    private val dispatcher: CoroutineDispatcher
) : IContactsRepository {
    override suspend fun getContacts(): Result<ContactResults> {
        return withContext(dispatcher) {
            remoteDataSource.getContacts()
        }
    }

    override suspend fun getContactById(id: String): Result<Contact> {
        return withContext(dispatcher) {
            remoteDataSource.getContactById(id)
        }
    }

    override suspend fun addContact(contact: Contact): Result<Unit> {
        return withContext(dispatcher) {
            remoteDataSource.addContact(contact)
        }
    }

    override suspend fun updateContact(contact: Contact): Result<Unit> {
        return withContext(dispatcher) {
            remoteDataSource.updateContact(contact)
        }
    }


    override suspend fun deleteContact(id: String): Result<Unit> {
        return withContext(dispatcher) {
            remoteDataSource.deleteContact(id)
        }
    }
}