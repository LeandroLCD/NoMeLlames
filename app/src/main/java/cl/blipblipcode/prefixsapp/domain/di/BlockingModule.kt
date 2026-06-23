package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.*
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class BlockingModule {

    @Binds
    @Singleton
    abstract fun bindGetBlockPrivateNumbersUseCase(
        useCase: GetBlockPrivateNumbersUseCase
    ): IGetBlockPrivateNumbersUseCase

    @Binds
    @Singleton
    abstract fun bindGetBlockNonContactsUseCase(
        useCase: GetBlockNonContactsUseCase
    ): IGetBlockNonContactsUseCase

    @Binds
    @Singleton
    abstract fun bindSetBlockPrivateNumbersUseCase(
        useCase: SetBlockPrivateNumbersUseCase
    ): ISetBlockPrivateNumbersUseCase

    @Binds
    @Singleton
    abstract fun bindSetBlockNonContactsUseCase(
        useCase: SetBlockNonContactsUseCase
    ): ISetBlockNonContactsUseCase
}