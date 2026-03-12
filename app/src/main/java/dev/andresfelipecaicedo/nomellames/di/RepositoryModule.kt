package dev.andresfelipecaicedo.nomellames.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.andresfelipecaicedo.nomellames.data.repository.AllowedCallRepositoryImpl
import dev.andresfelipecaicedo.nomellames.data.repository.AppSettingsRepositoryImpl
import dev.andresfelipecaicedo.nomellames.data.repository.BlockedCallRepositoryImpl
import dev.andresfelipecaicedo.nomellames.data.repository.PrefixRepositoryImpl
import dev.andresfelipecaicedo.nomellames.data.repository.SystemBlockedNumbersRepositoryImpl
import dev.andresfelipecaicedo.nomellames.domain.repositories.AllowedCallRepository
import dev.andresfelipecaicedo.nomellames.domain.repositories.AppSettingsRepository
import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import dev.andresfelipecaicedo.nomellames.domain.repositories.SystemBlockedNumbersRepository
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
    abstract fun bindSystemBlockedNumbersRepository(repository: SystemBlockedNumbersRepositoryImpl): SystemBlockedNumbersRepository
}
