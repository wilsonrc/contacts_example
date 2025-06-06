package com.example.contacts_example.data.source

import com.example.contacts_example.data.ContactResults
import com.example.contacts_example.ui.Contact
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
            val result = localDataSource.getContactById(id)
            val contact = Contact(
                id = result.getOrThrow().id,
                firstName = result.getOrThrow().name.split(" ")[0],
                lastName = result.getOrThrow().name.split(" ").getOrElse(1) { "" },
                phoneNumber = result.getOrThrow().phoneNumber,
                avatarUrl = result.getOrThrow().avatarUrl
            )
            Result.success(contact)
        }
    }

    override suspend fun addContact(contact: Contact): Result<Unit> {
        //Mapping logic can be added here if needed
        val mappedContact = com.example.contacts_example.data.source.local.Contact(
            id = contact.id,
            name = contact.firstName + " " + contact.lastName,
            phoneNumber = contact.phoneNumber,
            avatarUrl = ""
        ) // Example of mapping, if needed
        return withContext(dispatcher) {
            localDataSource.addContact(mappedContact)
        }
    }

    override suspend fun updateContact(contact: Contact): Result<Unit> {
        //Mapping logic can be added here if needed
        val mappedContact = com.example.contacts_example.data.source.local.Contact(
            id = contact.id,
            name = contact.firstName + " " + contact.lastName,
            phoneNumber = contact.phoneNumber,
            avatarUrl = ""
        ) // Example of mapping, if needed

        return withContext(dispatcher) {
            localDataSource.updateContact(mappedContact)
        }
    }


    override suspend fun deleteContact(id: String): Result<Unit> {
        return withContext(dispatcher) {
            localDataSource.deleteContact(id)
        }
    }
}