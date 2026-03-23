package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.*
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.*
import cl.blipblipcode.prefixsapp.domain.useCase.history.*
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.*
import cl.blipblipcode.prefixsapp.domain.useCase.settings.*
import cl.blipblipcode.prefixsapp.domain.useCase.version.*
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

    @Binds
    @Singleton
    abstract fun bindDeleteAllPrefixRulesUseCase(
        useCase: DeleteAllPrefixRulesUseCase
    ): IDeleteAllPrefixRulesUseCase

    // Version Use Cases
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
