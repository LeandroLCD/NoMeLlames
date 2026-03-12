package dev.andresfelipecaicedo.nomellames.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    @Singleton
    fun providesDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun providesCoroutineScope(dispatcher: CoroutineDispatcher) =
        CoroutineScope(SupervisorJob() + dispatcher)
}