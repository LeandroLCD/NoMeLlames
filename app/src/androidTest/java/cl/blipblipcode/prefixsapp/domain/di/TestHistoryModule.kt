package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import cl.blipblipcode.prefixsapp.domain.useCase.history.*
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HistoryModule::class]
)
abstract class TestHistoryModule {

    @Binds
    @Singleton
    abstract fun bindGetCallHistoryUseCase(
        useCase: GetCallHistoryUseCase
    ): IGetCallHistoryUseCase

    @Binds
    @Singleton
    abstract fun bindExportHistoryToCsvUseCase(
        useCase: ExportHistoryToCsvUseCase
    ): IExportHistoryToCsvUseCase

    @Binds
    @Singleton
    abstract fun bindClearAllHistoryUseCase(
        useCase: ClearAllHistoryUseCase
    ): IClearAllHistoryUseCase
}
