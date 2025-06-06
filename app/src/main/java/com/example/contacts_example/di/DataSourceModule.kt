package com.example.contacts_example.di

import com.example.contacts_example.data.source.IContactsDataSource
import com.example.contacts_example.data.source.local.ContactDao
import com.example.contacts_example.data.source.local.ContactsLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalContactsDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    @LocalContactsDataSource // Use a qualifier if you have multiple IContactsDataSource impl
    fun provideContactsLocalDataSource(
        contactDao: ContactDao
    ): IContactsDataSource { // Return the interface
        return ContactsLocalDataSource(contactDao)
    }
}