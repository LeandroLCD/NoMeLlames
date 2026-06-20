package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import cl.blipblipcode.prefixsapp.domain.useCase.settings.*
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SettingsModule::class]
)
abstract class TestSettingsModule {

    @Binds
    @Singleton
    abstract fun bindGetAppSettingsUseCase(
        useCase: GetAppSettingsUseCase
    ): IGetAppSettingsUseCase

    @Binds
    @Singleton
    abstract fun bindUpdatePrefixSyncUseCase(
        useCase: UpdatePrefixSyncUseCase
    ): IUpdatePrefixSyncUseCase

    @Binds
    @Singleton
    abstract fun bindGetThemeAppUseCase(
        useCase: GetThemeAppUseCase
    ): IGetThemeAppUseCase

    @Binds
    @Singleton
    abstract fun bindSetThemeAppUseCase(
        useCase: SetThemeAppUseCase
    ): ISetThemeAppUseCase
}
