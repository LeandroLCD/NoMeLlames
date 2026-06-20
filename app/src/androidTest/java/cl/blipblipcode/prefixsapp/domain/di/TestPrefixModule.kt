package cl.blipblipcode.prefixsapp.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.*
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PrefixModule::class]
)
abstract class TestPrefixModule {

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

    @Binds
    @Singleton
    abstract fun bindMatchPrefixRuleUseCase(
        useCase: MatchPrefixRuleUseCase
    ): IMatchPrefixRuleUseCase

    @Binds
    @Singleton
    abstract fun bindNormalizePhoneNumberUseCase(
        useCase: NormalizePhoneNumberUseCase
    ): INormalizePhoneNumberUseCase

    @Binds
    @Singleton
    abstract fun bindGetCachedPrefixRulesUseCase(
        useCase: GetCachedPrefixRulesUseCase
    ): IGetCachedPrefixRulesUseCase
}
