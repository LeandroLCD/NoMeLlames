package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.*
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AllowedCallModule::class]
)
abstract class TestAllowedCallModule {

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
