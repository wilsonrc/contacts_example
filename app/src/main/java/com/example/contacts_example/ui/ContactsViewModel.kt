package com.example.contacts_example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contacts_example.data.Contact
import com.example.contacts_example.data.source.ContactsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed interface for UI State
sealed interface ContactsUiState {
    data class Success(val contacts: List<Contact>) : ContactsUiState
    data class Error(val message: String) : ContactsUiState
    object Loading : ContactsUiState
    object Empty : ContactsUiState // Represents an initial or empty state
}

sealed interface CreateContactUiState {
    object Success : CreateContactUiState
    data class Error(val message: String) : CreateContactUiState
    object Loading : CreateContactUiState
    object Idle : CreateContactUiState // Initial state before creation attempt
}

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactsRepository: ContactsRepository
) : ViewModel() {

    // For fetching the list of contacts
    private val _contactsUiState = MutableStateFlow<ContactsUiState>(ContactsUiState.Empty)
    val contactsUiState: StateFlow<ContactsUiState> = _contactsUiState.asStateFlow()

    // For the state of creating a new contact
    private val _createContactUiState = MutableStateFlow<CreateContactUiState>(CreateContactUiState.Idle)
    val createContactUiState: StateFlow<CreateContactUiState> = _createContactUiState.asStateFlow()

    init {
        // Optionally, load contacts when the ViewModel is created
        // getContactsList()
    }

    fun getContactsList() {
        viewModelScope.launch {
            _contactsUiState.value = ContactsUiState.Loading
            val result = contactsRepository.getContacts()
            result.fold(
                onSuccess = { contactResults ->
                    _contactsUiState.value = ContactsUiState.Success(contactResults.results)
                },
                onFailure = { throwable ->
                    _contactsUiState.value = ContactsUiState.Error(
                        throwable.message ?: "Unknown error fetching contacts"
                    )
                }
            )
        }
    }

    fun createContact(firstName: String, lastName: String, phone: String) { // Removed avatarUrl for now
        viewModelScope.launch {
            _createContactUiState.value = CreateContactUiState.Loading

            // Construct the Contact object according to your ContactResult.kt definition
            val newContact = Contact(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phone
                // If your Contact data class evolves to include an ID or other fields,
                // you'll need to adjust this construction accordingly.
            )

            val result = contactsRepository.addContact(newContact)
            result.fold(
                onSuccess = {
                    _createContactUiState.value = CreateContactUiState.Success
                    // Optionally, refresh the contacts list after successful creation
                    getContactsList()
                    // Reset state after a short delay or navigation
                    // kotlinx.coroutines.delay(200) // Example delay
                    // _createContactUiState.value = CreateContactUiState.Idle
                },
                onFailure = { throwable ->
                    _createContactUiState.value = CreateContactUiState.Error(
                        throwable.message ?: "Unknown error creating contact"
                    )
                }
            )
        }
    }

    /**
     * Resets the create contact UI state to Idle.
     * Call this after handling a success or error message in the UI.
     */
    fun resetCreateContactState() {
        _createContactUiState.value = CreateContactUiState.Idle
    }
}