package cl.blipblipcode.prefixsapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.data.repositories.AllowedCallRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repositories.AppSettingsRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repositories.BlockedCallRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repositories.PrefixRepositoryImpl
import cl.blipblipcode.prefixsapp.data.repositories.VersionRepositoryImpl
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
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

    @Binds
    @Singleton
    abstract fun bindVersionRepository(repository: VersionRepositoryImpl): VersionRepository

}
