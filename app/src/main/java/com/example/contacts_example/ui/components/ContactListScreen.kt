package com.example.contacts_example.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter // Make sure this is imported
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.contacts_example.R // Assuming R is in this package
import com.example.contacts_example.ui.theme.Contacts_exampleTheme

const val RANDOM_AVATAR_BASE_URL = "https://picsum.photos/200/300" // Example placeholder

data class Contact(
    val id: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val avatarUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    contacts: List<Contact>,
    onAddNewContact: () -> Unit,
    onDeleteSelectedContacts: () -> Unit,
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
                    onDeleteSelectedContacts()
                    selectedContactIds = emptySet()
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
                modifier = Modifier.weight(1f)
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

@Composable
fun AvatarImage(
    avatarUrl: String,
    isLoadingFromParent: Boolean,
    onImageClick: () -> Unit,
    onCoilLoadingStateChange: (isLoading: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val placeholderPainter = painterResource(id = R.drawable.ic_avatar_placeholder)

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(avatarUrl.ifBlank { RANDOM_AVATAR_BASE_URL }) // Use placeholder if URL is blank
            .crossfade(true)
            // .size(coil.size.Size.ORIGINAL) // Optional: If you need to specify size for Coil
            .build(),
        placeholder = placeholderPainter, // Pass placeholder to rememberAsyncImagePainter
        error = placeholderPainter,       // Pass error to rememberAsyncImagePainter
        // fallback = placeholderPainter, // Optional: for a different image if model is null
        onLoading = {
            onCoilLoadingStateChange(true) // Inform parent Coil is loading
            Log.d("AvatarImage", "Coil: Loading image...")
        },
        onSuccess = { successState ->
            onCoilLoadingStateChange(false) // Inform parent Coil finished loading
            Log.d("AvatarImage", "Coil: Image loaded successfully for URL: ${successState.result.request.data}")
        },
        onError = { errorState ->
            onCoilLoadingStateChange(false) // Inform parent Coil finished with error
            Log.e("AvatarImage", "Coil: Error loading image - ${errorState.result.throwable}")
        }
    )

    Box(
        modifier = modifier
            .clip(CircleShape) // Clip before background and clickable for better touch ripple effect
            .background(MaterialTheme.colorScheme.surfaceVariant) // Background for the circle
            .clickable(onClick = onImageClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Avatar del contacto (toca para cambiar)",
            contentScale = ContentScale.Crop, // Crop the image to fill the circle
            modifier = Modifier.fillMaxSize() // Image fills the Box
        )

        // Show progress indicator if parent expects loading AND Coil is actually loading
        // Now we directly check the painter's state
        if (isLoadingFromParent && painter.state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp), // Adjust size as needed
                color = MaterialTheme.colorScheme.onSurfaceVariant // Or primary
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
    ListItem(
        headlineContent = { Text("${contact.firstName} ${contact.lastName}") },
        supportingContent = { Text(contact.phoneNumber) },
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        leadingContent = {
            AvatarImage(
                avatarUrl = contact.avatarUrl ?: RANDOM_AVATAR_BASE_URL, // Provide a fallback
                isLoadingFromParent = false, // Default for list items
                onImageClick = { /* No action for avatar click in list item for now */ },
                onCoilLoadingStateChange = { /* List item doesn't directly react to this */ },
                modifier = Modifier.size(40.dp) // Standard list item avatar size
            )
        },
        trailingContent = {
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectedChange
            )
        }
    )
}


// --- Preview ---
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, name = "Pantalla de Lista de Contactos")
@Composable
fun ContactListScreenPreview() {
    val sampleContacts = remember {
        mutableStateOf(listOf(
            Contact("1", "Menganito", "De Tal", "600111222", avatarUrl = RANDOM_AVATAR_BASE_URL),
            Contact("2", "Fulanita", "Mengánez", "600333444"),
            Contact("3", "Perenganito", "López", "600555666", avatarUrl = RANDOM_AVATAR_BASE_URL)
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
            contact = Contact("1", "Menganito", "De Tal", "600111222", avatarUrl = RANDOM_AVATAR_BASE_URL),
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
            contact = Contact("1", "Menganito", "De Tal", "600111222", avatarUrl = RANDOM_AVATAR_BASE_URL),
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