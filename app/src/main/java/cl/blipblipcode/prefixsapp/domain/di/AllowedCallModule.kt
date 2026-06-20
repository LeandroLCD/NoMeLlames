package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.*
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AllowedCallModule {

    @Binds
    @Singleton
    abstract fun bindGetAllowedCallsCountUseCase(
        useCase: GetAllowedCallsCountUseCase
    ): IGetAllowedCallsCountUseCase

    @Binds
    @Singleton
    abstract fun bindInsertAllowedCallUseCase(
        useCase: InsertAllowedCallUseCase
    ): IInsertAllowedCallUseCase
}
