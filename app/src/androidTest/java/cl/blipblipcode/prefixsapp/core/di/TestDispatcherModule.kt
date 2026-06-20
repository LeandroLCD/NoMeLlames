package cl.blipblipcode.prefixsapp.core.di

import cl.blipblipcode.prefixsapp.di.DispatcherModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatcherModule::class]
)
class TestDispatcherModule {
    @Provides
    @Singleton
    fun providesDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun providesCoroutineScope(dispatcher: CoroutineDispatcher) =
        CoroutineScope(SupervisorJob() + dispatcher)
}