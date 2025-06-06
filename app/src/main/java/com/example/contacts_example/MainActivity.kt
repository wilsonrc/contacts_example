package com.example.contacts_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.contacts_example.ui.Contact
import com.example.contacts_example.ui.ContactListScreen
import com.example.contacts_example.ui.components.creation.ContactCreationScreen
import com.example.contacts_example.ui.theme.Contacts_exampleTheme
import kotlin.text.contains

// Define a simple sealed class for screen navigation
sealed class Screen {
    object ContactList : Screen()
    object CreateContact : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Contacts_exampleTheme {
                // Main app composable
                ContactApp()
            }
        }
    }
}

@Composable
fun ContactApp() {
    // State to manage the current screen
    var currentScreen by remember { mutableStateOf<Screen>(Screen.ContactList) }

    // State to hold the list of contacts (in-memory for this example)
    val contacts = remember { mutableStateListOf<Contact>() }
    // Add some sample data (optional)
    // remember {
    //     contacts.addAll(listOf(
    //         Contact(UUID.randomUUID().toString(), "John", "Doe", "123-456-7890", "https://picsum.photos/200"),
    //         Contact(UUID.randomUUID().toString(), "Jane", "Smith", "987-654-3210", "https://picsum.photos/201")
    //     ))
    // }


    // State for the search term in the contact list
    var searchTerm by remember { mutableStateOf("") }

    when (currentScreen) {
        is Screen.ContactList -> {
            ContactListScreen(
                contacts = contacts.filter {
                    it.firstName.contains(searchTerm, ignoreCase = true) ||
                            it.lastName.contains(searchTerm, ignoreCase = true) ||
                            it.phoneNumber.contains(searchTerm, ignoreCase = true)
                },
                onAddNewContact = {
                    currentScreen = Screen.CreateContact
                },
                onDeleteSelectedContacts = { /* TODO: Implement contact deletion logic */
                    // Example: You would typically get selected IDs from ContactListScreen
                    // and remove them from the 'contacts' list.
                    // For now, let's assume ContactListScreen handles selection internally
                    // and calls this with IDs to delete.
                    // This part needs further implementation based on how you manage selections.
                },
                searchTerm = searchTerm,
                onSearchTermChange = { newTerm ->
                    searchTerm = newTerm
                }
            )
        }
        is Screen.CreateContact -> {
            ContactCreationScreen(
                onSaveContact = { firstName, lastName, phone, avatarUrl ->
                    // Add the new contact to the list
                    contacts.add(
                        Contact(
                            id = com.android.identity.util.UUID.randomUUID().toString(), // Generate a unique ID
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumber = phone,
                            // You might want to adjust the Contact data class if it expects an avatar URL
                            // For now, I'll assume it has a field for it.
                            // avatarUrl = avatarUrl // Add this if your Contact data class has it
                        )
                    )
                    // Navigate back to the contact list
                    currentScreen = Screen.ContactList
                },
                onCancel = {
                    // Navigate back to the contact list
                    currentScreen = Screen.ContactList
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Contacts_exampleTheme {
        ContactApp()
    }
}