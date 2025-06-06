package com.example.contacts_example.data.source.local

import com.example.contacts_example.data.source.IContactsDataSource
import com.example.contacts_example.data.ContactResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsLocalDataSource(
    private val contactDao: ContactDao // Inject the DAO
) : IContactsDataSource {

    override suspend fun getContacts(): Result<ContactResults> = withContext(Dispatchers.IO) {
        try {
            val contacts = contactDao.getAllContacts()
            Result.success(ContactResults(resultCount = contacts.size, results = contacts))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getContactById(id: String): Result<Contact> =
        withContext(Dispatchers.IO) {
        try {
            val contact = contactDao.getContactById(id)
            if (contact != null) {
                Result.success(contact)
            } else {
                Result.failure(Exception("Contact not found with id: $id"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addContact(contact: Contact): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            contactDao.insertContact(contact)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateContact(contact: Contact): Result<Unit> =
        withContext(Dispatchers.IO) {
        try {
            val updatedRows = contactDao.updateContact(contact)
            if (updatedRows > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to update contact or contact not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteContact(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val deletedRows = contactDao.deleteContactById(id)
            if (deletedRows > 0) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete contact or contact not found."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}