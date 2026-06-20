package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import cl.blipblipcode.prefixsapp.domain.useCase.version.*
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [VersionModule::class]
)
abstract class TestVersionModule {

    @Binds
    @Singleton
    abstract fun bindObserveVersionStatusUseCase(
        useCase: ObserveVersionStatusUseCase
    ): IObserveVersionStatusUseCase

    @Binds
    @Singleton
    abstract fun bindCheckVersionStatusUseCase(
        useCase: CheckVersionStatusUseCase
    ): ICheckVersionStatusUseCase
}
