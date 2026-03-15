package cl.blipblipcode.prefixsapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.data.repository.AllowedCallRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repository.AppSettingsRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repository.BlockedCallRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repository.PrefixRepositoryImpl
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrefixRepository(repository: PrefixRepositoryImpl): PrefixRepository

    @Binds
    @Singleton
    abstract fun bindBlockedCallRepository(repository: BlockedCallRepositoryImpl): BlockedCallRepository

    @Binds
    @Singleton
    abstract fun bindAllowedCallRepository(repository: AllowedCallRepositoryImpl): AllowedCallRepository

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(repository: AppSettingsRepositoryImpl): AppSettingsRepository

}
