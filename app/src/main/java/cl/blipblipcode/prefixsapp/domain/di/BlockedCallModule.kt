package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.*
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class BlockedCallModule {

    @Binds
    @Singleton
    abstract fun bindGetAllBlockedCallsUseCase(
        useCase: GetAllBlockedCallsUseCase
    ): IGetAllBlockedCallsUseCase

    @Binds
    @Singleton
    abstract fun bindInsertBlockedCallUseCase(
        useCase: InsertBlockedCallUseCase
    ): IInsertBlockedCallUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteAllBlockedCallsUseCase(
        useCase: DeleteAllBlockedCallsUseCase
    ): IDeleteAllBlockedCallsUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteBlockedCallUseCase(
        useCase: DeleteBlockedCallUseCase
    ): IDeleteBlockedCallUseCase

    @Binds
    @Singleton
    abstract fun bindGetUnseenCountUseCase(
        useCase: GetUnseenCountUseCase
    ): IGetUnseenCountUseCase

    @Binds
    @Singleton
    abstract fun bindMarkAllAsSeenUseCase(
        useCase: MarkAllAsSeenUseCase
    ): IMarkAllAsSeenUseCase

    @Binds
    @Singleton
    abstract fun bindGetBlockedCallsCountUseCase(
        useCase: GetBlockedCallsCountUseCase
    ): IGetBlockedCallsCountUseCase

    @Binds
    @Singleton
    abstract fun bindGetRecentBlockedCallsUseCase(
        useCase: GetRecentBlockedCallsUseCase
    ): IGetRecentBlockedCallsUseCase
}
