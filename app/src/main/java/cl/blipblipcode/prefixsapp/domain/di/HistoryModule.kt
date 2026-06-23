package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.domain.useCase.history.*
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryModule {

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
