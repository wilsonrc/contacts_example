package com.example.contacts_example.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.contacts_example.ui.theme.Contacts_exampleTheme

// Modelo de datos simple para el contacto
data class Contact(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    // Normalmente, estos vendrían de un ViewModel
    contacts: List<Contact>,
    onAddNewContact: () -> Unit,
    onDeleteSelectedContacts: () -> Unit, // Se llamaría cuando se presiona borrar
    // Para la búsqueda
    searchTerm: String,
    onSearchTermChange: (String) -> Unit
) {
    var selectedContactIds by remember { mutableStateOf(emptySet<String>()) }

    Scaffold(
        topBar = {
            ContactListTopAppBar(
                title = "Contactos",
                onAddNewContact = onAddNewContact,
                onDeleteSelectedContacts = {
                    onDeleteSelectedContacts() // Llama a la lógica de borrado
                    selectedContactIds = emptySet() // Limpia la selección después de borrar
                },
                isDeleteEnabled = selectedContactIds.isNotEmpty()
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            SearchBar(
                searchTerm = searchTerm,
                onSearchTermChange = onSearchTermChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            ContactLazyList(
                contacts = contacts,
                selectedContactIds = selectedContactIds,
                onContactSelected = { contactId, isSelected ->
                    selectedContactIds = if (isSelected) {
                        selectedContactIds + contactId
                    } else {
                        selectedContactIds - contactId
                    }
                },
                modifier = Modifier.weight(1f) // Para que la lista ocupe el espacio restante
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListTopAppBar(
    title: String,
    onAddNewContact: () -> Unit,
    onDeleteSelectedContacts: () -> Unit,
    isDeleteEnabled: Boolean
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        actions = {
            IconButton(onClick = onAddNewContact) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Añadir nuevo contacto"
                )
            }
            IconButton(onClick = onDeleteSelectedContacts, enabled = isDeleteEnabled) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar contactos seleccionados",
                    tint = if (isDeleteEnabled) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    )
}

@Composable
fun SearchBar(
    searchTerm: String,
    onSearchTermChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchTerm,
        onValueChange = onSearchTermChange,
        label = { Text("Buscar contacto") },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun ContactLazyList(
    contacts: List<Contact>,
    selectedContactIds: Set<String>,
    onContactSelected: (contactId: String, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (contacts.isEmpty()) {
        // Podrías mostrar un mensaje de "No hay contactos" aquí
        Text(
            text = "No se encontraron contactos.",
            modifier = modifier.padding(16.dp)
        )
        return
    }

    LazyColumn(modifier = modifier) {
        items(contacts, key = { contact -> contact.id }) { contact ->
            ContactItem(
                contact = contact,
                isSelected = contact.id in selectedContactIds,
                onSelectedChange = { isSelected ->
                    onContactSelected(contact.id, isSelected)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem(
    contact: Contact,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    // Implementación básica del item. Puedes hacerlo más complejo
    // con selección, avatar, etc.
    androidx.compose.material3.ListItem(
        headlineContent = { Text("${contact.firstName} ${contact.lastName}") },
        supportingContent = { Text(contact.phoneNumber) },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        leadingContent = {
            // Aquí podrías poner un Checkbox para la selección múltiple
            // o un avatar del contacto
            androidx.compose.material3.Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectedChange
            )
        }
        // trailingContent = { Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones") } // Opcional
    )
    // Para una implementación más simple sin ListItem:
    /*
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectedChange(!isSelected) } // Permite seleccionar/deseleccionar
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox para selección (opcional, como buena práctica)
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectedChange
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = "${contact.firstName} ${contact.lastName}", style = MaterialTheme.typography.bodyLarge)
            Text(text = contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
        }
    }
    */
}


// --- Preview ---
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, name = "Pantalla de Lista de Contactos")
@Composable
fun ContactListScreenPreview() {
    val sampleContacts = remember {
        mutableStateOf(listOf(
            Contact("1", "Menganito", "De Tal", "600111222"),
            Contact("2", "Fulanita", "Mengánez", "600333444"),
            Contact("3", "Perenganito", "López", "600555666")
        ))
    }
    var searchTerm by remember { mutableStateOf("") }

    Contacts_exampleTheme {
        ContactListScreen(
            contacts = sampleContacts.value.filter {
                it.firstName.contains(searchTerm, ignoreCase = true) ||
                        it.lastName.contains(searchTerm, ignoreCase = true) ||
                        it.phoneNumber.contains(searchTerm, ignoreCase = true)
            },
            onAddNewContact = { /* Acción para nuevo contacto */ },
            onDeleteSelectedContacts = { /* Acción para borrar */ },
            searchTerm = searchTerm,
            onSearchTermChange = { newTerm -> searchTerm = newTerm }
        )
    }
}

@Preview(showBackground = true, name = "Item de Contacto")
@Composable
fun ContactItemPreview() {
    Contacts_exampleTheme {
        ContactItem(
            contact = Contact("1", "Menganito", "De Tal", "600111222"),
            isSelected = false,
            onSelectedChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Item de Contacto Seleccionado")
@Composable
fun ContactItemSelectedPreview() {
    Contacts_exampleTheme {
        ContactItem(
            contact = Contact("1", "Menganito", "De Tal", "600111222"),
            isSelected = true,
            onSelectedChange = {}
        )
    }
}

@Preview(showBackground = true, name = "Barra Superior")
@Composable
fun ContactListTopAppBarPreview() {
    Contacts_exampleTheme {
        ContactListTopAppBar(
            title = "Mis Contactos",
            onAddNewContact = {},
            onDeleteSelectedContacts = {},
            isDeleteEnabled = true
        )
    }
}

@Preview(showBackground = true, name = "Barra de Búsqueda")
@Composable
fun SearchBarPreview() {
    var text by remember { mutableStateOf("Bus") }
    Contacts_exampleTheme {
        SearchBar(searchTerm = text, onSearchTermChange = { text = it })
    }
}