package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.domain.useCase.version.*
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class VersionModule {

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
