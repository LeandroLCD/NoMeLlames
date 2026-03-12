package dev.andresfelipecaicedo.nomellames.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall.*
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.*
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.*
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.*
import dev.andresfelipecaicedo.nomellames.domain.useCase.settings.*
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    // BlockedCall Use Cases
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

    // AllowedCall Use Cases
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

    // History Use Cases
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

    // AppSettings Use Cases
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

    // Prefix Use Cases
    @Binds
    @Singleton
    abstract fun bindGetPrefixesUseCase(
        useCase: GetPrefixesUseCase
    ): IGetPrefixesUseCase

    @Binds
    @Singleton
    abstract fun bindGetSkipCallLogUseCase(
        useCase: GetSkipCallLogUseCase
    ): IGetSkipCallLogUseCase

    @Binds
    @Singleton
    abstract fun bindGetSkipNotificationUseCase(
        useCase: GetSkipNotificationUseCase
    ): IGetSkipNotificationUseCase

    @Binds
    @Singleton
    abstract fun bindSetSkipCallLogUseCase(
        useCase: SetSkipCallLogUseCase
    ): ISetSkipCallLogUseCase

    @Binds
    @Singleton
    abstract fun bindSetSkipNotificationUseCase(
        useCase: SetSkipNotificationUseCase
    ): ISetSkipNotificationUseCase

    @Binds
    @Singleton
    abstract fun bindAddPrefixUseCase(
        useCase: AddPrefixUseCase
    ): IAddPrefixUseCase

    @Binds
    @Singleton
    abstract fun bindRemovePrefixUseCase(
        useCase: RemovePrefixUseCase
    ): IRemovePrefixUseCase

    @Binds
    @Singleton
    abstract fun bindGetAllPrefixRulesUseCase(
        useCase: GetAllPrefixRulesUseCase
    ): IGetAllPrefixRulesUseCase

    @Binds
    @Singleton
    abstract fun bindAddPrefixRuleUseCase(
        useCase: AddPrefixRuleUseCase
    ): IAddPrefixRuleUseCase

    @Binds
    @Singleton
    abstract fun bindRemovePrefixRuleUseCase(
        useCase: RemovePrefixRuleUseCase
    ): IRemovePrefixRuleUseCase
}
