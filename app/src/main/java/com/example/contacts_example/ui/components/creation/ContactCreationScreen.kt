package com.example.contacts_example.ui.components.creation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.contacts_example.R // Ensure you have ic_avatar_placeholder in drawable
import com.example.contacts_example.ui.theme.Contacts_exampleTheme
import kotlinx.coroutines.launch

// --- Constants ---
private const val RANDOM_AVATAR_BASE_URL = "https://picsum.photos/300/300?random="

// --- Screen Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCreationScreen(
    initialState: ContactCreationState = ContactCreationState(),
    onSaveContact: (firstName: String, lastName: String, phone: String, avatarUrl: String) -> Unit,
    onCancel: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    var state by remember { mutableStateOf(initialState) }
    val coroutineScope = rememberCoroutineScope()

    fun refreshAvatar() {
        // Parent initiates loading state
        state = state.copy(isLoadingAvatar = true, avatarUrl = generateNewAvatarUrl())
    }

    // Initial avatar load if URL is blank or a placeholder
    LaunchedEffect(Unit) {
        if (state.avatarUrl.isBlank() || state.avatarUrl == RANDOM_AVATAR_BASE_URL) {
            refreshAvatar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ContactCreationTopAppBar(
                title = "Crear Contacto",
                onCancel = onCancel
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AvatarImage(
                avatarUrl = state.avatarUrl,
                isLoadingFromParent = state.isLoadingAvatar, // Parent's current understanding of loading state
                onImageClick = {
                    refreshAvatar()
                },
                onCoilLoadingStateChange = { coilIsActuallyLoading ->
                    // Synchronize parent's isLoadingAvatar with Coil's actual state
                    // This is important for when Coil finishes (success/error)
                    // or if Coil starts loading independently of refreshAvatar (less likely here)
                    if (state.isLoadingAvatar != coilIsActuallyLoading) {
                        state = state.copy(isLoadingAvatar = coilIsActuallyLoading)
                    }
                },
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.firstName,
                onValueChange = { state = state.copy(firstName = it) },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.lastName,
                onValueChange = { state = state.copy(lastName = it) },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.phone,
                onValueChange = { newValue ->
                    state = state.copy(phone = newValue.filter { it.isDigit() }.take(15))
                },
                label = { Text("Teléfono") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f)) // Pushes button to the bottom

            Button(
                onClick = {
                    if (state.firstName.isBlank() || state.lastName.isBlank() || state.phone.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Por favor, completa todos los campos.",
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@Button
                    }
                    // Check if avatar is still loading or if the URL is a placeholder/blank
                    if (state.isLoadingAvatar || state.avatarUrl.isBlank() || state.avatarUrl.startsWith(RANDOM_AVATAR_BASE_URL)) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Espera a que cargue el avatar o intenta generar uno nuevo.",
                                duration = SnackbarDuration.Short
                            )
                        }
                        return@Button
                    }
                    onSaveContact(state.firstName, state.lastName, state.phone, state.avatarUrl)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}

// --- Top App Bar Composable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactCreationTopAppBar(
    title: String,
    onCancel: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Cancelar creación"
                )
            }
        },
        actions = {
            // Spacer to balance the title with the navigation icon, making it appear more centered
            Spacer(modifier = Modifier.width(48.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

// --- Avatar Image Composable (Corrected) ---
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
        placeholder = placeholderPainter,
        error = placeholderPainter,
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
            // Consider also setting a specific error avatar or state if desired
        }
        // contentScale and filterQuality are often better applied directly to the Image composable
    )

    Box(
        modifier = modifier
            .clickable(onClick = onImageClick)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), // Background for the circle
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = "Avatar del contacto (toca para cambiar)",
            contentScale = ContentScale.Crop, // Crop the image to fill the circle
            modifier = Modifier.fillMaxSize() // Image fills the Box
        )

        // Show progress indicator if parent expects loading AND Coil is actually loading
        if (isLoadingFromParent && painter.state is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp), // Adjust size as needed
                color = MaterialTheme.colorScheme.onSurfaceVariant // Or primary
            )
        }
    }
}

// --- Helper Function ---
fun generateNewAvatarUrl(): String {
    return "$RANDOM_AVATAR_BASE_URL${System.currentTimeMillis()}"
}

// --- Previews ---
@Preview(showBackground = true, name = "Pantalla de Creación de Contacto")
@Composable
fun ContactCreationScreenPreview() {
    var previewScreenState by remember { mutableStateOf(ContactCreationState(firstName = "Menganito")) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun previewRefreshAvatar() {
        previewScreenState = previewScreenState.copy(
            isLoadingAvatar = true,
            avatarUrl = generateNewAvatarUrl()
        )
    }

    LaunchedEffect(Unit) {
        if (previewScreenState.avatarUrl.isBlank() || previewScreenState.avatarUrl == RANDOM_AVATAR_BASE_URL) {
            previewRefreshAvatar()
        }
    }

    Contacts_exampleTheme {
        ContactCreationScreen(
            initialState = previewScreenState,
            onSaveContact = { fn, ln, p, av ->
                Log.i("PreviewSave", "Guardar: $fn $ln, $p, $av")
                scope.launch {
                    snackbarHostState.showSnackbar("Contacto guardado (simulado)")
                }
                previewScreenState = ContactCreationState().copy(avatarUrl = generateNewAvatarUrl())
            },
            onCancel = { Log.i("PreviewCancel", "Cancelar") },
            snackbarHostState = snackbarHostState
        )
    }
}

@Preview(showBackground = true, name = "Avatar Cargando")
@Composable
fun AvatarImageLoadingPreview() {
    var isLoading by remember { mutableStateOf(true) }
    Contacts_exampleTheme {
        Box(modifier = Modifier.padding(16.dp).size(120.dp), contentAlignment = Alignment.Center) {
            AvatarImage(
                avatarUrl = RANDOM_AVATAR_BASE_URL, // Simulate a URL that would be loading
                isLoadingFromParent = isLoading,
                onImageClick = {},
                onCoilLoadingStateChange = { coilIsLoading -> isLoading = coilIsLoading },
                modifier = Modifier.fillMaxSize() // AvatarImage fills the Box
            )
        }
    }
}

@Preview(showBackground = true, name = "Avatar Cargado")
@Composable
fun AvatarImageLoadedPreview() {
    Contacts_exampleTheme {
        Box(modifier = Modifier.padding(16.dp).size(120.dp), contentAlignment = Alignment.Center) {
            AvatarImage(
                avatarUrl = "https://picsum.photos/id/1025/300/300", // A real static image URL
                isLoadingFromParent = false, // Explicitly false for "loaded" state
                onImageClick = {},
                onCoilLoadingStateChange = { /* No-op for this preview's purpose */ },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "Barra Superior Creación")
@Composable
fun ContactCreationTopAppBarPreview() {
    Contacts_exampleTheme {
        ContactCreationTopAppBar(title = "Nuevo Amigo", onCancel = {})
    }
}