package com.example.contacts_example.ui.components.creation

import androidx.compose.runtime.Immutable

@Immutable
data class ContactCreationState(
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val isLoadingAvatar: Boolean = false,
)