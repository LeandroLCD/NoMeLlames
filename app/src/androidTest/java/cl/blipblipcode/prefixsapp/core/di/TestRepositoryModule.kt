package cl.blipblipcode.prefixsapp.core.di

import cl.blipblipcode.prefixsapp.core.fakes.FakeAllowedCallRepository
import cl.blipblipcode.prefixsapp.core.fakes.FakeAppSettingsRepository
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import cl.blipblipcode.prefixsapp.core.fakes.FakeVersionRepository
import cl.blipblipcode.prefixsapp.di.RepositoryModule
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPrefixRepository(fake: FakePrefixRepository): PrefixRepository

    @Binds
    @Singleton
    abstract fun bindBlockedCallRepository(fake: FakeBlockedCallRepository): BlockedCallRepository

    @Binds
    @Singleton
    abstract fun bindAllowedCallRepository(fake: FakeAllowedCallRepository): AllowedCallRepository

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(fake: FakeAppSettingsRepository): AppSettingsRepository

    @Binds
    @Singleton
    abstract fun bindVersionRepository(fake: FakeVersionRepository): VersionRepository
}
